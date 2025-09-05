package cli.ui;

import user.dto.UserDTO;

public class UserSession {
    private boolean loggedIn = false;
    private String  nickname = "***";
    private UserDTO user = null;

    public boolean isLoggedIn() { return loggedIn; }
    public void setLoggedIn(boolean v) { loggedIn = v; }

    public String getNickname() { return nickname; }
    public void setNickname(String v) { nickname = v; }
	public UserDTO getUser() {
		return user;
	}
	public void setUser(UserDTO user) {
		this.user = user;
	}
}
