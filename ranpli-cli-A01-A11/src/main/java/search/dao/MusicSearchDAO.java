package search.dao;

import java.sql.SQLException;
import java.util.List;

import search.dto.SearchedMusicDTO;
import search.exception.EmptySearchMusicException;

public interface MusicSearchDAO {
	/**
	 * 음악 검색
	 */
	List<SearchedMusicDTO> searchMusic (String context)
		throws EmptySearchMusicException;
}
