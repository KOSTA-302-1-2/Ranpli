package user.controller;

import user.Exception.UserNotFoundException;
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
			System.out.println(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void logout(String userId) {
		Session session = new Session(userId);
		SessionSet ss = SessionSet.getInstance();
		ss.removeFromSet(session);
	}
}
