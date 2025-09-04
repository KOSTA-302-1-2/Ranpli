package user.controller;

import java.util.List;

import playlist.dto.PlaylistDetailDTO;
import user.dto.UserDTO;
import user.exception.PlayListNullException;
import user.exception.UserInvalidException;
import user.exception.UserNotFoundException;
import user.service.UserService;
import user.session.Session;
import user.session.SessionSet;

public class UserController {
	static UserService userService = new UserService();

	public static void account(String userId, String userPwd) {
		try {
			userService.account(userId, userPwd);
		} catch (UserInvalidException e) {
			e.printStackTrace();
		}
	}
	
	public static void login(String userId, String userPwd) {
		try {
			UserDTO user = userService.login(userId, userPwd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void logout(String userId) {
		Session session = new Session(userId);
		SessionSet ss = SessionSet.getInstance();
		ss.removeFromSet(session);
	}
	
	public static void getPlayList(String userId) {
		List<PlaylistDetailDTO> playList = userService.getPlayList(userId);
		if (playList == null) {
			// do something error task
		}
		
		else {
			for (PlaylistDetailDTO pdd : playList) {
				System.out.println(pdd);
			}
		}
	}
}