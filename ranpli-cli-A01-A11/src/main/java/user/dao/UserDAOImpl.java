package user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import app.db.Db;

import user.Exception.UserIdInvalidException;
import user.Exception.UserPwdInvalidException;
import user.dto.PlaylistDetailDTO;
import user.dto.UserDTO;

public class UserDAOImpl implements UserDAO {
	private final static int idLengthLimit = 20;
	private final static int pwdLengthLimit = 20;
	
	@Override
	public void account(String userId, String userPwd)
			throws UserIdInvalidException, UserPwdInvalidException, SQLException {

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		int userNo = 0;
		int result = 0;
		
		
		String sql = "INSERT INTO tb_users"
				+ 			"(users_id, users_pwd, users_reg_date)"
				+ 			"VALUES(?, ?, ?)";
		
		LocalDate now = LocalDate.now();
		DateTimeFormatter formatter = 
				DateTimeFormatter.ofPattern("yyyy-MM-dd");
		
		
		if (userId.length() > idLengthLimit) {
			throw new UserIdInvalidException("아이디의 길이는 20자 이하여야 합니다.");
		}
		
		if (userPwd.length() > pwdLengthLimit) {
			throw new UserPwdInvalidException("비밀번호의 길이는 20자 이하여야 합니다.");
		}
		
		try {
			con = Db.getConnection();
			con.setAutoCommit(false);
			
			ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, userId);
			ps.setString(2, userPwd);
			ps.setString(3, now.format(formatter));
			
			result = ps.executeUpdate();
			
			if (result == 0) {
				throw new SQLException("회원가입에 실패했습니다.");
			}
			
			else {
				rs = ps.getGeneratedKeys();
				if (rs.next()) {
					userNo = rs.getInt(1);					
				}
			}
			
			addPlaylist(con, userNo);
			
		} catch (SQLException e) {
			e.printStackTrace();
			con.rollback();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			con.commit();
			Db.releaseConnection(con, ps);
		}
	}
	
	private void addPlaylist(Connection con, int userNo) throws SQLException {
		PreparedStatement ps = null;
		
		int result = 0;
		
		String sql = "INSERT INTO tb_playlist (users_no) values (?)";
		
		try {
			ps = con.prepareStatement(sql);
			
			ps.setInt(1, userNo);
			
			result = ps.executeUpdate();
			
			if (result == 0) {
				throw new SQLException();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Db.releaseConnection(null, ps);
		}
	}
	
	@Override
	public UserDTO login(String userId, String userPwd)
			throws UserIdInvalidException, UserPwdInvalidException, SQLException {

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		UserDTO user = null;
		
		String sql = "select * from tb_users "
						+ "where users_id = ?";
		
		try {
			con = Db.getConnection();
			con.setAutoCommit(false);
			
			ps = con.prepareStatement(sql);
			ps.setString(1, userId);
			
			rs = ps.executeQuery();
			
			if (rs.next()) {
				user = new UserDTO(
						rs.getInt(1), 
						rs.getString(2), 
						rs.getString(3), 
						rs.getString(4) 
						);
				
				if (user.getUserPwd().equals(userPwd) != true) {
					throw new UserPwdInvalidException("비밀번호가 일치하지 않습니다.");
				}
				
				getUserPlaylist(con, user);
			}
			
			else {
				throw new UserIdInvalidException("일치하는 아이디가 존재하지 않습니다.");
			}
		} finally {
			Db.releaseConnection(con, ps);
		}
		
		return user;
	}
	
	private void getUserPlaylist (Connection con, UserDTO user) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String sql = "select * "
						+ "from tb_users "
						+ "join tb_playlist_detail "
						+ "join tb_music using(music_no) "
						+ "where users_no = ?";
		
		try {
			ps = con.prepareStatement(sql);
			ps.setInt(1, user.getUserNO());
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				PlaylistDetailDTO pdd = new PlaylistDetailDTO(
						rs.getInt(1),
						rs.getInt(2),
						rs.getInt(7),
						rs.getString(9),
						rs.getString(10),
						rs.getString(11),
						rs.getString(12),
						rs.getInt(13),
						rs.getString(14)
						);
				
				user.addPlaylistDetailList(pdd);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			con.rollback();
		}
		finally {
			con.commit();
			Db.releaseConnection(con, ps);
		}
	}
}
