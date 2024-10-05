package com.clarku.ot.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.clarku.ot.exception.GlobalException;
import com.clarku.ot.repository.ISessionRepo;
import com.clarku.ot.utils.Constants;
import com.clarku.ot.vo.SessionVO;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class SessionServiceImpl implements ISessionService {

	@Autowired
	ISessionRepo sessionRepo;

	@Override
	public SessionVO createSession(Integer userId) throws GlobalException {
		if (Boolean.TRUE.equals(isSessionExists(userId))) {
			log.error("SessionServiceImpl :: createSession(): Session already exists.");
			throw new GlobalException(Constants.SESSION_ALREADY_EXISTS_EXP, HttpStatus.UNAUTHORIZED);
		}
		if (Boolean.FALSE.equals(sessionRepo.createSession(userId))) {
			log.error("SessionServiceImpl :: createSession() : Error in creating the session");
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return getSession(userId);
	}

	@Override
	public SessionVO getSession(Integer userId) throws GlobalException {
		SessionVO session = sessionRepo.retrieveSessionDetails(userId);
		session.setActive(session.getSessionEndTime().isAfter(LocalDateTime.now()));
		return session;
	}

	@Override
	public void endSession(String sessionId) throws GlobalException {
		if (Boolean.FALSE.equals(sessionRepo.isSessionActive(sessionId))) {
			log.error("SessionServiceImpl :: endSession(): No Active Session Exists.");
			throw new GlobalException(Constants.INVALID_SESSION_EXP, HttpStatus.UNAUTHORIZED);
		}
		sessionRepo.endSession(sessionId);
	}

	@Override
	public void checkAndExtendSession(String sessionId) throws GlobalException {
		SessionVO session = sessionRepo.retrieveSession(sessionId);
		if (session.getSessionEndTime().isBefore(LocalDateTime.now().plusMinutes(60))) {
			extendSession(sessionId);
		}
	}

	@Override
	public void extendSession(String sessionId) throws GlobalException {
		sessionRepo.updateSession(sessionId);
	}

	@Override
	public Boolean isSessionExists(Integer userId) throws GlobalException {
		return sessionRepo.isActiveSessionExists(userId);
	}

}
