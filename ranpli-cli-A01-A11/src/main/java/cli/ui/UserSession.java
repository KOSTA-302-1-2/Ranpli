package cli.ui;

import user.dto.UserDTO;

public class UserSession {
    private boolean loggedIn = false;
    private String  nickname = "***";
    private UserDTO user = null;
    private int usersNo = -1;
    private int playlistNo = -1;
    private String userId;


    public boolean isLoggedIn() { return loggedIn; }
    public void setLoggedIn(boolean v) { loggedIn = v; }

    public String getNickname() { return nickname; }
    public void setNickname(String v) { nickname = v; }

    public UserDTO getUser() { return user; }
    public void setUser(UserDTO user) { this.user = user; }

    // ─────────────────────────────
    // UserDTO 위임 게터
    // ─────────────────────────────
    public Integer getUserNo() {
        return (user != null) ? user.getUserNO() : null;
    }

    public String getUserId() {
        return (user != null) ? user.getUserId() : null;
    }

    // Null 방지 헬퍼
    public int getUserNoOrZero() {
        return (user != null) ? user.getUserNO() : 0;
    }
}
