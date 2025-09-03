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
  private final OkHttpClient http = new OkHttpClient();
  private final ObjectMapper om = new ObjectMapper();
  private final Random rnd = new Random();

  public static class FeedItem {
    public final String storefront;
    public final String title;
    public final String artist;
    public final String url;
    public final String trackId;
    public FeedItem(String storefront, String title, String artist, String url, String trackId){
      this.storefront = storefront; this.title = title; this.artist = artist; this.url = url; this.trackId = trackId;
    }
  }

  public List<FeedItem> fetchTopSongs(String storefront, int limit) throws IOException {
    HttpUrl url = HttpUrl.parse("https://rss.applemarketingtools.com/api/v2/"
        + storefront + "/music/most-played/" + limit + "/songs.json");
    Request req = new Request.Builder().url(url).get().build();
    try (Response resp = http.newCall(req).execute()) {
      if (!resp.isSuccessful() || resp.body() == null) return List.of();
      JsonNode root = om.readTree(resp.body().string());
      JsonNode results = root.path("feed").path("results");
      List<FeedItem> list = new ArrayList<>();
      for (JsonNode n : results) {
        list.add(new FeedItem(
            storefront,
            n.path("name").asText(""),
            n.path("artistName").asText(""),
            n.path("url").asText(""),
            n.path("id").asText("")
        ));
      }
      return list;
    }
  }

  public FeedItem pickRandom(List<FeedItem> list){
    if (list == null || list.isEmpty()) return null;
    return list.get(rnd.nextInt(list.size()));
  }

  public FeedItem pickRandomFromStorefront(int limit) throws IOException {
    String sf = Config.get("country"); if (sf == null || sf.isBlank()) sf = "us";
    return pickRandom(fetchTopSongs(sf, limit));
  }
}
