package com.clarku.ot.service;

import java.util.List;

import com.clarku.ot.exception.GlobalException;
import com.clarku.ot.vo.ChangePassVO;
import com.clarku.ot.vo.UserProfileVO;
import com.clarku.ot.vo.UserVO;

public interface IUserService {

	Boolean resetPassword(String emailId) throws GlobalException;

	UserVO getUser(Integer userId) throws GlobalException;
	
	Boolean changePassword(Integer userId, ChangePassVO passVO) throws GlobalException;

	Boolean updateUser(UserVO user, UserVO updatedUser) throws GlobalException;

	List<UserProfileVO> getUsers() throws GlobalException;

	String generateTempPassword(Integer userId) throws GlobalException;

}