package playlist.dao;

import java.sql.Connection;
import java.sql.SQLException;

import playlist.Exception.DuplicateMusicException;


public interface PlaylistDAO {
	/**
	 * 현재 음악을 플레이리스트에 저장
	 */
	void saveMusicToPlaylist (String userId, int musicNo)
			throws DuplicateMusicException,
						SQLException;
}
