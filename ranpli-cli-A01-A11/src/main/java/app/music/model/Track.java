package app.music.model;

/** 재생에 필요한 최소 메타데이터 */
public record Track(int musicNo,
        long itunesTrackId,
        String title,
        String artist,
        String previewUrl,
        String artworkUrl) {}
