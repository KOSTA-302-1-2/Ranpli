package cli.ui;

public class UserSession {
    private boolean loggedIn = false;
    private String  nickname = "***";

    public boolean isLoggedIn() { return loggedIn; }
    public void setLoggedIn(boolean v) { loggedIn = v; }

    public String getNickname() { return nickname; }
    public void setNickname(String v) { nickname = v; }
}
