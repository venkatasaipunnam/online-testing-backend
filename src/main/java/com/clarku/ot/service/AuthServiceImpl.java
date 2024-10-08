package com.clarku.ot.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.clarku.ot.exception.GlobalException;
import com.clarku.ot.repository.ISessionRepo;
import com.clarku.ot.utils.Constants;
import com.clarku.ot.vo.SessionVO;

@Service
public class AuthServiceImpl implements IAuthService {

	@Autowired
	ISessionRepo sessionRepo;

	@Override
	public SessionVO retrieveSession(HttpHeaders headers) throws GlobalException {
		List<String> sessionId = headers.get(Constants.HEADER_SESSION_ID);
		if(sessionId == null || sessionId.isEmpty()) {
			throw new GlobalException("Invalid Session, Please login", HttpStatus.UNAUTHORIZED);
		}
		SessionVO session = sessionRepo.retrieveSession(String.valueOf(UUID.fromString(sessionId.get(0))));
		if(session == null || Boolean.TRUE.equals(ObjectUtils.isEmpty(session)) ) {
			throw new GlobalException("Invalid Session", HttpStatus.UNAUTHORIZED);
		}
		return session;
	}

}