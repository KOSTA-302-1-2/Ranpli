package cli.ui;

import java.util.List;
import java.util.Scanner;

import playlist.dto.PlaylistDetailDTO;

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
        System.out.println("가입일 : " + session.getUser().getUserRegDate());
        System.out.println();
        System.out.println("플레이리스트");
        List<PlaylistDetailDTO> musicList = session.getUser().getPlaylistDetailList();
        for (int i = 0; i < musicList.size(); i++) {
        		System.out.print(" - #" + (i+1) + " ");
        		System.out.println(musicList.get(i));
        }
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
