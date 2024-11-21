package com.clarku.ot.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.clarku.ot.config.SqlProperties;
import com.clarku.ot.exception.GlobalException;
import com.clarku.ot.utils.Constants;
import com.clarku.ot.vo.ExamKeyVO;
import com.clarku.ot.vo.ExamRespondentVO;
import com.clarku.ot.vo.ResponseFeedbackVO;
import com.clarku.ot.vo.StudentResponseVO;
import com.clarku.ot.vo.UserVO;

import lombok.extern.log4j.Log4j2;

@Repository
@Log4j2
public class ExamResponseRepositoryImpl implements IExamResponseRepo {

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private static final String USER_ID = "userId";

	private static final String EXAM_ID = "examId";

	

	@Override
	public List<ExamRespondentVO> retreiveExamTakers(Integer examId) throws GlobalException {
		List<ExamRespondentVO> allUsers = new ArrayList<>();
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(EXAM_ID, examId);
		try {
			allUsers = namedParameterJdbcTemplate.query(SqlProperties.response.get("getExamTakers"), parameters, new BeanPropertyRowMapper<>(ExamRespondentVO.class));
		} catch (DataAccessException exp) {
			log.error("ExamResponseRepositoryImpl :: retreiveExamTakers(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("ExamResponseRepositoryImpl :: retreiveExamTakers(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return allUsers;
	}



	@Override
	public List<ExamKeyVO> retrieveExamKey(Integer examId) throws GlobalException {
		List<ExamKeyVO> examKey = new ArrayList<>();
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(EXAM_ID, examId);
		try {
			examKey = namedParameterJdbcTemplate.query(SqlProperties.response.get("getExamKey"), parameters, new BeanPropertyRowMapper<>(ExamKeyVO.class));
		} catch (DataAccessException exp) {
			log.error("ExamResponseRepositoryImpl :: retrieveExamKey(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("ExamResponseRepositoryImpl :: retrieveExamKey(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return examKey;
	}



	@Override
	public List<ResponseFeedbackVO> retrieveExamFeedbacks(Integer examId) throws GlobalException {
		List<ResponseFeedbackVO> feedbacks = new ArrayList<>();
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(EXAM_ID, examId);
		try {
			feedbacks = namedParameterJdbcTemplate.query(SqlProperties.grade.get("getResponseFeedbacksbyExamId"), parameters, new BeanPropertyRowMapper<>(ResponseFeedbackVO.class));
		} catch (DataAccessException exp) {
			log.error("ExamResponseRepositoryImpl :: retrieveExamFeedbacks(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("ExamResponseRepositoryImpl :: retrieveExamFeedbacks(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return feedbacks;
	}


	@Override
	public List<ResponseFeedbackVO> retrieveExamFeedbacksBySession(UUID examSession) throws GlobalException {
		List<ResponseFeedbackVO> feedbacks = new ArrayList<>();
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("examSession", examSession);
		try {
			feedbacks = namedParameterJdbcTemplate.query(SqlProperties.grade.get("getResponseFeedbacksbyExamId"), parameters, new BeanPropertyRowMapper<>(ResponseFeedbackVO.class));
		} catch (DataAccessException exp) {
			log.error("ExamResponseRepositoryImpl :: retrieveExamFeedbacksBySession(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("ExamResponseRepositoryImpl :: retrieveExamFeedbacksBySession(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return feedbacks;
	}

	@Override
	public List<StudentResponseVO> retrieveStudentResponses(Integer examId) throws GlobalException {
		List<StudentResponseVO> feedbacks = new ArrayList<>();
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(EXAM_ID, examId);
		try {
			feedbacks = namedParameterJdbcTemplate.query(SqlProperties.response.get("getStudentResponsesbyExamId"), parameters, new BeanPropertyRowMapper<>(StudentResponseVO.class));
		} catch (DataAccessException exp) {
			log.error("ExamResponseRepositoryImpl :: retrieveStudentResponses(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("ExamResponseRepositoryImpl :: retrieveStudentResponses(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return feedbacks;
	}
	

	@Override
	public List<StudentResponseVO> retrieveStudentResponsesByStudentSession(UUID examSession) throws GlobalException {
		List<StudentResponseVO> feedbacks = new ArrayList<>();
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("examSession", examSession);
		try {
			feedbacks = namedParameterJdbcTemplate.query(SqlProperties.response.get("getStudentResponsesbySession"), parameters, new BeanPropertyRowMapper<>(StudentResponseVO.class));
		} catch (DataAccessException exp) {
			log.error("ExamResponseRepositoryImpl :: retrieveStudentResponsesByStudentSession(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("ExamResponseRepositoryImpl :: retrieveStudentResponsesByStudentSession(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return feedbacks;
	}



	@Override
	public Boolean saveExamFeedback(UserVO user, StudentResponseVO response, ResponseFeedbackVO feedback) throws GlobalException {
		int insertedCount = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, user.getUserId());
		parameters.addValue("responseId", response.getResponseId());
		parameters.addValue("feedback", feedback.getFeedback());
		parameters.addValue("isCorrect", response.getIsCorrect());
		parameters.addValue("points", response.getPointsGained());
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			insertedCount = namedParameterJdbcTemplate.update(SqlProperties.grade.get("saveResponseFeedback"), parameters, keyHolder, new String[] { "feedback_id" });
			feedback.setFeedbackId(keyHolder.getKey().intValue());
		} catch (DataAccessException exp) {
			log.error("ExamResponseRepositoryImpl :: saveExamFeedback(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("ExamResponseRepositoryImpl :: saveExamFeedback(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return insertedCount != 0;
	}

}
