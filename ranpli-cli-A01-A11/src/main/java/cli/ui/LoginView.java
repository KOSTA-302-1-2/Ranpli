package cli.ui;

import cli.ui.UserSession;
import cli.ui.*;

import java.util.Scanner;

public class LoginView implements Screen {
    /** 데모 모드: true면 로그인 성공처럼 보이게 in-memory로만 세션 변경 */
    private static final boolean DEMO_MODE = true;

    @Override
    public ViewId render(UserSession session, Scanner sc) {
    	
        Layout.header("로그인");

        System.out.println("아이디");
        System.out.println("────────────────────────────────────────");
        System.out.print("> ");
        String id = sc.nextLine().trim();
        if (id.equalsIgnoreCase("b")) return ViewId.MAIN_MENU;

        System.out.println();
        System.out.println("비밀번호");
        System.out.println("────────────────────────────────────────");
        System.out.print("> ");
        String pw = sc.nextLine().trim();
        if (pw.equalsIgnoreCase("b")) return ViewId.MAIN_MENU;

        System.out.println();
        System.out.println("버튼: [Enter] 로그인   [B] 취소");
        System.out.print("> ");
        String cmd = sc.nextLine().trim().toLowerCase();
        if ("b".equals(cmd)) return ViewId.MAIN_MENU;

        if (DEMO_MODE) {
            session.setLoggedIn(true);
            session.setNickname(id.isEmpty() ? "***" : id);
            System.out.println("\n(데모) 로그인 성공! 메인으로 이동합니다...");
        } else {
            System.out.println("\n(Stub) 실제 로그인 미구현. 메인으로 이동합니다...");
        }
        try { Thread.sleep(700); } catch (Exception ignore) {}
        return ViewId.MAIN_MENU;
    }
}
