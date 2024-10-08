package com.clarku.ot.service;

import com.clarku.ot.exception.GlobalException;
import com.clarku.ot.vo.SessionVO;

public interface ISessionService {

	SessionVO createSession(Integer userId) throws GlobalException;

	SessionVO getSession(Integer userId) throws GlobalException;

	void endSession(String sessionId) throws GlobalException;

	void extendSession(String sessionId) throws GlobalException;

	void checkAndExtendSession(String sessionId) throws GlobalException;

	Boolean isSessionExists(Integer userId) throws GlobalException;

}