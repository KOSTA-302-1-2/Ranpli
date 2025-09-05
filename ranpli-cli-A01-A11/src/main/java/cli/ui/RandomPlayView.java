package cli.ui;

import cli.ui.UserSession;
import cli.ui.*;
import app.music.PlayerController;
import app.music.RandomEngine;
import app.repo.TrackDao;

import java.util.Scanner;

public class RandomPlayView implements Screen {

    private final RandomEngine random;
    private final PlayerController player;

    public RandomPlayView() {
        var dao = new TrackDao();
        this.random = new RandomEngine(dao);
        this.player = new PlayerController();
    }

    @Override
    public ViewId render(UserSession session, Scanner sc) {
        Layout.header("랜덤 재생");

        var track = random.pickOneAndPersist();
        if (track == null) {
            System.out.println("랜덤 선택 실패. 메인으로 돌아갑니다.");
            try { Thread.sleep(900); } catch (Exception ignore) {}
            return ViewId.MAIN_MENU;
        }

    
        player.play(track);

  
        try {
            cli.ui.MainView.NowPlayingSession.runJLine(player, random);
        } catch (Throwable t) {
            // 혹시 접근제어자 문제 시 에러 메시지라도 보고 메인으로 복귀
            t.printStackTrace();
        }

        // 재생 세션 종료 후 메인으로 복귀
        return ViewId.MAIN_MENU;
    }
}
