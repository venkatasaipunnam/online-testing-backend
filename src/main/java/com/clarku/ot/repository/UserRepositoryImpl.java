package com.clarku.ot.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import com.clarku.ot.vo.SignUpVO;
import com.clarku.ot.vo.UserProfileVO;
import com.clarku.ot.vo.UserVO;

import lombok.extern.log4j.Log4j2;

@Repository
@Log4j2
public class UserRepositoryImpl implements IUserRepo{

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private static final String USER_ID = "userId";

	@Override
	public String getUserFirstName(Integer userId) throws GlobalException {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, userId);
		String firstName;
		try {
			firstName = namedParameterJdbcTemplate.queryForObject(SqlProperties.user.get("getUserFirstNameById"), parameters, String.class);
		} catch (DataAccessException exp) {
			log.error("UserRepositoryImpl :: getUserFirstName(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("UserRepositoryImpl :: getUserFirstName(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return firstName;
	}

	@Override
	public UserVO retrieveUserDetails(Integer userId) throws GlobalException {
		UserVO userDetails = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, userId);
		try {
			userDetails = namedParameterJdbcTemplate.queryForObject(SqlProperties.user.get("getUserProfDetailsById"), parameters, new BeanPropertyRowMapper<UserVO>(UserVO.class));
		} catch (DataAccessException exp) {
			log.error("UserRepositoryImpl :: retrieveUserDetails(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("UserRepositoryImpl :: retrieveUserDetails(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return userDetails;
	}

	@Override
	public Boolean saveSignUpUser(SignUpVO userDetails) throws GlobalException {
		Integer updateCount = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("firstName", userDetails.getFirstName());
		parameters.addValue("lastName", userDetails.getLastName());
		parameters.addValue("emailId", userDetails.getEmailId().toLowerCase());
		parameters.addValue("type", userDetails.getUserType());
		try {
			updateCount = namedParameterJdbcTemplate.update(SqlProperties.user.get("saveUserProfDetails"), parameters);
		} catch (DataAccessException exp) {
			log.error("UserRepositoryImpl :: saveSignUpUser(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("UserRepositoryImpl :: saveSignUpUser(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return updateCount != 0;
	}

	@Override
	public UserProfileVO retrieveUserLoginDetails(Integer userId) throws GlobalException {
		UserProfileVO userLoginDetails = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, userId);
		try {
			userLoginDetails = namedParameterJdbcTemplate.queryForObject(SqlProperties.login.get("getLoginDetails"), parameters, new BeanPropertyRowMapper<UserProfileVO>(UserProfileVO.class));
		} catch (DataAccessException exp) {
			log.error("UserRepositoryImpl :: retrieveUserLoginDetails(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("UserRepositoryImpl :: retrieveUserLoginDetails(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return userLoginDetails;
	}

	@Override
	public Boolean updateUserPass(Integer userId, String password) throws GlobalException {
		Integer updateCount = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, userId);
		parameters.addValue("password", password);
		try {
			updateCount = namedParameterJdbcTemplate.update(SqlProperties.login.get("saveUserPass"), parameters);
		} catch (DataAccessException exp) {
			log.error("UserRepositoryImpl :: updateUserPass(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("UserRepositoryImpl :: updateUserPass(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return updateCount != 0;
	}

	@Override
	public Boolean updateUserProf(Integer userId, HashMap<String, String> updatedFields) throws GlobalException {
		int updateCount = 0;
		Boolean isFirstDone = false;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		StringBuilder updateUserQuery = new StringBuilder(SqlProperties.user.get("updateUserDynamicFields"));

		parameters.addValue(USER_ID, userId);
		if(updatedFields.containsKey("firstName")) {
			parameters.addValue("firstName", updatedFields.get("firstName"));
			updateUserQuery.append(" first_name = :firstName ");
			isFirstDone = true;
		}
		if (updatedFields.containsKey("lastName")) {
			parameters.addValue("lastName", updatedFields.get("lastName"));
			if (Boolean.TRUE.equals(isFirstDone)) { 
				updateUserQuery.append(",");
			}
			updateUserQuery.append(" last_name = :lastName ");
			isFirstDone = true;
		}
		if (updatedFields.containsKey("emailId")) {
			parameters.addValue("emailId", updatedFields.get("emailId"));
			if (Boolean.TRUE.equals(isFirstDone)) { 
				updateUserQuery.append(",");
			}
			updateUserQuery.append(" email = :emailId ");
			isFirstDone = true;
		}
		updateUserQuery.append(" WHERE user_id = :userId;");
		try {
			if (Boolean.TRUE.equals(isFirstDone)) {
				updateCount = namedParameterJdbcTemplate.update(updateUserQuery.toString(), parameters);
			}
		} catch (DataAccessException exp) {
			log.error("UserRepositoryImpl :: updateUserProf(): data access exception {} {}", exp.getMessage(), exp.getCause());
		} catch (Exception exp) {
			log.error("UserRepositoryImpl :: updateUserProf(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return updateCount != 0;
	}

	@Override
	public List<UserProfileVO> getUsers() throws GlobalException {
		List<UserProfileVO> allUsers = new ArrayList<>();
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		try {
			allUsers = namedParameterJdbcTemplate.query(SqlProperties.admin.get("getAllUsers"), parameters, new BeanPropertyRowMapper<>(UserProfileVO.class));
		} catch (DataAccessException exp) {
			log.error("UserRepositoryImpl :: getUsers(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("UserRepositoryImpl :: getUsers(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return allUsers;
	}

}