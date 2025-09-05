package cli.ui;

import cli.ui.UserSession;
import cli.ui.*;
import java.util.Scanner;

public class MainMenuView implements Screen {
	
	   @Override
	    public ViewId render(UserSession session, Scanner sc) {
	        Layout.header("RANPLI • MENU");
	        if (!session.isLoggedIn()) {
	            System.out.println("[A] 회원가입   [L] 로그인   [R] 랜플리   [S] 검색   [Q] 종료");
	        } else {
	            System.out.println("[R] 랜플리   [S] 검색   [P] 프로필   [Q] 종료");
	        }
	        System.out.print("> ");
	        String in = sc.nextLine().trim().toLowerCase();

	       
	        ViewId next;
	        if (!session.isLoggedIn()) {
	            switch (in) {
	                case "a" -> next = ViewId.SIGNUP;
	                case "l" -> next = ViewId.LOGIN;
	                case "s" -> next = ViewId.SEARCH;
	                case "r" -> next = ViewId.RANDOM;
	                case "q" -> next = ViewId.EXIT;
	                default  -> next = ViewId.MAIN_MENU;
	            }
	        } else {
	            switch (in) {
	                case "p" -> next = ViewId.PROFILE;
	                case "s" -> next = ViewId.SEARCH;
	                case "r" -> next = ViewId.RANDOM;
	                case "q" -> next = ViewId.EXIT;
	                default  -> next = ViewId.MAIN_MENU;
	            }
	        }

	        
	        return next;
	    }
	}