package user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import app.db.Db;

import user.Exception.UserIdInvalidException;
import user.Exception.UserPwdInvalidException;

import user.dto.UserDTO;

public class UserDAOImpl implements UserDAO {
	private final static int idLengthLimit = 20;
	private final static int pwdLengthLimit = 20;
	
	@Override
	public void account(String userId, String userPwd)
			throws UserIdInvalidException, UserPwdInvalidException, SQLException {

		Connection con = null;
		PreparedStatement ps = null;
		int result = 0;
		
		String sql = "INSERT INTO tb_members"
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
			ps = con.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setString(2, userPwd);
			ps.setString(3, now.format(formatter));
			
			result = ps.executeUpdate();
			
			if (result == 0) {
				throw new SQLException("회원가입에 실패했습니다.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Db.releaseConnection(con, ps);
		}
	}

	@Override
	public UserDTO login(String userId, String userPwd)
			throws UserIdInvalidException, UserPwdInvalidException, SQLException {

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		UserDTO user = null;
		
		String sql = "select * from tb_members "
						+ "where users_id = ?";
		
		try {
			con = Db.getConnection();
			
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
			}
			
			else {
				throw new UserIdInvalidException("일치하는 아이디가 존재하지 않습니다.");
			}
		} finally {
			Db.releaseConnection(con, ps);
		}
		
		return user;
	}

}
