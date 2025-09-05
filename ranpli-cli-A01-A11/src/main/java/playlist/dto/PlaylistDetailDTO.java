package playlist.dto;

public class PlaylistDetailDTO {
	// tb_playlist_detail 속성
	private int playlistDetailNo;
	private int userNo;
	private String musicSaveTime;
	
	// tb_music 속성
	private int musicNo;
	private int isBeforeMusic;
	
	private String musicTitle;
	private String musicArtist;
	private String musicAlbum;
	private String musicUrl;
	
	
	public PlaylistDetailDTO(int musicNo, int userNo, int playlistDetailNo, String musicSaveTime,  String musicTitle, 
			String musicArtist, String musicAlbum, int isBeforeMusic, String musicUrl) {
		super();
		this.musicNo = musicNo;
		this.userNo = userNo;
		this.playlistDetailNo = playlistDetailNo;
		this.musicSaveTime = musicSaveTime;
		this.musicTitle = musicTitle;
		this.musicArtist = musicArtist;
		this.musicAlbum = musicAlbum;
		this.isBeforeMusic = isBeforeMusic;
		this.musicUrl = musicUrl;
	}

	public int getPlaylistDetailNo() {
		return playlistDetailNo;
	}
	
	public void setPlaylistDetailNo(int playlistDetailNo) {
		this.playlistDetailNo = playlistDetailNo;
	}
	
	public int getUserNo() {
		return userNo;
	}
	
	public void setUserNo(int userNo) {
		this.userNo = userNo;
	}
	
	public String getMusicSaveTime() {
		return musicSaveTime;
	}
	
	public void setMusicSaveTime(String musicSaveTime) {
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
		builder.append(this.musicTitle);
		builder.append(" / ");
		builder.append(this.musicArtist);
		builder.append(" / ");
		builder.append(this.musicAlbum);
		return builder.toString();
	}
}
