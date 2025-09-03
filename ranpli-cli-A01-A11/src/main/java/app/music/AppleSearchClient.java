package app.music;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AppleSearchClient {
  private final OkHttpClient http = new OkHttpClient();
  private final ObjectMapper om = new ObjectMapper();

  public Optional<Map<String,String>> lookupByAppleMusicId(String id){
    try {
      HttpUrl url = HttpUrl.parse("https://itunes.apple.com/lookup")
          .newBuilder()
          .addQueryParameter("id", id)
          .build();
      Request req = new Request.Builder().url(url).get().build();
      try (Response resp = http.newCall(req).execute()) {
        if (!resp.isSuccessful() || resp.body() == null) return Optional.empty();
        JsonNode root = om.readTree(resp.body().string());
        if (root.path("resultCount").asInt(0) < 1) return Optional.empty();
        JsonNode first = root.path("results").get(0);
        String preview = first.path("previewUrl").asText(null);
        if (preview == null || preview.isBlank()) return Optional.empty();
        Map<String,String> m = new HashMap<>();
        m.put("itunesTrackId", first.path("trackId").asText(""));
        m.put("title", first.path("trackName").asText(""));
        m.put("artist", first.path("artistName").asText(""));
        m.put("previewUrl", preview);
        m.put("artworkUrl", first.path("artworkUrl100").asText(""));
        return Optional.of(m);
      }
    } catch (IOException e){
      return Optional.empty();
    }
  }
}
