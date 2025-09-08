package search.service;

import search.dao.ItunesSearchDAO;
import search.dao.ItunesSearchDAOImpl;
import search.dto.SearchedMusicDTO;
import search.exception.EmptySearchMusicException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * DB 결과를 우선 사용하고, 모자라면 iTunes로 보강/합치는 하이브리드 검색.
 * - limit: 최종 반환 최대 개수
 * - dedup: 제목+아티스트 기준 중복 제거
 */
public class HybridSearchService {

    private final MusicSearchService dbService = new MusicSearchService(); // 기존 DB 서비스
    private final ItunesSearchDAO itunesDao = new ItunesSearchDAOImpl();

    public List<SearchedMusicDTO> search(String keyword, int limit) {
        int cap = limit > 0 ? limit : 30;

        // 1) DB 먼저
        List<SearchedMusicDTO> db = Collections.emptyList();
        try {
            db = dbService.searchMusic(keyword);
        } catch (EmptySearchMusicException e) {
            db = Collections.emptyList();
        } catch (Exception e) {
            // DB 오류는 조용히 무시하고 iTunes로 넘어감 (로그 필요시 여기에)
            db = Collections.emptyList();
        }

        // cap을 이미 채웠으면 바로 반환
        if (db.size() >= cap) {
            return db.subList(0, cap);
        }

        // 2) iTunes로 보강
        List<SearchedMusicDTO> itunes = Collections.emptyList();
        try {
            itunes = itunesDao.search(keyword, cap);
        } catch (Exception e) {
            itunes = Collections.emptyList();
        }

        // 3) 합치고 중복 제거(제목+아티스트 키)
        LinkedHashMap<String, SearchedMusicDTO> map = new LinkedHashMap<>();
        for (SearchedMusicDTO d : db)     map.put(key(d), d);
        for (SearchedMusicDTO d : itunes) map.putIfAbsent(key(d), d);

        return map.values().stream().limit(cap).collect(Collectors.toList());
    }

    private static String key(SearchedMusicDTO d) {
        return (nn(d.getMusicTitle()) + "||" + nn(d.getMusicArtist())).toLowerCase(Locale.ROOT);
    }
    private static String nn(String s) { return s == null ? "" : s; }
}
