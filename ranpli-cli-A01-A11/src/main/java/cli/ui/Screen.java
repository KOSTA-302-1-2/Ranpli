package cli.ui;

import cli.ui.UserSession;
import java.util.Scanner;

public interface Screen {
    ViewId render(UserSession session, Scanner sc);
}