package com.clarku.ot.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.clarku.ot.exception.GlobalException;
import com.clarku.ot.repository.ILoginRepo;
import com.clarku.ot.repository.IUserRepo;
import com.clarku.ot.utils.Constants;
import com.clarku.ot.utils.Secure;
import com.clarku.ot.vo.ChangePassVO;
import com.clarku.ot.vo.UserProfileVO;
import com.clarku.ot.vo.UserVO;

import io.micrometer.common.util.StringUtils;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class UserServiceImpl implements IUserService {

	@Autowired
	private IUserRepo userRepo;

	@Autowired
	private ILoginRepo loginRepo;

	@Autowired
	Secure secure;

	@Autowired
	INotificationService notify;

	@Override
	public Boolean resetPassword(String emailId) throws GlobalException {
		UserProfileVO userDetails = loginRepo.retrieveUserLogin(emailId);
		if (userDetails == null) {
			log.error("LoginServiceImpl:: forgetPassword(): User Not Exixts with emailId : {}", emailId);
			throw new GlobalException(Constants.USER_NOT_EXISTS_FORGET_PASS_EXP, HttpStatus.BAD_REQUEST);
		}
//		if (Boolean.TRUE.equals(userDetails.getIsLocked())) {
//			log.error("LoginServiceImpl:: forgetPassword(): User Locked : {}", emailId);
//			throw new GlobalException(Constants.USER_LOCKED_EXP, HttpStatus.BAD_REQUEST);
//		}
		return generateAndSaveTemporaryPass(userDetails);
	}

	private Boolean generateAndSaveTemporaryPass(UserProfileVO userLoginDetails) throws GlobalException {
		String tempPass = secure.generateTempPass();
		String userFirstName = userRepo.getUserFirstName(userLoginDetails.getUserId());
		Boolean isSaved = loginRepo.saveTempPassword(userLoginDetails.getUserId(), secure.getEncrypted(tempPass));
		if (Boolean.TRUE.equals(isSaved)) {
			notify.sendSuccessResetPassEmail(userLoginDetails.getEmailId(), userFirstName, tempPass);
		}
		return isSaved;
	}

	@Override
	public UserVO getUser(Integer userId) throws GlobalException {
		return userRepo.retrieveUserDetails(userId);
	}

	@Override
	public Boolean changePassword(Integer userId, ChangePassVO passVO) throws GlobalException {
		UserProfileVO loginDetails = userRepo.retrieveUserLoginDetails(userId);
		if (loginDetails.getTempPassword() != null && !StringUtils.isBlank(loginDetails.getTempPassword())) {
			if (!secure.getEncrypted(passVO.getCurrentPassword()).equals(loginDetails.getTempPassword())) {
				throw new GlobalException("Wrong Current Password", HttpStatus.BAD_REQUEST);
			}
		}else {
			if (!secure.getEncrypted(passVO.getCurrentPassword()).equals(loginDetails.getPassword())) {
				throw new GlobalException("Wrong Current Password", HttpStatus.BAD_REQUEST);
			}
		}
		if (passVO.getCurrentPassword().equals(passVO.getCreatePassword())) {
			throw new GlobalException("Existing and New password should not be same", HttpStatus.BAD_REQUEST);
		}
		if (!passVO.getCreatePassword().equals(passVO.getConfirmPassword())) {
			throw new GlobalException("Create and Confirm password should be same", HttpStatus.BAD_REQUEST);
		}
		return userRepo.updateUserPass(userId, secure.getEncrypted(passVO.getCreatePassword()));
	}

	@Override
	public Boolean updateUser(UserVO existingUser, UserVO updatedUser) throws GlobalException {
		HashMap<String, String> updatedDetails = new HashMap<>();
		if (!existingUser.getFirstName().equals(updatedUser.getFirstName())) {
			updatedDetails.put("firstName", updatedUser.getFirstName());
		}
		if (!existingUser.getLastName().equals(updatedUser.getLastName())) {
			updatedDetails.put("lastName", updatedUser.getLastName());
		}
		if (!existingUser.getEmailId().equals(updatedUser.getEmailId())) {
			updatedDetails.put("emailId", updatedUser.getEmailId());
		}
		
		return userRepo.updateUserProf(existingUser.getUserId(), updatedDetails);
	}

	@Override
	public List<UserProfileVO> getUsers() throws GlobalException {
		return userRepo.getUsers();
	}

	@Override
	public String generateTempPassword(Integer userId) throws GlobalException {
		String tempPass = secure.generateTempPass();
		loginRepo.saveTempPassword(userId, secure.getEncrypted(tempPass));
		return tempPass;
	}

}
