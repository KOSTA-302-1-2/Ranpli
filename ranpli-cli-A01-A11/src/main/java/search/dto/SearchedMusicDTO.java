package search.dto;

public class SearchedMusicDTO {
	private int musicNo;
	private String musicTitle;
	private String musicArtist;
	private String musicAlbum;
	private int is_before_music;
	private String musicUrl;
	
	public SearchedMusicDTO(int musicNo, String musicTitle, String musicArtist, String musicAlbum, int is_before_music,
			String musicUrl) {
		super();
		this.musicNo = musicNo;
		this.musicTitle = musicTitle;
		this.musicArtist = musicArtist;
		this.musicAlbum = musicAlbum;
		this.is_before_music = is_before_music;
		this.musicUrl = musicUrl;
	}

	public int getMusicNo() {
		return musicNo;
	}

	public void setMusicNo(int musicNo) {
		this.musicNo = musicNo;
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

	public int getIs_before_music() {
		return is_before_music;
	}

	public void setIs_before_music(int is_before_music) {
		this.is_before_music = is_before_music;
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
		builder.append("SearchedMusicDTO [musicNo=");
		builder.append(musicNo);
		builder.append(", musicTitle=");
		builder.append(musicTitle);
		builder.append(", musicArtist=");
		builder.append(musicArtist);
		builder.append(", musicAlbum=");
		builder.append(musicAlbum);
		builder.append(", is_before_music=");
		builder.append(is_before_music);
		builder.append(", musicUrl=");
		builder.append(musicUrl);
		builder.append("]");
		return builder.toString();
	}
}
