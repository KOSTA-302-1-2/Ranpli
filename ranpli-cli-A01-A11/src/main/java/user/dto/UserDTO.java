package user.dto;

import java.util.ArrayList;
import java.util.List;

import playlist.dto.PlaylistDetailDTO;

public class UserDTO {
	private int userNO;
	private String userId;
	private String userPwd;
	private String userRegDate;
	private String userDeleteDate;
	private List<PlaylistDetailDTO> playlistDetailList = new ArrayList<>();
	
	public UserDTO(int userNO, String userId, String userPwd, String userRegDate) {
		this.userNO = userNO;
		this.userId = userId;
		this.userPwd = userPwd;
		this.userRegDate = userRegDate;
	}
	
	public int getUserNO() {
		return userNO;
	}

	public void setUserNO(int userNO) {
		this.userNO = userNO;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserPwd() {
		return userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public String getUserRegDate() {
		return userRegDate;
	}

	public void setUserRegDate(String userRegDate) {
		this.userRegDate = userRegDate;
	}

	public String getUserDeleteDate() {
		return userDeleteDate;
	}

	public void setUserDeleteDate(String userDeleteDate) {
		this.userDeleteDate = userDeleteDate;
	}

	public List<PlaylistDetailDTO> getPlaylistDetailList () {
		return playlistDetailList ;
	}
	
	public void setPlaylistDetailList (List<PlaylistDetailDTO> playlistDetailList) {
		this.playlistDetailList = playlistDetailList;
	}
	
	public void addPlaylistDetailList (PlaylistDetailDTO playListDetail) {
		this.playlistDetailList.add(playListDetail);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MemberDTO [userNO=");
		builder.append(userNO);
		builder.append(", userId=");
		builder.append(userId);
		builder.append(", userPwd=");
		builder.append(userPwd);
		builder.append(", userRegDate=");
		builder.append(userRegDate);
		builder.append(", userDeleteDate=");
		builder.append(userDeleteDate);
		builder.append("]");
		return builder.toString();
	}
}
