package search.service;

import java.util.List;

import search.dao.MusicSearchDAO;
import search.dao.MusicSearchDAOImpl;
import search.dto.SearchedMusicDTO;
import search.exception.EmptySearchMusicException;

public class MusicSearchService {
	private MusicSearchDAO musicSearchDao  = new MusicSearchDAOImpl();
	
	public void searchMusic (String context) throws EmptySearchMusicException{
		try {
			List<SearchedMusicDTO> searchedMusicList = musicSearchDao.searchMusic(context);
			
			for (SearchedMusicDTO smd : searchedMusicList) {
				System.out.println(smd);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}