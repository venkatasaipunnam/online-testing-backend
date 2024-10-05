package com.clarku.ot.service;

import com.clarku.ot.exception.GlobalException;
import com.clarku.ot.vo.CustomMessageVO;
import com.clarku.ot.vo.SignUpVO;
import com.clarku.ot.vo.UserVO;

public interface INotificationService {

	void sendSuccessSignUpMail(SignUpVO userDetails) throws GlobalException;

	void sendSuccessResetPassEmail(String emailId, String firstName, String tempPass) throws GlobalException;

	void sendPasswordChangeEmail(String emailId, String firstName) throws GlobalException;

	void sendProfileUpdateEmail(String emailId, String firstName) throws GlobalException;

	void sendCustomMessageToUsers(UserVO user, CustomMessageVO messageVO) throws GlobalException;

	void sendSuccessCreateUserMail(SignUpVO createdUser) throws GlobalException;

	void sendSuccessTemporaryPassword(SignUpVO createdUser) throws GlobalException;

}
