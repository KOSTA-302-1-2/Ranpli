package search.dao;

import search.dto.SearchedMusicDTO;
import java.util.List;

public interface ItunesSearchDAO {
    List<SearchedMusicDTO> search(String keyword, int limit) throws Exception;
}
