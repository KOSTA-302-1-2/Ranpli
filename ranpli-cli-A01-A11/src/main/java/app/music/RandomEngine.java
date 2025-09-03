package app.music;

import app.music.model.Track;
import app.repo.TrackDao;

import java.util.Optional;

/**
 * 랜덤 1곡 선택 + (ERD) tb_music 저장 + Track 생성
 * - 기존 동작 유지: RSS에서 랜덤 → iTunes lookup → DB 저장 → 재생
 */
public class RandomEngine {
  private final TrackDao trackDao;
  private final AppleRssClient rss = new AppleRssClient();
  private final AppleSearchClient search = new AppleSearchClient();

  public RandomEngine(TrackDao trackDao) { this.trackDao = trackDao; }

  public Track pickOneAndPersist() {
    try {
      var item = rss.pickRandomFromStorefront(100);
      if (item == null) return null;

      Optional<java.util.Map<String,String>> looked = search.lookupByAppleMusicId(item.trackId);
      if (looked.isEmpty()) return null;

      var m = looked.get();
      String title  = m.getOrDefault("title", "");
      String artist = m.getOrDefault("artist", "");
      String prev   = m.getOrDefault("previewUrl", "");
      String album  = m.getOrDefault("album", "");

      // ERD: tb_music 기준 저장(유사 UPSERT)
      int musicNo = trackDao.findOrInsert(title, artist, album, prev);

      // itunesTrackId 자리에 music_no 사용 (내부 식별용)
      return new Track(musicNo, title, artist, prev, null);
    } catch (Exception e) {
      // 어디서 막히는지 보이도록
      e.printStackTrace();
      return null;
    }
  }
}
