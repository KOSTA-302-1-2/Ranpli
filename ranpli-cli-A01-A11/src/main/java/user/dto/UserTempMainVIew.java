package user.dto;

import java.util.Scanner;

import playlist.controller.PlaylistController;
import user.controller.UserController;
import user.session.SessionSet;

public class UserTempMainVIew {
	String userId = null;
	String userPwd = null;
	public UserTempMainVIew() {
		Scanner sc = new Scanner(System.in);
		
		// 회원가입 테스트
//		System.out.println("회원가입 아이디 : ");
//		userId = sc.nextLine();
//
//		System.out.println("회원가입 비밀번호 : ");
//		userPwd = sc.nextLine();
//		
//		UserController.account(userId, userPwd);
		
		// 로그인 테스트
		System.out.println("로그인 아이디 : ");
		userId = sc.nextLine();

		System.out.println("로그인 비밀번호 : ");
		userPwd = sc.nextLine();
		
		UserController.login(userId, userPwd);
		System.out.println(SessionSet.getInstance().getSet());
		
		// 플레이 리스트 테스트
		UserController.getPlayList(userId);
		
		// 플레이 리스트에 음악 추가 테스트
		PlaylistController.saveMusicToPlaylist(userId, 15);
		
		// 로그아웃 테스트
		UserController.logout(userId);
		
		sc.close();
	}
	
	public static void main(String[] args) {
		new UserTempMainVIew();
	}
}