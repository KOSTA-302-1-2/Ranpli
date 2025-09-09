// 패키지: 음악 도메인(랜덤 재생 엔진)
package app.music;

// 재생 단위 모델(Track) 임포트
import app.music.model.Track;
// tb_music 테이블 접근 DAO 임포트
import app.repo.TrackDao;

// Optional 사용(lookup 결과 존재/부재 표현)
import java.util.Optional;
// 다국가 결과 합치기 위한 컬렉션들
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 랜덤 1곡 선택 + (ERD) tb_music 저장 + Track 생성
 * - 변경점: 여러 국가 RSS(top songs)를 합쳐 큰 풀에서 랜덤 선택
 * - 기존 흐름: RSS → iTunes lookup → DB 저장 → 재생
 */
public class RandomEngine {
  // DB 영속화 담당 DAO
  private final TrackDao trackDao;
  // 인기곡 RSS 호출 클라이언트
  private final AppleRssClient rss = new AppleRssClient();
  // Apple Music 상세 조회(미리듣기 URL 등) 클라이언트
  private final AppleSearchClient search = new AppleSearchClient();

  // DAO 주입 생성자
  public RandomEngine(TrackDao trackDao) { this.trackDao = trackDao; }

  // 랜덤으로 1곡을 선택하고, DB에 보존(findOrInsert) 후 재생용 Track으로 반환
  public Track pickOneAndPersist() {
    try {

      //여러 국가에서 100개씩 모아(중복 제거)
      List<String> sfs = List.of("us","kr","jp");     // 사용할 국가 코드들(필요 시 추가/수정)
      List<AppleRssClient.FeedItem> pool = new ArrayList<>(); // 국가별 결과를 합칠 풀
      Set<String> seen = new HashSet<>();             // trackId 기준 중복 제거용 집합

      for (String sf : sfs) {                         // 각 국가 코드에 대해
        var part = rss.fetchTopSongs(sf, 100);        // 해당 국가의 상위 100곡을 가져오고
        for (var it : part) {                         // 결과를 순회하면서
          if (seen.add(it.trackId)) {                 // trackId가 처음 보면(true)만
            pool.add(it);                             // 풀에 추가(중복 제거)
          }
        }
      }

      var item = rss.pickRandom(pool);                // 합쳐진 큰 풀에서 최종 1곡 무작위 선택
      if (item == null) return null;                  // 풀 비었거나 실패 시 null 반환

      Optional<java.util.Map<String,String>> looked =
          search.lookupByAppleMusicId(item.trackId);  // 선택곡의 Apple Music ID로 상세 조회(미리듣기 등)
      if (looked.isEmpty()) return null;              // 상세 조회 실패 시 null

      var m = looked.get();                           // 조회 결과 맵
      String title  = m.getOrDefault("title", "");    // 곡명
      String artist = m.getOrDefault("artist", "");   // 아티스트
      String prev   = m.getOrDefault("previewUrl", ""); // 미리듣기 URL
      String album  = m.getOrDefault("album", "");    // 앨범명

      // ERD: tb_music 기준 저장(유사 UPSERT)
      int musicNo = trackDao.findOrInsert(title, artist, album, prev); // 존재하면 PK 반환, 없으면 INSERT 후 PK 반환

      // itunesTrackId 자리에 music_no 사용 (내부 식별용) — 외부 ID는 -1L로 비활성 표기
      return new Track(musicNo, -1L, title, artist, prev, null);
    } catch (Exception e) {                            // 네트워크/파싱/DB 에러 등 모든 예외 포착
      e.printStackTrace();                             // 디버깅을 위해 스택 트레이스 출력
      return null;                                     // 상위에서 실패 분기 처리하도록 null 반환
    }
  }
}
