package com.clarku.ot.service;

import com.clarku.ot.exception.GlobalException;
import com.clarku.ot.exception.LoginException;
import com.clarku.ot.vo.LoginVO;
import com.clarku.ot.vo.SignUpVO;
import com.clarku.ot.vo.UserVO;

public interface ILoginService {

	UserVO signIn(LoginVO loginDetails) throws GlobalException, LoginException;

	void updateLastLogin(Integer integer) throws LoginException;

	Boolean signUpUser(SignUpVO userDetails) throws GlobalException;

	Boolean createUser(SignUpVO createdUser) throws GlobalException;

}