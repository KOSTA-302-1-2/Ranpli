package user.controller;

import java.util.List;

import user.Exception.PlayListNullException;
import user.Exception.UserNotFoundException;
import user.dto.PlaylistDetailDTO;
import user.dto.UserDTO;

import user.service.UserService;
import user.session.Session;
import user.session.SessionSet;

public class UserController {
	static UserService userService = new UserService();

	public static void account(String userId, String userPwd) {
		try {
			userService.account(userId, userPwd);
		} catch (UserNotFoundException e) {
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