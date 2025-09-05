package cli.ui;

import cli.ui.UserSession;
import cli.ui.*;
import cli.ui.MainMenuView;
import cli.ui.RandomPlayView;
// (옵션) Login/Signup/Profile/Search 추가 시 import

import java.util.*;

public class ViewRouter {
    private final Map<ViewId, Screen> screens = new HashMap<>();
    private final UserSession session;

    public ViewRouter(UserSession session) {
        this.session = session;
        screens.put(ViewId.MAIN_MENU, new MainMenuView());
        screens.put(ViewId.RANDOM,    new RandomPlayView());
        screens.put(ViewId.LOGIN,  new LoginView());
        screens.put(ViewId.SIGNUP, new SignupView());
        screens.put(ViewId.PROFILE,new ProfileView());
        //screens.put(ViewId.SEARCH, new SearchView());
    }

    public void run() {
        var sc = new Scanner(System.in);
        ViewId cur = ViewId.MAIN_MENU;

        while (cur != ViewId.EXIT) {
            var s = screens.getOrDefault(cur, screens.get(ViewId.MAIN_MENU));
            cur = s.render(session, sc);
        }
        System.out.println("프로그램을 종료합니다.");
    }
}
