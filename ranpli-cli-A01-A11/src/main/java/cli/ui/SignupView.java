package cli.ui;

import cli.ui.UserSession;
import cli.ui.*;

import java.util.Scanner;

public class SignupView implements Screen {
    private static final boolean DEMO_MODE = true;

    @Override
    public ViewId render(UserSession session, Scanner sc) {
        Layout.header("회원가입");

        System.out.println("아이디");
        System.out.println("────────────────────────────────────────");
        System.out.print("> ");
        String id = sc.nextLine().trim();
        if (id.equalsIgnoreCase("b")) return ViewId.MAIN_MENU;

        System.out.println();
        System.out.println("비밀번호 (20자 이하)");
        System.out.println("────────────────────────────────────────");
        System.out.print("> ");
        String pw = sc.nextLine().trim();
        if (pw.equalsIgnoreCase("b")) return ViewId.MAIN_MENU;

        System.out.println();
        System.out.println("버튼: [Enter] 가입하기   [B] 취소");
        System.out.print("> ");
        String cmd = sc.nextLine().trim().toLowerCase();
        if ("b".equals(cmd)) return ViewId.MAIN_MENU;

        if (DEMO_MODE) {
            System.out.println("\n(데모) 가입 완료! 자동 로그인 상태로 메인으로 이동합니다...");
            session.setLoggedIn(true);
            session.setNickname(id.isEmpty() ? "***" : id);
        } else {
            System.out.println("\n(Stub) 실제 가입 미구현. 메인으로 이동합니다...");
        }
        try { Thread.sleep(900); } catch (Exception ignore) {}
        return ViewId.MAIN_MENU;
    }
}
