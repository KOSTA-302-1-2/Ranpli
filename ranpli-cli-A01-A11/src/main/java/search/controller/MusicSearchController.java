package search.controller;

import search.exception.EmptySearchMusicException;
import search.service.MusicSearchService;

public class MusicSearchController {
	static MusicSearchService musicSearchService =
			new MusicSearchService();
	
	public static void searchMusic(String context) {
		try {
			musicSearchService.searchMusic(context);
		} catch (EmptySearchMusicException e) {
			e.printStackTrace();
		}
	}
}
