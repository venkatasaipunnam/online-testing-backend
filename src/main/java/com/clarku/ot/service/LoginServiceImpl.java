package com.clarku.ot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.clarku.ot.exception.GlobalException;
import com.clarku.ot.exception.LoginException;
import com.clarku.ot.repository.ILoginRepo;
import com.clarku.ot.repository.IUserRepo;
import com.clarku.ot.utils.Constants;
import com.clarku.ot.utils.Secure;
import com.clarku.ot.vo.LoginVO;
import com.clarku.ot.vo.SignUpVO;
import com.clarku.ot.vo.UserProfileVO;
import com.clarku.ot.vo.UserVO;

import io.micrometer.common.util.StringUtils;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class LoginServiceImpl implements ILoginService {

	@Autowired
	private ILoginRepo loginRepo;

	@Autowired
	private IUserRepo userRepo;

	@Autowired
	Secure secure;

	@Override
	public UserVO signIn(LoginVO loginDetails) throws GlobalException, LoginException {
		UserProfileVO userDetails = validateUser(loginDetails);
		UserVO userProf = userRepo.retrieveUserDetails(userDetails.getUserId());
		if (StringUtils.isNotBlank(userDetails.getTempPassword())) {
			userProf.setIsTempPassword(true);
		}
		return userProf;
	}

	private UserProfileVO validateUser(LoginVO loginDetails) throws GlobalException, LoginException {
		String loginBy = loginDetails.getUsername();
		if (loginDetails.getIsLoginByEmail() != null && Boolean.TRUE.equals(loginDetails.getIsLoginByEmail())) {
			loginBy = loginDetails.getEmailId();
		}
		if (StringUtils.isBlank(loginBy)) {
			log.error("LoginServiceImpl :: validateUser(): Login ID is Null/Empty ");
			throw new LoginException(Constants.EMAIL_USERNAME_REQUIRED, HttpStatus.BAD_REQUEST);
		}
		UserProfileVO userDetails = retrieveLoginDetails(loginDetails);
		if (userDetails == null) {
			log.error("LoginServiceImpl :: validateUser(): User {} ", loginDetails.getEmailId());
			throw new LoginException(Constants.NOT_REGISTERED_EXP, HttpStatus.UNAUTHORIZED); 
		}
		if (Boolean.FALSE.equals(userDetails.getIsActive())) {
			log.error("LoginServiceImpl:: validateUser() : User is in-active " + Constants.USER_INACTIVE_EXP);
			throw new LoginException(Constants.USER_INACTIVE_EXP, HttpStatus.UNAUTHORIZED);
		}
		if (Boolean.TRUE.equals(userDetails.getIsLocked())) {
			log.error("LoginServiceImpl:: validateUser() : " + Constants.USER_LOCKED_EXP);
			throw new LoginException(Constants.USER_LOCKED_EXP, HttpStatus.UNAUTHORIZED);
		}
		
		String encryptPass = secure.getEncrypted(loginDetails.getPassword());
		String actualPass = userDetails.getPassword();
		if (StringUtils.isNotBlank(userDetails.getTempPassword())) {
			actualPass = userDetails.getTempPassword();
		}
		if (!encryptPass.equals(actualPass)) {
			if (userDetails.getFailedLoginAttempts().equals(Integer.valueOf(2))) {
				loginRepo.lockUserAccount(userDetails.getUserId());
			} else {
				loginRepo.updateUnSuccessAttempt(userDetails.getUserId());
			}
			log.error("LoginServiceImpl:: validateUser() : " + Constants.LOGIN_WRONG_PASS_EXP);
			throw new LoginException(Constants.LOGIN_WRONG_PASS_EXP, HttpStatus.UNAUTHORIZED);
		}
		return userDetails;
	}

	private UserProfileVO retrieveLoginDetails(LoginVO loginDetails) throws GlobalException {
		UserProfileVO userDetails = null;
		if (loginDetails.getIsLoginByEmail() != null && loginDetails.getIsLoginByEmail().equals(Boolean.TRUE)) {
			userDetails = loginRepo.retrieveUserLogin(loginDetails.getEmailId());
		} else {
			userDetails = loginRepo.retrieveUserLoginbyUsername(loginDetails.getUsername());
		}
		return userDetails;
	}

	@Override
	public void updateLastLogin(Integer userId) throws LoginException {
		loginRepo.saveLastLogin(userId);
		loginRepo.resetUserAttemptsStatus(userId);
	}

	@Override
	public Boolean signUpUser(SignUpVO userDetails) throws GlobalException {
		validateSignUpDetails(userDetails);
		userRepo.saveSignUpUser(userDetails);
		Integer userId = loginRepo.getUserId(userDetails.getEmailId());
		userDetails.setUserId(userId);
		Boolean isSaved = loginRepo.saveLoginDetails(userDetails);
		loginRepo.savePasswordDetails(userDetails);
		return isSaved;
	}

	private void validateSignUpDetails(SignUpVO userDetails) throws GlobalException {
		if (Boolean.TRUE.equals(loginRepo.isUserExists(userDetails.getEmailId()))) {
			throw new GlobalException(Constants.USER_EXISTS_SIGNUP_EXP, HttpStatus.BAD_REQUEST);
		}
		if (!userDetails.getCreatePassword().equals(userDetails.getConfirmPassword())) {
			throw new GlobalException(Constants.CREATE_CONFIRM_PASS_MISSMATCH_EXP, HttpStatus.BAD_REQUEST);
		}
		userDetails.setConfirmPassword(secure.getEncrypted(userDetails.getConfirmPassword()));
	}

	@Override
	public Boolean createUser(SignUpVO userDetails) throws GlobalException {
		if (Boolean.TRUE.equals(loginRepo.isUserExists(userDetails.getEmailId()))) {
			throw new GlobalException(Constants.USER_EXISTS_SIGNUP_EXP, HttpStatus.BAD_REQUEST);
		}
		userDetails.setCreatePassword("****");
		userDetails.setConfirmPassword("****");
		Boolean isUserSaved = userRepo.saveSignUpUser(userDetails);
		return isUserSaved && loginRepo.saveLoginDetails(userDetails);
	}

}
