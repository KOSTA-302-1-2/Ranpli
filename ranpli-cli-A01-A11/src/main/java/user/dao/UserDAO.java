package user.dao;

import java.sql.SQLException;

import user.Exception.UserIdInvalidException;
import user.Exception.UserPwdInvalidException;

import user.dto.UserDTO;

public interface UserDAO {
	/**
	 * 회원가입
	 */
	void account(String userId, String userPwd)
			throws UserIdInvalidException, 
						UserPwdInvalidException,
						SQLException;
	
	/**
	 * 로그인
	 */
	UserDTO login(String userId, String userPwd) 
			throws UserIdInvalidException, 
						UserPwdInvalidException,
						SQLException;
}