package search.service;

import java.util.List;

import search.dao.MusicSearchDAO;
import search.dao.MusicSearchDAOImpl;
import search.dto.SearchedMusicDTO;
import search.exception.EmptySearchMusicException;

public class MusicSearchService {
	private MusicSearchDAO musicSearchDao  = new MusicSearchDAOImpl();
	
	public List<SearchedMusicDTO> searchMusic (String context) throws EmptySearchMusicException{
		try {
			List<SearchedMusicDTO> searchedMusicList = musicSearchDao.searchMusic(context);
			
			return searchedMusicList;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public SearchedMusicDTO pickRandom (String context) throws EmptySearchMusicException{
		List<SearchedMusicDTO> searchedMusicList = searchMusic(context);
		
		int randomIdx = (int) (Math.random() * searchedMusicList.size());
		
		return searchedMusicList.get(randomIdx);
	}
}