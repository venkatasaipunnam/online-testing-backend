package com.clarku.ot.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.clarku.ot.exception.GlobalException;
import com.clarku.ot.repository.IExamRepo;
import com.clarku.ot.repository.ISessionRepo;
import com.clarku.ot.utils.Constants;
import com.clarku.ot.vo.ExamSessionVO;
import com.clarku.ot.vo.SessionVO;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class AuthServiceImpl implements IAuthService {

	@Autowired
	ISessionRepo sessionRepo;

	@Autowired
	IExamRepo examRepo;

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

	@Override
	public ExamSessionVO retrieveExamSession(HttpHeaders headers) throws GlobalException {
		List<String> examSessionId = headers.get(Constants.HEADER_EXAM_SESSION_ID);
		if(examSessionId == null || examSessionId.isEmpty()) {
			log.error("Invalid Exam Session");
			return null;
		}
		ExamSessionVO session = examRepo.retrieveUserExamSessionDetails(String.valueOf(UUID.fromString(examSessionId.get(0))));
		if(session == null || Boolean.TRUE.equals(ObjectUtils.isEmpty(session)) ) {
			log.error("Invalid Exam Session");
			return null;
		}
		return session;
	}

}