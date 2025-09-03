package user.dto;

public class PlaylistDetailDTO {
	// tb_playlist_detail 속성
	private int playlistDetailNo;
	private int UserNo;
	private int musicSaveTime;
	
	// tb_music 속성
	private int musicNo;
	private int isBeforeMusic;
	
	private String musicTitle;
	private String musicArtist;
	private String musicAlbum;
	private String musicUrl;
	
	public int getPlaylistDetailNo() {
		return playlistDetailNo;
	}
	
	public void setPlaylistDetailNo(int playlistDetailNo) {
		this.playlistDetailNo = playlistDetailNo;
	}
	
	public int getUserNo() {
		return UserNo;
	}
	
	public void setUserNo(int userNo) {
		UserNo = userNo;
	}
	
	public int getMusicSaveTime() {
		return musicSaveTime;
	}
	
	public void setMusicSaveTime(int musicSaveTime) {
		this.musicSaveTime = musicSaveTime;
	}
	
	public int getMusicNo() {
		return musicNo;
	}
	
	public void setMusicNo(int musicNo) {
		this.musicNo = musicNo;
	}
	
	public int getIsBeforeMusic() {
		return isBeforeMusic;
	}
	
	public void setIsBeforeMusic(int isBeforeMusic) {
		this.isBeforeMusic = isBeforeMusic;
	}
	
	public String getMusicTitle() {
		return musicTitle;
	}
	
	public void setMusicTitle(String musicTitle) {
		this.musicTitle = musicTitle;
	}
	
	public String getMusicArtist() {
		return musicArtist;
	}
	
	public void setMusicArtist(String musicArtist) {
		this.musicArtist = musicArtist;
	}
	
	public String getMusicAlbum() {
		return musicAlbum;
	}
	public void setMusicAlbum(String musicAlbum) {
		this.musicAlbum = musicAlbum;
	}
	
	public String getMusicUrl() {
		return musicUrl;
	}
	
	public void setMusicUrl(String musicUrl) {
		this.musicUrl = musicUrl;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PlaylistDetailDTO [playlistDetailNo=");
		builder.append(playlistDetailNo);
		builder.append(", UserNo=");
		builder.append(UserNo);
		builder.append(", musicSaveTime=");
		builder.append(musicSaveTime);
		builder.append(", musicNo=");
		builder.append(musicNo);
		builder.append(", isBeforeMusic=");
		builder.append(isBeforeMusic);
		builder.append(", musicTitle=");
		builder.append(musicTitle);
		builder.append(", musicArtist=");
		builder.append(musicArtist);
		builder.append(", musicAlbum=");
		builder.append(musicAlbum);
		builder.append(", musicUrl=");
		builder.append(musicUrl);
		builder.append("]");
		return builder.toString();
	}
}
