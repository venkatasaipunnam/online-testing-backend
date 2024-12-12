package com.clarku.ot.repository;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.clarku.ot.config.SqlProperties;
import com.clarku.ot.exception.GlobalException;
import com.clarku.ot.utils.Constants;
import com.clarku.ot.vo.SessionVO;

import lombok.extern.log4j.Log4j2;

@Repository
@Log4j2
public class SessionRepositoryImpl implements ISessionRepo {

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private static final String USER_ID = "userId";

	private static final String SESSION_ID = "sessionId";

	private static final String CURRENT_TIME = "currentTime";

	@Override
	public SessionVO retrieveSessionDetails(Integer userId) throws GlobalException {
		SessionVO sessionDetails;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, userId);
		parameters.addValue(CURRENT_TIME, LocalDateTime.now());
		try {
			sessionDetails = namedParameterJdbcTemplate.queryForObject(SqlProperties.session.get("getSessionDetailsByUserId"), parameters, new BeanPropertyRowMapper<SessionVO>(SessionVO.class));
		} catch (DataAccessException exp) {
			log.error("SessionRepositoryImpl :: retrieveSessionDetails(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INVALID_SESSION_EXP, HttpStatus.UNAUTHORIZED);
		} catch (Exception exp) {
			log.error("SessionRepositoryImpl :: retrieveSessionDetails(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return sessionDetails;
	}

	@Override
	public Boolean createSession(Integer userId) throws GlobalException {
		int insertedCount = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, userId);
		parameters.addValue(CURRENT_TIME, LocalDateTime.now());
		try {
			insertedCount = namedParameterJdbcTemplate.update(SqlProperties.session.get("saveUserSession"), parameters);
		} catch (DataAccessException exp) {
			log.error("SessionRepositoryImpl :: createSession(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("SessionRepositoryImpl :: createSession(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return insertedCount != 0;
	}

	@Override
	public Boolean isActiveSessionExists(Integer userId) throws GlobalException {
		int count = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, userId);
		parameters.addValue(CURRENT_TIME, LocalDateTime.now());
		try {
			count = namedParameterJdbcTemplate.queryForObject(SqlProperties.session.get("isActiveUserSessionExists"), parameters, Integer.class);
		} catch (DataAccessException exp) {
			log.error("SessionRepositoryImpl :: isActiveSessionExists(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("SessionRepositoryImpl :: isActiveSessionExists(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return count != 0;
	}

	@Override
	public void endSession(String sessionId) throws GlobalException {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(SESSION_ID, sessionId);
		parameters.addValue(CURRENT_TIME, LocalDateTime.now());
		try {
			namedParameterJdbcTemplate.update(SqlProperties.session.get("endUserSessionById"), parameters);
		} catch (DataAccessException exp) {
			log.error("SessionRepositoryImpl :: endSession(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("SessionRepositoryImpl :: endSession(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public void updateSession(String sessionId) throws GlobalException {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(SESSION_ID, sessionId);
		try {
			namedParameterJdbcTemplate.update(SqlProperties.session.get("updateUserSessionById"), parameters);
		} catch (DataAccessException exp) {
			log.error("SessionRepositoryImpl :: updateSession(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("SessionRepositoryImpl :: updateSession(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public Boolean isSessionActive(String sessionId) throws GlobalException {
		int count = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(SESSION_ID, sessionId);
		parameters.addValue(CURRENT_TIME, LocalDateTime.now().toString());

		log.info(sessionId);
		log.info(LocalDateTime.now());
		try {
			count = namedParameterJdbcTemplate.queryForObject(SqlProperties.session.get("isSessionActive"), parameters, Integer.class);
		} catch (DataAccessException exp) {
			log.error("SessionRepositoryImpl :: isSessionActive(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("SessionRepositoryImpl :: isSessionActive(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		log.info(count);
		return count != 0;
	}

	@Override
	public SessionVO retrieveSession(String sessionId) throws GlobalException {
		SessionVO sessionDetails = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(SESSION_ID, sessionId);
		parameters.addValue(CURRENT_TIME, LocalDateTime.now());
		try {
			sessionDetails = namedParameterJdbcTemplate.queryForObject(SqlProperties.session.get("getSessionDetailsById"), parameters, new BeanPropertyRowMapper<SessionVO>(SessionVO.class));
		} catch (DataAccessException exp) {
			log.error("SessionRepositoryImpl :: retrieveSession(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("SessionRepositoryImpl :: retrieveSession(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return sessionDetails;
	}

}
