package cli.ui;

import cli.ui.UserSession;
import cli.ui.*;

import java.util.Scanner;

public class ProfileView implements Screen {
    @Override
    public ViewId render(UserSession session, Scanner sc) {
        Layout.header("프로필");

        if (!session.isLoggedIn()) {
            System.out.println("(미로그인) 프로필은 로그인 후 이용할 수 있습니다.");
            System.out.println("\n[Enter] 메인으로");
            sc.nextLine();
            return ViewId.MAIN_MENU;
        }

        System.out.println("닉네임 : " + session.getNickname());
        System.out.println("가입일 : 0000-00-00");
        System.out.println();
        System.out.println("플레이리스트 (더미)");
        System.out.println(" - #1 Love wins all / IU");
        System.out.println(" - #2 Blueming / IU");
        System.out.println(" - #3 Super Shy / NewJeans");
        System.out.println();
        System.out.println("[B] 뒤로   [L] 로그아웃(데모)");
        System.out.print("> ");

        String in = sc.nextLine().trim().toLowerCase();
        if ("l".equals(in)) {
            session.setLoggedIn(false); // 데모 로그아웃
        }
        return ViewId.MAIN_MENU;
    }
}
