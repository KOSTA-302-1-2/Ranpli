package user.dto;

import java.util.Scanner;

import user.controller.UserController;
import user.session.SessionSet;

public class UserTempMainVIew {

	public UserTempMainVIew() {
		Scanner sc = new Scanner(System.in);
		
		System.out.println("회원가입 아이디 : ");
		String userId = sc.nextLine();

		System.out.println("회원가입 비밀번호 : ");
		String userPwd = sc.nextLine();
		
		UserController.account(userId, userPwd);
		
		System.out.println("로그인 아이디 : ");
		userId = sc.nextLine();

		System.out.println("로그인 비밀번호 : ");
		userPwd = sc.nextLine();
		
		UserController.login(userId, userPwd);
		System.out.println(SessionSet.getInstance().getSet());
		
		UserController.logout(userId);
		
		sc.close();
	}
	
	public static void main(String[] args) {
		new UserTempMainVIew();
	}
}