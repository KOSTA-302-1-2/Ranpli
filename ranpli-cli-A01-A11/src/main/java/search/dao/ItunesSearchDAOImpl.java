package search.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import search.dto.SearchedMusicDTO;

import java.util.ArrayList;
import java.util.List;

public class ItunesSearchDAOImpl implements ItunesSearchDAO {

    private static final OkHttpClient HTTP = new OkHttpClient();
    private static final ObjectMapper M = new ObjectMapper();

    @Override
    public List<SearchedMusicDTO> search(String keyword, int limit) throws Exception {
        // URLEncoder.encode 제거 → OkHttp가 자동 인코딩
        HttpUrl url = HttpUrl.parse("https://itunes.apple.com/search")
                .newBuilder()
                .addQueryParameter("term", keyword)                  
                .addQueryParameter("country", "")
                .addQueryParameter("media", "music")
                .addQueryParameter("entity", "musicTrack")           
                .addQueryParameter("limit", String.valueOf(Math.max(1, limit)))
                .build();

        Request req = new Request.Builder()
                .url(url)
                .header("User-Agent", "ranpli-cli/1.0")              // (권장) UA 지정
                .get()
                .build();

        try (Response res = HTTP.newCall(req).execute()) {
            if (!res.isSuccessful() || res.body() == null) {
                return List.of();
            }
            JsonNode root = M.readTree(res.body().byteStream());
            JsonNode arr  = root.get("results");
            if (arr == null || !arr.isArray() || arr.size() == 0) return List.of();

            List<SearchedMusicDTO> out = new ArrayList<>(arr.size());
            for (JsonNode n : arr) {
                String title      = nn(n.get("trackName"));
                String artist     = nn(n.get("artistName"));
                String album      = nn(n.get("collectionName"));
                String previewUrl = nn(n.get("previewUrl"));
                

                SearchedMusicDTO dto = new SearchedMusicDTO(
                        0,          // musicNo (DB PK 없으니 0)
                        title,      // musicTitle
                        artist,     // musicArtist
                        album,      // musicAlbum
                        0,          // is_before_music (기본값)
                        previewUrl  // musicUrl (미리듣기 URL)
                );

                out.add(dto);
            }
            return out;
        }
    }

    private static String nn(JsonNode n) { return n == null ? "" : n.asText(""); }
}
