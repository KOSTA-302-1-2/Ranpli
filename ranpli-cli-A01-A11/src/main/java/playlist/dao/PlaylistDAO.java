package playlist.dao;

import java.sql.SQLException;
import java.util.List;

import playlist.dto.PlaylistDetailDTO;
import playlist.exception.DuplicateMusicException;

public interface PlaylistDAO {
    /** 현재 음악을 플레이리스트에 저장 */
    void saveMusicToPlaylist(String userId, int musicNo)
            throws DuplicateMusicException, SQLException;

    /** 사용자 아이디 기준으로 저장한 음악 목록 조회 (프로필뷰 표시용) */
    List<PlaylistDetailDTO> findSavedByUserId(String userId) throws SQLException;
}
