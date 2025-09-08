package cli.ui;

import java.util.List;
import java.util.Scanner;

import playlist.dao.PlaylistDAO;
import playlist.dao.PlaylistDAOImpl;
import playlist.dto.PlaylistDetailDTO;

public class ProfileView implements Screen {
    @Override
    public ViewId render(UserSession session, Scanner sc) {
        Layout.header("                                                            프로필");

        if (!session.isLoggedIn()) {
            System.out.println("(미로그인) 프로필은 로그인 후 이용할 수 있습니다.");
            System.out.println("\n[Enter] 메인으로");
            sc.nextLine();
            return ViewId.MAIN_MENU;
        }

        String userId = session.getUser().getUserId();

        // ★ 저장 목록 항상 최신 조회
        List<PlaylistDetailDTO> musicList;
        try {
            PlaylistDAO dao = new PlaylistDAOImpl();
            musicList = dao.findSavedByUserId(userId);
        } catch (Exception e) {
            System.out.println("저장 목록을 불러오지 못했습니다.");
            e.printStackTrace();
            musicList = java.util.Collections.emptyList();
        }

        System.out.println("사용자: " + session.getUser().getUserId());
        System.out.println("저장한 노래: " + musicList.size() + "곡");
        System.out.println();

        for (int i = 0; i < musicList.size(); i++) {
            System.out.print(" - #" + (i + 1) + " ");
            System.out.println(musicList.get(i));
        }
        System.out.println();
        System.out.println("[B] 뒤로   [L] 로그아웃");
        System.out.print("> ");

        String in = sc.nextLine().trim().toLowerCase();
        if ("l".equals(in)) {
            session.setLoggedIn(false);
        }
        return ViewId.MAIN_MENU;
    }
}
