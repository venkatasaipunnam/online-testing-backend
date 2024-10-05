package com.clarku.ot.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.clarku.ot.config.SqlProperties;
import com.clarku.ot.exception.GlobalException;
import com.clarku.ot.exception.LoginException;
import com.clarku.ot.utils.Constants;
import com.clarku.ot.vo.SignUpVO;
import com.clarku.ot.vo.UserProfileVO;

import lombok.extern.log4j.Log4j2;

@Repository
@Log4j2
public class LoginRepositoryImpl implements ILoginRepo {

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private static final String USER_ID = "userId";

	private static final String EMAIL_ID = "emailId";

	@Override
	public UserProfileVO retrieveUserLogin(String emailId) throws GlobalException {
		UserProfileVO userLoginDetails = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(EMAIL_ID, emailId.toLowerCase());
		try {
			userLoginDetails = namedParameterJdbcTemplate.queryForObject(SqlProperties.login.get("getLoginDetailsByEmailId"), parameters, new BeanPropertyRowMapper<UserProfileVO>(UserProfileVO.class));
		} catch (DataAccessException exp) {
			log.error("LoginRepositoryImpl :: retrieveUserLogin(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("LoginRepositoryImpl :: retrieveUserLogin(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return userLoginDetails;
	}

	@Override
	public UserProfileVO retrieveUserLoginbyUsername(String username) throws GlobalException {
		UserProfileVO userLoginDetails = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("username", username);
		try {
			userLoginDetails = namedParameterJdbcTemplate.queryForObject(SqlProperties.login.get("getLoginDetailsByUsername"), parameters, new BeanPropertyRowMapper<UserProfileVO>(UserProfileVO.class));
		} catch (DataAccessException exp) {
			log.error("LoginRepositoryImpl :: retrieveUserLoginbyUsername(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("LoginRepositoryImpl :: retrieveUserLoginbyUsername(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return userLoginDetails;
	}

	@Override
	public void lockUserAccount(Integer userId) throws LoginException {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, userId);
		parameters.addValue("locked", true);
		try {
			namedParameterJdbcTemplate.update(SqlProperties.login.get("lockUserAccount"), parameters);
		} catch (DataAccessException exp) {
			log.error("LoginRepositoryImpl :: lockUserAccount(): data access exception {}", exp.getMessage());
			throw new LoginException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("LoginRepositoryImpl :: lockUserAccount(): exception {}", exp.getMessage());
			throw new LoginException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public void updateUnSuccessAttempt(Integer userId) throws LoginException {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, userId);
		try {
			namedParameterJdbcTemplate.update(SqlProperties.login.get("updateFailedAttempts"), parameters);
		} catch (DataAccessException exp) {
			log.error("LoginRepositoryImpl :: updateUnSuccessAttempt(): data access exception {}", exp.getMessage());
			throw new LoginException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("LoginRepositoryImpl :: updateUnSuccessAttempt(): exception {}", exp.getMessage());
			throw new LoginException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public void saveLastLogin(Integer userId) throws LoginException {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, userId);
		try {
			namedParameterJdbcTemplate.update(SqlProperties.login.get("saveLastLogin"), parameters);
		} catch (DataAccessException exp) {
			log.error("LoginRepositoryImpl :: saveLastLogin(): data access exception {}", exp.getMessage());
			throw new LoginException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("LoginRepositoryImpl :: saveLastLogin(): exception {}", exp.getMessage());
			throw new LoginException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public void resetUserAttemptsStatus(Integer userId) throws LoginException {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, userId);
		try {
			namedParameterJdbcTemplate.update(SqlProperties.login.get("resetUserAttempts"), parameters);
		} catch (DataAccessException exp) {
			log.error("LoginRepositoryImpl :: resetUserAttemptsStatus(): data access exception {}", exp.getMessage());
			throw new LoginException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("LoginRepositoryImpl :: resetUserAttemptsStatus(): exception {}", exp.getMessage());
			throw new LoginException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public Boolean isUserExists(String emailId) throws GlobalException {
		int count = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(EMAIL_ID, emailId.toLowerCase());
		try {
			count = namedParameterJdbcTemplate.queryForObject(SqlProperties.user.get("isUserExistsByEmail"), parameters, Integer.class);
		} catch (DataAccessException exp) {
			log.error("LoginRepositoryImpl :: isUserExists(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("LoginRepositoryImpl :: isUserExists(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return count != 0;
	}

	@Override
	public Integer getUserId(String emailId) throws GlobalException {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(EMAIL_ID, emailId.toLowerCase());
		Integer userId;
		try {
			userId = namedParameterJdbcTemplate.queryForObject(SqlProperties.user.get("getUserIdByEmail"), parameters, Integer.class);
		} catch (DataAccessException exp) {
			log.error("LoginRepositoryImpl :: getUserId(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("LoginRepositoryImpl :: getUserId(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return userId;
	}

	@Override
	public Boolean saveTempPassword(Integer userId, String tempPass) throws GlobalException {
		int updatedCount = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, userId);
		parameters.addValue("tempPassword", tempPass);
		try {
			updatedCount = namedParameterJdbcTemplate.update(SqlProperties.login.get("saveTempPassQuery"), parameters);
		} catch (DataAccessException exp) {
			log.error("LoginRepositoryImpl :: saveTempPassword(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("LoginRepositoryImpl :: saveTempPassword(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return updatedCount == 1;
	}

	@Override
	public Boolean saveLoginDetails(SignUpVO userDetails) throws GlobalException {
		int updatedCount = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, userDetails.getUserId());
		parameters.addValue("username", userDetails.getUserName());
		parameters.addValue("password", userDetails.getConfirmPassword());
		try {
			updatedCount = namedParameterJdbcTemplate.update(SqlProperties.login.get("saveLoginDetails"), parameters);
		} catch (DataAccessException exp) {
			log.error("LoginRepositoryImpl :: saveLoginDetails(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("LoginRepositoryImpl :: saveLoginDetails(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return updatedCount == 1;
	}
	
	@Override
	public Boolean savePasswordDetails(SignUpVO userDetails) throws GlobalException {
		int updatedCount = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, userDetails.getUserId());
		parameters.addValue("password", userDetails.getConfirmPassword());
		try {
			updatedCount = namedParameterJdbcTemplate.update(SqlProperties.login.get("savePasswordDetails"), parameters);
		} catch (DataAccessException exp) {
			log.error("LoginRepositoryImpl :: saveLoginDetails(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("LoginRepositoryImpl :: saveLoginDetails(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return updatedCount == 1;
	}


}