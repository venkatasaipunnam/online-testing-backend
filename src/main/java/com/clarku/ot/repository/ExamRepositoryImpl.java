package com.clarku.ot.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
import com.clarku.ot.vo.CreateExamVO;
import com.clarku.ot.vo.CreateOptionVO;
import com.clarku.ot.vo.CreateQuestionVO;
import com.clarku.ot.vo.ExamSessionVO;
import com.clarku.ot.vo.ExamVO;
import com.clarku.ot.vo.OptionVO;
import com.clarku.ot.vo.QuestionVO;
import com.clarku.ot.vo.UpdateExamVO;
import com.clarku.ot.vo.UpdateOptionVO;
import com.clarku.ot.vo.UpdateQuestionVO;
import com.clarku.ot.vo.UserExamResponseVO;
import com.clarku.ot.vo.UserVO;

import lombok.extern.log4j.Log4j2;

@Repository
@Log4j2
public class ExamRepositoryImpl implements IExamRepo{

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private static final String USER_ID = "userId";

	@Override
	public Boolean createExam(CreateExamVO examVO, UserVO user) throws GlobalException {
		int insertedCount = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, user.getUserId());
		parameters.addValue("title", examVO.getTitle());
		parameters.addValue("description", examVO.getDescription());
		parameters.addValue("startTime", examVO.getStartTime());
		parameters.addValue("endTime", examVO.getEndTime());
		parameters.addValue("duration", examVO.getDuration());
		parameters.addValue("totalPoints", examVO.getTotalPoints());
		parameters.addValue("autoGrade", examVO.getAutoGrade());
		parameters.addValue("status", examVO.getStatus());
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			insertedCount = namedParameterJdbcTemplate.update(SqlProperties.exam.get("createExam"), parameters, keyHolder, new String[] { "exam_id" });
			examVO.setExamId(keyHolder.getKey().intValue());
		} catch (DataAccessException exp) {
			log.error("ExamRepositoryImpl :: createExam(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("ExamRepositoryImpl :: createExam(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return insertedCount != 0;
	}

	@Override
	public Integer createQuestion(CreateQuestionVO question, Integer userId) throws GlobalException {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, userId);
		parameters.addValue("title", question.getQuestionTitle());
		parameters.addValue("detailText", question.getQuestionDetails());
		parameters.addValue("points", question.getPoints());
		parameters.addValue("imagePath", question.getQuestionImagePath());
		parameters.addValue("type", question.getQuestionType());
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			namedParameterJdbcTemplate.update(SqlProperties.exam.get("createQuestion"), parameters, keyHolder, new String[] { "question_id" });
			return keyHolder.getKey().intValue();
		} catch (DataAccessException exp) {
			log.error("ExamRepositoryImpl :: createQuestion(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("ExamRepositoryImpl :: createQuestion(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public Integer createOption(CreateOptionVO option, Integer userId) throws GlobalException {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, userId);
		parameters.addValue("optionText", option.getOptionText());
		parameters.addValue("imagePath", option.getOptionImagePath());
		parameters.addValue("type", option.getOptionType());
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			namedParameterJdbcTemplate.update(SqlProperties.exam.get("createOption"), parameters, keyHolder, new String[] { "option_id" });
			return keyHolder.getKey().intValue();
		} catch (DataAccessException exp) {
			log.error("ExamRepositoryImpl :: createOption(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("ExamRepositoryImpl :: createOption(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public Boolean mapQuestionWithExam(Integer examId, Integer questionId) throws GlobalException {
		int updateCount = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("examId", examId);
		parameters.addValue("questionId", questionId);
		try {
			updateCount = namedParameterJdbcTemplate.update(SqlProperties.exam.get("mapQuestionWithExam"), parameters);
			return updateCount!=0;
		} catch (DataAccessException exp) {
			log.error("ExamRepositoryImpl :: mapQuestionWithExam(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("ExamRepositoryImpl :: mapQuestionWithExam(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public Boolean mapOptionWithQuestion(Integer questionId, Integer optionId, Boolean isCorrect) throws GlobalException {
		int updateCount = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("optionId", optionId);
		parameters.addValue("questionId", questionId);
		parameters.addValue("isCorrect", isCorrect);
		try {
			updateCount = namedParameterJdbcTemplate.update(SqlProperties.exam.get("mapOptionWithQuestion"), parameters);
			return updateCount!=0;
		} catch (DataAccessException exp) {
			log.error("ExamRepositoryImpl :: mapOptionWithQuestion(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("ExamRepositoryImpl :: mapOptionWithQuestion(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public Boolean mapUpdateOptionWithQuestion(Integer questionId, Integer optionId, Boolean isCorrect) throws GlobalException {
		int updateCount = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("optionId", optionId);
		parameters.addValue("questionId", questionId);
		parameters.addValue("isCorrect", isCorrect);
		try {
			updateCount = namedParameterJdbcTemplate.update(SqlProperties.exam.get("mapUpdateOptionWithQuestion"), parameters);
			return updateCount!=0;
		} catch (DataAccessException exp) {
			log.error("ExamRepositoryImpl :: mapOptionWithQuestion(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("ExamRepositoryImpl :: mapOptionWithQuestion(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public Boolean updateExam(UpdateExamVO examVO, UserVO user) throws GlobalException {
		int updateCount = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, user.getUserId());
		parameters.addValue("examId", examVO.getExamId());
		parameters.addValue("title", examVO.getTitle());
		parameters.addValue("description", examVO.getDescription());
		parameters.addValue("startTime", examVO.getStartTime());
		parameters.addValue("endTime", examVO.getEndTime());
		parameters.addValue("duration", examVO.getDuration());
		parameters.addValue("totalPoints", examVO.getTotalPoints());
		parameters.addValue("autoGrade", examVO.getAutoGrade());
		parameters.addValue("status", examVO.getStatus());
		try {
			updateCount = namedParameterJdbcTemplate.update(SqlProperties.exam.get("updateExam"), parameters);
		} catch (DataAccessException exp) {
			log.error("ExamRepositoryImpl :: updateExam(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("ExamRepositoryImpl :: updateExam(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return updateCount != 0;
	}

	@Override
	public Boolean updateQuestion(UpdateQuestionVO question, Integer userId) throws GlobalException {
		int updateCount = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, userId);
		parameters.addValue("title", question.getQuestionTitle());
		parameters.addValue("detailText", question.getQuestionDetails());
		parameters.addValue("points", question.getPoints());
		parameters.addValue("imagePath", question.getQuestionImagePath());
		parameters.addValue("type", question.getQuestionType());
		parameters.addValue("questionId", question.getQuestionId());
		try {
			updateCount = namedParameterJdbcTemplate.update(SqlProperties.exam.get("updateQuestion"), parameters);
			return updateCount != 0;
		} catch (DataAccessException exp) {
			log.error("ExamRepositoryImpl :: updateQuestion(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("ExamRepositoryImpl :: updateQuestion(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public Boolean updateOption(UpdateOptionVO option, Integer userId) throws GlobalException {
		int updateCount = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, userId);
		parameters.addValue("optionText", option.getOptionText());
		parameters.addValue("isCorrect", option.getIsCorrect());
		parameters.addValue("imagePath", option.getOptionImagePath());
		parameters.addValue("type", option.getOptionType());
		parameters.addValue("optionId", option.getOptionId());
		try {
			updateCount = namedParameterJdbcTemplate.update(SqlProperties.exam.get("updateOption"), parameters);
			return updateCount != 0;
		} catch (DataAccessException exp) {
			log.error("ExamRepositoryImpl :: updateOption(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("ExamRepositoryImpl :: updateOption(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public List<ExamVO> retrieveExamsByUser(UserVO user) throws GlobalException {
		List<ExamVO> allExams = new ArrayList<>();
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, user.getUserId());
		try {
			allExams = namedParameterJdbcTemplate.query(SqlProperties.exam.get("getAllUserExams"), parameters, new BeanPropertyRowMapper<>(ExamVO.class));
		} catch (DataAccessException exp) {
			log.error("ExamRepositoryImpl :: retrieveExamsByUser(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("ExamRepositoryImpl :: retrieveExamsByUser(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return allExams;
	}

	@Override
	public ExamVO retrieveExamDetails(Integer examId) throws GlobalException {
		ExamVO exam = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("examId", examId);
		try {
			exam = namedParameterJdbcTemplate.queryForObject(SqlProperties.exam.get("getExamById"), parameters, new BeanPropertyRowMapper<ExamVO>(ExamVO.class));
		} catch (DataAccessException exp) {
			log.error("ExamRepositoryImpl :: retrieveExamDetails(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("ExamRepositoryImpl :: retrieveExamDetails(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return exam;
	}

	@Override
	public List<ExamVO> retrieveAllExams() throws GlobalException {
		List<ExamVO> allExams = new ArrayList<>();
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		try {
			allExams = namedParameterJdbcTemplate.query(SqlProperties.exam.get("getAllExams"), parameters, new BeanPropertyRowMapper<>(ExamVO.class));
		} catch (DataAccessException exp) {
			log.error("ExamRepositoryImpl :: retrieveAllExams(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("ExamRepositoryImpl :: retrieveAllExams(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return allExams;
	}

	@Override
	public List<CreateQuestionVO> retrieveQuestionsByIds(List<Integer> questionIds) throws GlobalException {
		List<CreateQuestionVO> allQuestions = new ArrayList<>();
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("questionIds", questionIds.toArray());
		try {
			allQuestions = namedParameterJdbcTemplate.query(SqlProperties.exam.get("getQuestionByIds"), parameters, new BeanPropertyRowMapper<>(CreateQuestionVO.class));
		} catch (DataAccessException exp) {
			log.error("ExamRepositoryImpl :: retrieveQuestionsByIds(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("ExamRepositoryImpl :: retrieveQuestionsByIds(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return allQuestions;
	}

	@Override
	public List<CreateOptionVO> retrieveOptionsByIds(List<Integer> optionIds) throws GlobalException {
		List<CreateOptionVO> allOptions = new ArrayList<>();
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("optionIds", optionIds.toArray());
		try {
			allOptions = namedParameterJdbcTemplate.query(SqlProperties.exam.get("getOptionsByIds"), parameters, new BeanPropertyRowMapper<>(CreateOptionVO.class));
		} catch (DataAccessException exp) {
			log.error("ExamRepositoryImpl :: retrieveOptionsByIds(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("ExamRepositoryImpl :: retrieveOptionsByIds(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return allOptions;
	}

	@Override
	public List<ExamVO> retrieveExamsAssignedToUser(UserVO user) throws GlobalException {
		List<ExamVO> allExams = new ArrayList<>();
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, user.getUserId());
		try {
			allExams = namedParameterJdbcTemplate.query(SqlProperties.exam.get("getAllAssignedExamsToUser"), parameters, new BeanPropertyRowMapper<>(ExamVO.class));
		} catch (DataAccessException exp) {
			log.error("ExamRepositoryImpl :: retrieveExamsAssignedToUser(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("ExamRepositoryImpl :: retrieveExamsAssignedToUser(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return allExams;
	}

	@Override
	public QuestionVO retrieveQuestionDetails(Integer questionId) throws GlobalException {
		QuestionVO question = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("questionId", questionId);
		try {
			question = namedParameterJdbcTemplate.queryForObject(SqlProperties.exam.get("getQuestionById"), parameters, new BeanPropertyRowMapper<QuestionVO>(QuestionVO.class));
		} catch (DataAccessException exp) {
			log.error("ExamRepositoryImpl :: retrieveQuestionDetails(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("ExamRepositoryImpl :: retrieveQuestionDetails(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return question;
	}

	@Override
	public OptionVO retrieveOptionDetails(Integer optionId) throws GlobalException {
		OptionVO option = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("optionId", optionId);
		try {
			option = namedParameterJdbcTemplate.queryForObject(SqlProperties.exam.get("getOptionById"), parameters, new BeanPropertyRowMapper<OptionVO>(OptionVO.class));
		} catch (DataAccessException exp) {
			log.error("ExamRepositoryImpl :: retrieveOptionDetails(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("ExamRepositoryImpl :: retrieveOptionDetails(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return option;
	}

	@Override
	public Boolean assignUserToExam(List<String> assignUsers, Integer examId, UserVO user) throws GlobalException {
		int size = assignUsers.size();
		MapSqlParameterSource[] batchArgs = new MapSqlParameterSource[size];
	    IntStream.range(0, size).forEach(i -> {
	        MapSqlParameterSource args = new MapSqlParameterSource();
	        args.addValue("email", assignUsers.get(i));
	        args.addValue("examId", examId);
	        args.addValue(USER_ID, user.getUserId());
	        batchArgs[i] = args;
	    });
	    try {
			int[] updatedCount = namedParameterJdbcTemplate.batchUpdate(SqlProperties.exam.get("assignExamToUser"), batchArgs);
			return updatedCount.length != 0;
	    } catch (DataAccessException exp) {
			log.error("ExamRepositoryImpl :: assignUserToExam(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("ExamRepositoryImpl :: assignUserToExam(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public Boolean unAssignUserToExam(List<String> unAssignUsers, Integer examId, UserVO user) throws GlobalException {
		int size = unAssignUsers.size();
		MapSqlParameterSource[] batchArgs = new MapSqlParameterSource[size];
	    IntStream.range(0, size).forEach(i -> {
	        MapSqlParameterSource args = new MapSqlParameterSource();
	        args.addValue("email", unAssignUsers.get(i));
	        args.addValue("examId", examId);
	        args.addValue(USER_ID, user.getUserId());
	        batchArgs[i] = args;
	    });
	    try {
			int[] updatedCount = namedParameterJdbcTemplate.batchUpdate(SqlProperties.exam.get("unAssignExamToUser"), batchArgs);
			return updatedCount.length != 0;
	    } catch (DataAccessException exp) {
			log.error("ExamRepositoryImpl :: unAssignUserToExam(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("ExamRepositoryImpl :: unAssignUserToExam(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@Override
	public List<String> retrieveAssignedUsersToExam(Integer examId) throws GlobalException {
		List<String> users = new ArrayList<>();
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("examId", examId);
		try {
			users = namedParameterJdbcTemplate.queryForList(SqlProperties.exam.get("getAllUsersAssignedToExam"), parameters, String.class);
		} catch (DataAccessException exp) {
			log.error("ExamRepositoryImpl :: retrieveAssignedUsersToExam(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("ExamRepositoryImpl :: retrieveAssignedUsersToExam(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return users;
	}

	@Override
	public Boolean deleteExam(Integer examId) throws GlobalException {
		int deletedCount = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("examId", examId);
		try {
			deletedCount = namedParameterJdbcTemplate.update(SqlProperties.exam.get("deleteExamById"), parameters);
		} catch (DataAccessException exp) {
			log.error("ExamRepositoryImpl :: deleteExam(): data access exception {} {}", exp.getMessage(), exp.getCause());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("ExamRepositoryImpl :: deleteExam(): exception {} {}", exp.getMessage(), exp.getCause());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return deletedCount != 0;
	}

	@Override
	public Boolean deleteQuestion(Integer questionId) throws GlobalException {
		int deletedCount = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("questionId", questionId);
		try {
			deletedCount = namedParameterJdbcTemplate.update(SqlProperties.exam.get("deleteQuestionById"), parameters);
		} catch (DataAccessException exp) {
			log.error("ExamRepositoryImpl :: deleteQuestion(): data access exception {} {}", exp.getMessage(), exp.getCause());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("ExamRepositoryImpl :: deleteQuestion(): exception {} {}", exp.getMessage(), exp.getCause());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return deletedCount != 0;
	}

	@Override
	public Boolean deleteOption(Integer optionId) throws GlobalException {
		int deletedCount = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("optionId", optionId);
		try {
			deletedCount = namedParameterJdbcTemplate.update(SqlProperties.exam.get("deleteOptionById"), parameters);
		} catch (DataAccessException exp) {
			log.error("ExamRepositoryImpl :: deleteOption(): data access exception {} {}", exp.getMessage(), exp.getCause());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("ExamRepositoryImpl :: deleteOption(): exception {} {}", exp.getMessage(), exp.getCause());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return deletedCount != 0;
	}

	@Override
	public List<QuestionVO> retrieveExamQuestionAndOptions(Integer examId) throws GlobalException {
		List<QuestionVO> questions = new ArrayList<>();
		List<OptionVO> options = new ArrayList<>();
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("examId", examId);
		try {
			questions = namedParameterJdbcTemplate.query(SqlProperties.exam.get("getAllQuestionsOfExam"), parameters, new BeanPropertyRowMapper<>(QuestionVO.class));
			options = namedParameterJdbcTemplate.query(SqlProperties.exam.get("getAllOptionsOfExam"), parameters, new BeanPropertyRowMapper<>(OptionVO.class));
			
			// Map options to questions
			Map<Integer, List<OptionVO>> optionsByQuestionId = options.stream()
			    .collect(Collectors.groupingBy(OptionVO::getQuestionId));

			questions.forEach(question -> question.setOptions(optionsByQuestionId.get(question.getQuestionId())));

		} catch (DataAccessException exp) {
			log.error("ExamRepositoryImpl :: retrieveExamQuestionAndOptions(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("ExamRepositoryImpl :: retrieveExamQuestionAndOptions(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return questions;
	}

	@Override
	public Boolean startUserExam(ExamVO exam, Integer userId) throws GlobalException {
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("examId", exam.getExamId());
		parameters.addValue("duration", exam.getDuration());
		parameters.addValue("currentTime", LocalDateTime.now());
		parameters.addValue(USER_ID, userId);
		try {
			int inserted = 0;
			inserted = namedParameterJdbcTemplate.update(SqlProperties.exam.get("startUserExam"), parameters);
			return inserted != 0;
		} catch (DataAccessException exp) {
			log.error("ExamRepositoryImpl :: startUserExam(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("ExamRepositoryImpl :: startUserExam(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public List<ExamSessionVO> retrieveUserExamSession(Integer examId) throws GlobalException {
		List<ExamSessionVO> allExamSessions = new ArrayList<>();
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("examId", examId);
		try {
			allExamSessions = namedParameterJdbcTemplate.query(SqlProperties.exam.get("getExamSessionsByExamId"), parameters, new BeanPropertyRowMapper<>(ExamSessionVO.class));
		} catch (DataAccessException exp) {
			log.error("ExamRepositoryImpl :: retrieveUserExamSession(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("ExamRepositoryImpl :: retrieveUserExamSession(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return allExamSessions;
	}

	@Override
	public List<UserExamResponseVO> retrieveUserExamResponses(ExamSessionVO examSessionVO) throws GlobalException {		
		List<UserExamResponseVO> allUserExamResponses = new ArrayList<>();
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("examId", examSessionVO.getExamId());
		parameters.addValue(USER_ID, examSessionVO.getUserId());
		parameters.addValue("examSessionId", String.valueOf(examSessionVO.getExamSessionId()));
		try {
			allUserExamResponses = namedParameterJdbcTemplate.query(SqlProperties.exam.get("getUserExamResponses"), parameters, new BeanPropertyRowMapper<>(UserExamResponseVO.class));
		} catch (DataAccessException exp) {
			log.error("ExamRepositoryImpl :: retrieveUserExamResponses(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("ExamRepositoryImpl :: retrieveUserExamResponses(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return allUserExamResponses;
	}

	@Override
	public ExamSessionVO retrieveUserExamSessionDetails(String examSessionId) throws GlobalException {
		ExamSessionVO examSession = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("examSessionId", examSessionId);
		try {
			examSession = namedParameterJdbcTemplate.queryForObject(SqlProperties.exam.get("getExamSessionBySessionId"), parameters, new BeanPropertyRowMapper<ExamSessionVO>(ExamSessionVO.class));
		} catch (DataAccessException exp) {
			log.error("ExamRepositoryImpl :: retrieveUserExamSessionDetails(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("ExamRepositoryImpl :: retrieveUserExamSessionDetails(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return examSession;
	}

	@Override
	public ExamSessionVO retrieveUserExamSessionByExamIdUserId(Integer examId, Integer userId) throws GlobalException {
		ExamSessionVO examSession = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("examId", examId);
		parameters.addValue(USER_ID, userId);
		try {
			examSession = namedParameterJdbcTemplate.queryForObject(SqlProperties.exam.get("getExamSessionByExamIdUserId"), parameters, new BeanPropertyRowMapper<ExamSessionVO>(ExamSessionVO.class));
		} catch (DataAccessException exp) {
			log.error("ExamRepositoryImpl :: retrieveUserExamSessionByExamIdUserId(): data access exception {} for userId {} and examId {}", exp.getMessage(), userId, examId);
		} catch (Exception exp) {
			log.error("ExamRepositoryImpl :: retrieveUserExamSessionByExamIdUserId(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return examSession;
	}

	@Override
	public Boolean updateUserExamResponse(UserExamResponseVO exam, UserVO user, ExamSessionVO examSession) throws GlobalException {
		int isUpdated = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("examId", exam.getExamId());
		parameters.addValue("questionId", exam.getQuestionId());
		parameters.addValue("optionId", exam.getOptionId());
		parameters.addValue("answer", exam.getAnswerResponse());
		parameters.addValue(USER_ID, user.getUserId());
		parameters.addValue("examSessionId", String.valueOf(examSession.getExamSessionId()));
		try {
			isUpdated = namedParameterJdbcTemplate.update(SqlProperties.exam.get("updateUserExamResponse"), parameters);
			return isUpdated != 0;
		} catch (DataAccessException exp) {
			log.error("ExamRepositoryImpl :: updateUserExamResponse(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("ExamRepositoryImpl :: updateUserExamResponse(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public Integer saveUserExamResponse(UserExamResponseVO exam, UserVO user, ExamSessionVO examSession) throws GlobalException {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("examId", exam.getExamId());
		parameters.addValue("questionId", exam.getQuestionId());
		parameters.addValue("optionId", exam.getOptionId());
		parameters.addValue("answer", exam.getAnswerResponse());
		parameters.addValue(USER_ID, user.getUserId());
		parameters.addValue("examSessionId", String.valueOf(examSession.getExamSessionId()));
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			namedParameterJdbcTemplate.update(SqlProperties.exam.get("saveUserExamResponse"), parameters, keyHolder, new String[] { "responseId" });
			return keyHolder.getKey().intValue();
		} catch (DataAccessException exp) {
			log.error("ExamRepositoryImpl :: saveUserExamResponse(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("ExamRepositoryImpl :: saveUserExamResponse(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public Boolean endUserExam(Integer userId, Integer examId, UUID examSessionId) throws GlobalException {
		int updateCount = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, userId);
		parameters.addValue("examId", examId);
		parameters.addValue("examSessionId", String.valueOf(examSessionId));
		parameters.addValue("currentTime", LocalDateTime.now());
		try {
			updateCount = namedParameterJdbcTemplate.update(SqlProperties.exam.get("endUserExamSession"), parameters);
			return updateCount != 0;
		} catch (DataAccessException exp) {
			log.error("ExamRepositoryImpl :: endUserExam(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("ExamRepositoryImpl :: endUserExam(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public Boolean deleteUserExamResponse(ExamSessionVO examSession, UserVO user, UserExamResponseVO exam)
			throws GlobalException {
		int deletedCount = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("examId", exam.getExamId());
		parameters.addValue("questionId", exam.getQuestionId());
		parameters.addValue("optionId", exam.getOptionId());
		parameters.addValue(USER_ID, user.getUserId());
		parameters.addValue("examSessionId", String.valueOf(examSession.getExamSessionId()));
		try {
			deletedCount = namedParameterJdbcTemplate.update(SqlProperties.exam.get("deleteUserExamResponse"), parameters);
		} catch (DataAccessException exp) {
			log.error("ExamRepositoryImpl :: deleteUserExamResponse(): data access exception {} {}", exp.getMessage(), exp.getCause());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("ExamRepositoryImpl :: deleteUserExamResponse(): exception {} {}", exp.getMessage(), exp.getCause());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return deletedCount != 0;
	}

	@Override
	public Boolean updateExamStatus(ExamVO exam, String status) throws GlobalException {
		int updateCount = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("examId", exam.getExamId());
		parameters.addValue("status", status);
		try {
			updateCount = namedParameterJdbcTemplate.update(SqlProperties.exam.get("updateExamStatus"), parameters);
			return updateCount != 0;
		} catch (DataAccessException exp) {
			log.error("ExamRepositoryImpl :: updateExamStatus(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("ExamRepositoryImpl :: updateExamStatus(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public List<Integer> retrieveUserAssignedExams(String emailId) throws GlobalException {
		List<Integer> allUserExams = new ArrayList<>();
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("emailId", emailId);
		try {
			allUserExams = namedParameterJdbcTemplate.queryForList(SqlProperties.exam.get("getAssignedExams"), parameters, Integer.class);
		} catch (DataAccessException exp) {
			log.error("ExamRepositoryImpl :: retrieveUserExamResponses(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("ExamRepositoryImpl :: retrieveUserExamResponses(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return allUserExams;
	}

	@Override
	public List<Integer> retrieveUserAttemptedExams(Integer userId) throws GlobalException {
		List<Integer> allUserExams = new ArrayList<>();
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("userId", userId);
		try {
			allUserExams = namedParameterJdbcTemplate.queryForList(SqlProperties.exam.get("getAttemptedExams"), parameters, Integer.class);
		} catch (DataAccessException exp) {
			log.error("ExamRepositoryImpl :: retrieveUserExamResponses(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("ExamRepositoryImpl :: retrieveUserExamResponses(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return allUserExams;
	}

}
