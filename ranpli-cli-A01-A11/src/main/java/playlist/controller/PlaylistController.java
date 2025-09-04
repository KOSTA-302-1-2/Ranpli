package playlist.controller;

import playlist.service.PlaylistService;

public class PlaylistController {
	private static PlaylistService playlistService = new PlaylistService();
	
	public static void saveMusicToPlaylist (String userId, int musicNo) {
		try {
			playlistService.saveMusicToPlaylist(userId, musicNo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
