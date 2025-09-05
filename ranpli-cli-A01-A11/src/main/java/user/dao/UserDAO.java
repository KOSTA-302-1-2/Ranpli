package user.dao;

import java.sql.SQLException;

import user.dto.UserDTO;
import user.exception.UserIdInvalidException;
import user.exception.UserPwdInvalidException;

public interface UserDAO {
	/**
	 * 회원가입
	 */
	void  account(String userId, String userPwd)
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