package playlist.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import app.db.Db;
import playlist.dto.PlaylistDetailDTO;
import playlist.exception.DuplicateMusicException;
import user.dto.UserDTO;
import user.session.Session;
import user.session.SessionSet;

/**
 * 하이브리드 구현
 * - (세션 캐시) 빠른 중복/개수 추정 → UX 메시지용
 * - (DB 트랜잭션) users_id→users_no→playlist_no 조회, 중복/한도/삭제/저장 모두 DB에서 최종 판단
 * - 10곡 초과 시, 해당 사용자의 플레이리스트에서 "가장 오래된 1곡" 삭제 후 INSERT
 */
public class PlaylistDAOImpl implements PlaylistDAO {

    private static final int PLAYLIST_LIMIT = 10;

    // ── 세션 캐시(옵션)
    private SessionSet ss;
    private Session session;
    private UserDTO user;

    /** 세션 캐시로 빠른 중복 체크(최종 판단은 DB에서 재확인) */
    private boolean isDuplicateInSession(int musicNo) {
        if (user == null || user.getPlaylistDetailList() == null) return false;
        for (PlaylistDetailDTO pdd : user.getPlaylistDetailList()) {
            if (pdd.getMusicNo() == musicNo) return true;
        }
        return false;
    }

    // ─────────────────────────────────────────────────────────────
    // 저장: 중복 검사 → 한도 초과 시 가장 오래된 1곡 삭제 → INSERT (모두 트랜잭션)
    // ─────────────────────────────────────────────────────────────
    @Override
    public void saveMusicToPlaylist(String userId, int musicNo)
            throws DuplicateMusicException, SQLException {

        // 세션 캐시 준비(없으면 null). UX를 위해 빠르게 중복 차단 시도.
        ss = SessionSet.getInstance();
        session = (ss != null) ? ss.get(userId) : null;
        user = (session != null) ? (UserDTO) session.getAttribute("user") : null;

        if (isDuplicateInSession(musicNo)) {
            // 세션 캐시 기준으로도 이미 있음 → 빠른 응답
            throw new DuplicateMusicException("이미 저장한 곡입니다. (세션 캐시)");
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        final String Q_USER_NO     = "SELECT users_no FROM tb_users WHERE users_id = ?";
        final String Q_PLAYLIST_NO = "SELECT playlist_no FROM tb_playlist WHERE users_no = ?";
        final String Q_DUP         = "SELECT 1 FROM tb_playlist_detail WHERE playlist_no = ? AND music_no = ? LIMIT 1";
        final String Q_CNT         = "SELECT COUNT(*) FROM tb_playlist_detail WHERE playlist_no = ?";
        final String Q_OLDEST_ID   = "SELECT playlist_detail_no FROM tb_playlist_detail " +
                                     "WHERE playlist_no = ? ORDER BY music_save_time ASC, playlist_detail_no ASC LIMIT 1";
        final String DEL_BY_ID     = "DELETE FROM tb_playlist_detail WHERE playlist_detail_no = ?";
        final String INS_DETAIL    = "INSERT INTO tb_playlist_detail (playlist_no, music_no, music_save_time) VALUES (?, ?, NOW())";

        try {
            con = Db.getConnection();
            con.setAutoCommit(false);

            // 1) users_no
            int usersNo = 0;
            ps = con.prepareStatement(Q_USER_NO);
            ps.setString(1, userId);
            rs = ps.executeQuery();
            if (rs.next()) usersNo = rs.getInt(1);
            else throw new SQLException("사용자를 찾을 수 없음: " + userId);
            rs.close(); ps.close();

            // 2) playlist_no
            int playlistNo = 0;
            ps = con.prepareStatement(Q_PLAYLIST_NO);
            ps.setInt(1, usersNo);
            rs = ps.executeQuery();
            if (rs.next()) playlistNo = rs.getInt(1);
            else throw new SQLException("해당 사용자 플레이리스트가 없음: " + userId);
            rs.close(); ps.close();

            // 3) DB 중복 확인(권위 소스)
            ps = con.prepareStatement(Q_DUP);
            ps.setInt(1, playlistNo);
            ps.setInt(2, musicNo);
            rs = ps.executeQuery();
            if (rs.next()) {
                throw new DuplicateMusicException("이미 저장된 곡입니다.");
            }
            rs.close(); ps.close();

            // 4) 곡 수 확인 → 한도(10) 초과면 가장 오래된 1곡 삭제
            int cnt = 0;
            ps = con.prepareStatement(Q_CNT);
            ps.setInt(1, playlistNo);
            rs = ps.executeQuery();
            if (rs.next()) cnt = rs.getInt(1);
            rs.close(); ps.close();

            if (cnt >= PLAYLIST_LIMIT) {
                Integer oldestDetailNo = null;
                ps = con.prepareStatement(Q_OLDEST_ID);
                ps.setInt(1, playlistNo);
                rs = ps.executeQuery();
                if (rs.next()) oldestDetailNo = rs.getInt(1);
                rs.close(); ps.close();

                if (oldestDetailNo != null) {
                    ps = con.prepareStatement(DEL_BY_ID);
                    ps.setInt(1, oldestDetailNo);
                    int del = ps.executeUpdate();
                    if (del == 0) throw new SQLException("가장 오래된 곡 삭제 실패");
                    ps.close();
                }
            }

            // 5) INSERT
            ps = con.prepareStatement(INS_DETAIL);
            ps.setInt(1, playlistNo);
            ps.setInt(2, musicNo);
            int updated = ps.executeUpdate();
            if (updated == 0) throw new SQLException("플레이리스트 저장 실패");
            ps.close();

            // 6) 커밋
            con.commit();

            // (옵션) 세션 캐시 동기화는 서비스 레이어에서 재조회로 갱신 권장

        } catch (DuplicateMusicException e) {
            if (con != null) con.rollback();
            throw e;
        } catch (SQLException e) {
            if (con != null) con.rollback();
            throw e;
        } catch (Exception e) {
            if (con != null) con.rollback();
            throw new SQLException("플레이리스트 저장 중 오류: " + e.getMessage(), e);
        } finally {
            Db.releaseConnection(con, ps, rs);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 목록 조회: 프로필뷰 표시용
    // ─────────────────────────────────────────────────────────────
    @Override
    public List<PlaylistDetailDTO> findSavedByUserId(String userId) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        final String Q =
            "SELECT pld.playlist_detail_no, u.users_no, pld.music_save_time, " +
            "       m.music_no, m.music_title, m.music_artist, m.music_album, m.music_url " +
            "  FROM tb_users u " +
            "  JOIN tb_playlist p ON p.users_no = u.users_no " +
            "  JOIN tb_playlist_detail pld ON pld.playlist_no = p.playlist_no " +
            "  JOIN tb_music m ON m.music_no = pld.music_no " +
            " WHERE u.users_id = ? " +
            " ORDER BY pld.playlist_detail_no DESC";

        List<PlaylistDetailDTO> list = new ArrayList<>();
        try {
            con = Db.getConnection();
            ps = con.prepareStatement(Q);
            ps.setString(1, userId);
            rs = ps.executeQuery();

            while (rs.next()) {
                PlaylistDetailDTO d = new PlaylistDetailDTO(0, 0, 0, Q, Q, Q, Q, 0, Q); // 기본 생성자 + 세터 사용
                d.setPlaylistDetailNo(rs.getInt("playlist_detail_no"));
                d.setUserNo(rs.getInt("users_no"));
                d.setMusicSaveTime(rs.getString("music_save_time"));
                d.setMusicNo(rs.getInt("music_no"));            // DTO가 String이면 getString(...)으로 변경
                d.setMusicTitle(rs.getString("music_title"));
                d.setMusicArtist(rs.getString("music_artist"));
                d.setMusicAlbum(rs.getString("music_album"));
                d.setMusicUrl(rs.getString("music_url"));
                list.add(d);
            }
        } finally {
            Db.releaseConnection(con, ps, rs);
        }
        return list;
    }
}
