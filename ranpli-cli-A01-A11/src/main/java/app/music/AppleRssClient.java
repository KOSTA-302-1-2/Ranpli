package app.music;

import app.config.Config;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AppleRssClient {
	  // HTTP 요청 전송을 위한 OkHttp 클라이언트(재사용)
	  private final OkHttpClient http = new OkHttpClient();
	  // JSON 파싱을 위한 Jackson ObjectMapper
	  private final ObjectMapper om = new ObjectMapper();
	  // 리스트에서 무작위 요소 선택용 난수기
	  private final Random rnd = new Random();

	  // RSS에서 내려오는 곡 정보를 담는 내부 불변 DTO
	  public static class FeedItem {
	    // 어떤 국가(storefront)의 차트인지 (예: "us", "kr", "jp")
	    public final String storefront;
	    // 곡 제목
	    public final String title;
	    // 아티스트명
	    public final String artist;
	    // 애플 뮤직 곡 상세 페이지 URL
	    public final String url;
	    // 애플(Apple Music) 곡 식별자
	    public final String trackId;

	    // 모든 필드를 생성 시 한번에 설정하는 생성자
	    public FeedItem(String storefront, String title, String artist, String url, String trackId){
	      // 필드 초기화
	      this.storefront = storefront; this.title = title; this.artist = artist; this.url = url; this.trackId = trackId;
	    }
	  }

	  // 지정한 국가(storefront)에서 상위 limit개의 'Most Played' 곡 목록을 가져온다.
	  public List<FeedItem> fetchTopSongs(String storefront, int limit) throws IOException {
	    // Apple Marketing Tools RSS 엔드포인트 URL을 조립
	    HttpUrl url = HttpUrl.parse("https://rss.applemarketingtools.com/api/v2/"
	        + storefront + "/music/most-played/" + limit + "/songs.json");
	    // GET 요청 객체 생성
	    Request req = new Request.Builder().url(url).get().build();
	    // 응답(Response)을 자동으로 닫기 위해 try-with-resources 사용
	    try (Response resp = http.newCall(req).execute()) {
	      // HTTP 실패이거나 본문이 없으면 빈 리스트 반환
	      if (!resp.isSuccessful() || resp.body() == null) return List.of();
	      // 응답 본문(JSON 문자열)을 트리(JsonNode)로 파싱
	      JsonNode root = om.readTree(resp.body().string());
	      // feed.results 배열 위치로 이동
	      JsonNode results = root.path("feed").path("results");
	      // 결과를 담을 리스트 준비
	      List<FeedItem> list = new ArrayList<>();
	      // 각 결과 노드를 순회하며 DTO로 변환
	      for (JsonNode n : results) {
	        list.add(new FeedItem(
	            storefront,                       // 요청한 국가 코드 기록
	            n.path("name").asText(""),        // 곡명(name), 누락 시 ""
	            n.path("artistName").asText(""),  // 아티스트명(artistName), 누락 시 ""
	            n.path("url").asText(""),         // 곡 상세 URL, 누락 시 ""
	            n.path("id").asText("")           // 트랙 ID(id), 누락 시 ""
	        ));
	      }
	      // 파싱된 전체 목록 반환
	      return list;
	    }
	  }

	  // 여러 국가의 Top Songs를 합쳐서(중복 제거) 큰 풀을 만든다.
		public List<FeedItem> fetchTopSongsMultiStorefront(List<String> storefronts, int limitPerSf) throws IOException {
		  // 모든 국가의 결과를 담을 리스트
		  List<FeedItem> all = new ArrayList<>();
		  // 트랙 ID 기준 중복 여부 확인용 Set
		  java.util.Set<String> seenIds = new java.util.HashSet<>();
		  // 전달받은 각 국가에 대해
		  for (String sf : storefronts) {
		      // 해당 국가의 상위 limitPerSf개 목록을 가져오고
		      List<FeedItem> part = fetchTopSongs(sf, limitPerSf);
		      // 각 항목을 순회하며
		      for (FeedItem it : part) {
		          // 처음 보는 trackId라면 Set에 추가되고 true → 목록에 편입
		          if (seenIds.add(it.trackId)) all.add(it);
		      }
		  }
		  // 합쳐진 큰 풀(중복 제거 결과) 반환
		  return all;
		}

	  // 리스트에서 무작위 1곡을 뽑아 반환(리스트가 비었으면 null)
	  public FeedItem pickRandom(List<FeedItem> list){
	    // 방어: null 또는 빈 리스트면 null
	    if (list == null || list.isEmpty()) return null;
	    // 0..size-1 범위에서 임의 인덱스를 골라 반환
	    return list.get(rnd.nextInt(list.size()));
	  }

	  // 설정(Config)의 국가 코드(없으면 "us")에서 상위 limit곡 목록을 받아 무작위 1곡을 반환
	  public FeedItem pickRandomFromStorefront(int limit) throws IOException {
	    // 설정에서 country 값을 읽고, 비어있으면 기본 "us"
	    String sf = Config.get("country"); if (sf == null || sf.isBlank()) sf = "us";
	    // 해당 국가의 목록을 받아 하나 무작위로 선택
	    return pickRandom(fetchTopSongs(sf, limit));
	  }
	}
