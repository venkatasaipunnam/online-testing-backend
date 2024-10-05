package com.clarku.ot.service;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clarku.ot.exception.GlobalException;
import com.clarku.ot.utils.Constants;
import com.clarku.ot.utils.EmailHelper;
import com.clarku.ot.vo.CustomMessageVO;
import com.clarku.ot.vo.EmailVO;
import com.clarku.ot.vo.SignUpVO;
import com.clarku.ot.vo.UserVO;


@Service
public class NotificationServiceImpl implements INotificationService {

	@Autowired
	EmailHelper email;

	private static final String FIRST_NAME = "firstName";

	@Override
	public void sendSuccessSignUpMail(SignUpVO userDetails) throws GlobalException {
		EmailVO emailVO = new EmailVO();
		emailVO.setSendTo(userDetails.getEmailId());
		emailVO.setSubject(Constants.WELCOME_SUBJECT);
		emailVO.setTemplateName(userDetails.getUserType().equalsIgnoreCase(Constants.INSTRUCTOR) ? Constants.SIGNUP_INSTRUCTOR_SUCCESS_TEMPLATE : Constants.SIGNUP_SUCCESS_TEMPLATE);
		HashMap<String, String> variables = new HashMap<>();
		variables.put(FIRST_NAME, userDetails.getFirstName());
		variables.put("emailId", userDetails.getEmailId());
		emailVO.setVariables(variables);
		email.sendEMail(emailVO);
	}

	@Override
	public void sendSuccessResetPassEmail(String emailId, String firstName, String tempPass) throws GlobalException {
		EmailVO emailVO = new EmailVO();
		emailVO.setSendTo(emailId);
		emailVO.setSubject(Constants.RESET_PASS_SUBJECT);
		emailVO.setTemplateName(Constants.RESET_PASS_SUCCESS_TEMPLATE);
		HashMap<String, String> variables = new HashMap<>();
		variables.put(FIRST_NAME, firstName);
		variables.put("tempPass", tempPass);
		emailVO.setVariables(variables);
		email.sendEMail(emailVO);
	}

	@Override
	public void sendPasswordChangeEmail(String emailId, String firstName) throws GlobalException {
		EmailVO emailVO = new EmailVO();
		emailVO.setSendTo(emailId);
		emailVO.setSubject(Constants.USER_PASS_CHANGED_SUB);
		emailVO.setTemplateName(Constants.USER_PASS_CHANGE_TEMPLATE);
		HashMap<String, String> variables = new HashMap<>();
		variables.put(FIRST_NAME, firstName);
		emailVO.setVariables(variables);
		email.sendEMail(emailVO);
	}

	@Override
	public void sendProfileUpdateEmail(String emailId, String firstName) throws GlobalException {
		EmailVO emailVO = new EmailVO();
		emailVO.setSendTo(emailId);
		emailVO.setSubject(Constants.USER_PROFILE_CHANGED_SUB);
		emailVO.setTemplateName(Constants.USER_PROFILE_CHANGE_TEMPLATE);
		HashMap<String, String> variables = new HashMap<>();
		variables.put(FIRST_NAME, firstName);
		emailVO.setVariables(variables);
		email.sendEMail(emailVO);
	}

	@Override
	public void sendCustomMessageToUsers(UserVO user, CustomMessageVO messageVO)
			throws GlobalException {
		EmailVO emailVO = new EmailVO();
		emailVO.setSendMultipleBCCTo(messageVO.getSendTo());
		emailVO.setSubject(messageVO.getSubject());
		emailVO.setTemplateName(Constants.CUSTOM_MESSAGE_NOTIFCATION);
		HashMap<String, String> variables = new HashMap<>();
		variables.put("message", messageVO.getMessage());
		variables.put("userName", user.getFirstName());
		emailVO.setVariables(variables);
		email.sendEMailMultipleBCC(emailVO);
		
	}

	@Override
	public void sendSuccessCreateUserMail(SignUpVO createdUser) throws GlobalException {
		EmailVO emailVO = new EmailVO();
		emailVO.setSendTo(createdUser.getEmailId());
		emailVO.setSubject(Constants.WELCOME_SUBJECT);
		emailVO.setTemplateName(createdUser.getUserType().equalsIgnoreCase(Constants.INSTRUCTOR) ? Constants.SIGNUP_INSTRUCTOR_SUCCESS_TEMPLATE : Constants.SIGNUP_SUCCESS_TEMPLATE);
		HashMap<String, String> variables = new HashMap<>();
		variables.put(FIRST_NAME, createdUser.getFirstName());
		variables.put("emailId", createdUser.getEmailId());
		emailVO.setVariables(variables);
		email.sendEMail(emailVO);
	}

	@Override
	public void sendSuccessTemporaryPassword(SignUpVO createdUser) throws GlobalException {
		EmailVO emailVO = new EmailVO();
		emailVO.setSendTo(createdUser.getEmailId());
		emailVO.setSubject(Constants.TEMP_PASS_SUBJECT);
		emailVO.setTemplateName(Constants.TEMP_PASS_SUCCESS_TEMPLATE);
		HashMap<String, String> variables = new HashMap<>();
		variables.put(FIRST_NAME, createdUser.getFirstName());
		variables.put("tempPass", createdUser.getCreatePassword());
		emailVO.setVariables(variables);
		email.sendEMail(emailVO);
	}

}
