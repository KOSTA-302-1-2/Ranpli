package search.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import app.db.Db;
import search.dto.SearchedMusicDTO;
import search.exception.EmptySearchMusicException;

public class MusicSearchDAOImpl implements MusicSearchDAO {

	@Override
	public List<SearchedMusicDTO> searchMusic(String context) 
			throws EmptySearchMusicException {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String searchText = "%" + context + "%";
		List<SearchedMusicDTO> searchedMusicList = new ArrayList<>();
		
		String sql = "select * "
						+ "from tb_music "
						+ "where music_title like ? or "
						+ "music_artist like ?";
		
		try {
			con = Db.getConnection();
			
			ps = con.prepareStatement(sql);
			ps.setString(1, searchText);
			ps.setString(2, searchText);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				SearchedMusicDTO smd = new SearchedMusicDTO(
						rs.getInt(1),
						rs.getString(2), 
						rs.getString(3), 
						rs.getString(4), 
						rs.getInt(5), 
						rs.getString(6)
						);
				
				searchedMusicList.add(smd);
			}
			
			if (searchedMusicList.isEmpty()) {
				throw new EmptySearchMusicException("검색 결과가 존재하지 않습니다.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Db.releaseConnection(con, ps, rs);
		}
		
		return searchedMusicList;
	}

}
