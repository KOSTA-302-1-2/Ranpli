package cli.ui;

import cli.ui.UserSession;
import cli.ui.Layout;
import cli.ui.ViewId;

import app.music.PlayerController;
import app.music.RandomEngine;
import app.repo.TrackDao;

import java.util.Scanner;

public class RandomPlayView implements cli.ui.Screen {

    private final RandomEngine random;
    private final PlayerController player;

    public RandomPlayView() {
        var dao = new TrackDao();
        this.random  = new RandomEngine(dao);
        this.player  = new PlayerController();
    }

    @Override
    public ViewId render(UserSession session, Scanner sc) {
        Layout.header("                                                            Random Play");

        var track = random.pickOneAndPersist();
        if (track == null) {
            System.out.println("랜덤 선택 실패. 메인으로 돌아갑니다.");
            try { Thread.sleep(900); } catch (Exception ignore) {}
            return ViewId.MAIN_MENU;
        }

        // 재생 시작
        player.play(track);

        try {
            // ✅ NowPlayingSession 시그니처 변경에 맞춰 4개 인자 전달
            //    - session: 저장할 때 사용자 식별자 사용
            //    - currentMusicNoSupplier: 현재 곡의 DB music_no 제공
            cli.ui.MainView.NowPlayingSession.runJLine(
                player,
                random,
                session,
                () -> { // 현재 곡 music_no 추출(Track에 musicNo()가 없으면 0 반환)
                    var cur = player.current();
                    try { return (cur != null) ? cur.musicNo() : 0; }
                    catch (Throwable ignore) { return 0; }
                }
            );
        } catch (Throwable t) {
            t.printStackTrace(); // 문제가 있어도 메인으로 복귀
        }

        return ViewId.MAIN_MENU;
    }
}