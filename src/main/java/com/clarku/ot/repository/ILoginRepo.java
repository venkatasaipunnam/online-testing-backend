package com.clarku.ot.repository;

import com.clarku.ot.exception.GlobalException;
import com.clarku.ot.exception.LoginException;
import com.clarku.ot.vo.SignUpVO;
import com.clarku.ot.vo.UserProfileVO;

public interface ILoginRepo {

	UserProfileVO retrieveUserLogin(String emailId) throws GlobalException;

	Boolean isUserExists(String emailId) throws GlobalException;

	Integer getUserId(String emailId) throws GlobalException;

	void saveLastLogin(Integer userId) throws LoginException;

	void lockUserAccount(Integer userId) throws LoginException;

	void updateUnSuccessAttempt(Integer userId) throws LoginException;

	Boolean saveTempPassword(Integer userId, String tempPass) throws GlobalException;

	Boolean saveLoginDetails(SignUpVO userDetails) throws GlobalException;
	
	Boolean savePasswordDetails(SignUpVO userDetails) throws GlobalException;

	UserProfileVO retrieveUserLoginbyUsername(String username) throws GlobalException;

	void resetUserAttemptsStatus(Integer userId) throws LoginException;
}
