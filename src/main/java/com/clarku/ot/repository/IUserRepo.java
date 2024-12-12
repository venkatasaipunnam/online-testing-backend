package com.clarku.ot.repository;

import java.util.HashMap;
import java.util.List;

import com.clarku.ot.exception.GlobalException;
import com.clarku.ot.vo.SignUpVO;
import com.clarku.ot.vo.UserProfileVO;
import com.clarku.ot.vo.UserVO;

public interface IUserRepo {

	UserVO retrieveUserDetails(Integer userId) throws GlobalException;

	String getUserFirstName(Integer userId) throws GlobalException;

	Boolean saveSignUpUser(SignUpVO userDetails) throws GlobalException;

	UserProfileVO retrieveUserLoginDetails(Integer userId) throws GlobalException;

	Boolean updateUserPass(Integer userId, String createPassword) throws GlobalException;

	Boolean updateUserProf(Integer userId, HashMap<String, String> updatedDetails) throws GlobalException;

	List<UserProfileVO> getUsers() throws GlobalException;

}
