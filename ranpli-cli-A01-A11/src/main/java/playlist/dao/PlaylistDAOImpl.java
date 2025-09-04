package playlist.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import app.db.Db;
import playlist.Exception.DuplicateMusicException;
import playlist.dto.PlaylistDetailDTO;

import user.dto.UserDTO;
import user.session.Session;
import user.session.SessionSet;

public class PlaylistDAOImpl implements PlaylistDAO {
	private final static int PLAYLIST_LIMIT = 10;
	
	private SessionSet ss = null;
	private Session session = null;
	
	private UserDTO user;

	private boolean isDuplicateMusic(int musicNo) {
		List<PlaylistDetailDTO> playList = user.getPlaylistDetailList();
		
		for (PlaylistDetailDTO pdd : playList) {
			if (pdd.getMusicNo() == musicNo) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isCountOverPlaylistLimit() {
		List<PlaylistDetailDTO> playList = user.getPlaylistDetailList();
		
		return playList.size() >= PLAYLIST_LIMIT ? true : false;
	}
	
	private int findOldestMusicNo () throws SQLException {
		List<PlaylistDetailDTO> playList = user.getPlaylistDetailList();
		
		PlaylistDetailDTO toDeleteMusic = playList.stream().min((a, b) -> {
			return a.getMusicSaveTime().compareTo(b.getMusicSaveTime());
		}).orElse(null);
		
		return toDeleteMusic.getMusicNo();
	}
	
	private void deleteMusicFromPlaylist(Connection con, int musicNo) throws SQLException {
		PreparedStatement ps = null;
		
		int result = 0;
		
		String sql = "delete from tb_playlist_detail where music_no = ?";
		
		try {
			ps = con.prepareStatement(sql);
			ps.setInt(1, findOldestMusicNo());
			
			result = ps.executeUpdate();
			
			if (result == 0) {
				throw new SQLException("음악 삭제에 실패했습니다.");
			}
		} catch (Exception e) {
			con.rollback();
			e.printStackTrace();
		} finally {
			Db.releaseConnection(null, ps);
		}
	}
	
	@Override
	public void saveMusicToPlaylist(String userId, int musicNo) throws DuplicateMusicException, SQLException {
		this.ss = SessionSet.getInstance();
		this.session = ss.get(userId);
		this.user = (UserDTO) session.getAttribute("user");
		
		Connection con = null;
		PreparedStatement ps = null;
	
		if (isDuplicateMusic(musicNo)) {
			throw new DuplicateMusicException("중복된 음악이 존재합니다.");
		}
			
		int result = 0;
		
		String sql = "insert into tb_playlist_detail (playlist_no, music_no, music_save_time) values (?, ?, now())";
		
		try {
			con = Db.getConnection();
			con.setAutoCommit(false);
			
			if (isCountOverPlaylistLimit()) {
				deleteMusicFromPlaylist(con, musicNo);
			}
			
			ps = con.prepareStatement(sql);
			ps.setInt(1, user.getUserNO());
			ps.setInt(2 , musicNo);
			
			result = ps.executeUpdate();
			
			if (result == 0) {
				throw new SQLException();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			con.commit();
			Db.releaseConnection(con, ps);
		}
	}
}
