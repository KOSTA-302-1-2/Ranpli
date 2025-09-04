package user.service;

import java.sql.SQLException;
import java.util.List;

import user.Exception.UserIdInvalidException;
import user.Exception.UserNotFoundException;
import user.Exception.UserPwdInvalidException;

import user.dao.UserDAO;
import user.dao.UserDAOImpl;
import user.dto.PlaylistDetailDTO;
import user.dto.UserDTO;

import user.session.Session;
import user.session.SessionSet;

public class UserService {
	static UserDAO userDao = new UserDAOImpl();
	
	public void account (String userId, String userPwd) throws UserNotFoundException {
		try {
			userDao.account(userId, userPwd);
		} catch (UserIdInvalidException e) {
			throw new UserNotFoundException("아이디를 다시 입력해주세요.");
		} catch (UserPwdInvalidException e) {
			throw new UserNotFoundException("비밀번호를 다시 입력해주세요.");
		} catch (Exception e) {
			System.out.println("회원가입에 문제가 있습니다.");
			e.printStackTrace();
		}
	}
	
	public UserDTO login (String userId, String userPwd) throws UserNotFoundException {
		UserDTO user = null;
		
		try {
			user = userDao.login(userId, userPwd);
			System.out.println(user);
		} catch (UserIdInvalidException e) {
			throw new UserNotFoundException("아이디를 다시 입력해주세요.");
		} catch (UserPwdInvalidException e) {
			throw new UserNotFoundException("비밀번호를 다시 입력해주세요.");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("로그인에 문제가 있습니다.");
		}
		
		Session session = new Session(userId);
		session.setAttribute("playList", user.getPlaylistDetailList());
		
		SessionSet sessionSet = SessionSet.getInstance();
		sessionSet.addSet(session);
		
		return user;
	}
	
	public List<PlaylistDetailDTO> getPlayList (String userId) {
		SessionSet ss = SessionSet.getInstance();
		for (Session s : ss.getSet()) {
			if (s.getSessionId() == userId) {
				return (List<PlaylistDetailDTO>) s.getAttributes().get("playList");
			}
		}
		
		return null;
	}
}