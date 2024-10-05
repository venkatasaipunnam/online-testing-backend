package com.clarku.ot.repository;

import com.clarku.ot.exception.GlobalException;
import com.clarku.ot.vo.SessionVO;

public interface ISessionRepo {

	SessionVO retrieveSessionDetails(Integer userId) throws GlobalException;

	SessionVO retrieveSession(String sessionId) throws GlobalException;

	Boolean createSession(Integer userId) throws GlobalException;

	Boolean isActiveSessionExists(Integer userId) throws GlobalException;

	void endSession(String sessionId) throws GlobalException;

	void updateSession(String sessionId) throws GlobalException;

	Boolean isSessionActive(String sessionId) throws GlobalException;

}