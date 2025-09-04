package playlist.service;

import java.sql.SQLException;
import playlist.Exception.DuplicateMusicException;

import playlist.dao.PlaylistDAO;
import playlist.dao.PlaylistDAOImpl;

public class PlaylistService {
	private PlaylistDAO playlistDao = new PlaylistDAOImpl();
	
	public void saveMusicToPlaylist (String userId, int musicNo) throws DuplicateMusicException, SQLException {
		try {
			playlistDao.saveMusicToPlaylist(userId, musicNo);
		} catch (Exception e) {
			System.out.println("음악 추가에 문제가 생겼습니다.");
			e.printStackTrace();
		}
	}
}
