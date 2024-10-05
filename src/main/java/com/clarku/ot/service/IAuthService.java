package com.clarku.ot.service;

import org.springframework.http.HttpHeaders;

import com.clarku.ot.exception.GlobalException;
import com.clarku.ot.vo.SessionVO;

public interface IAuthService {
	
	SessionVO retrieveSession(HttpHeaders headers) throws GlobalException;

}