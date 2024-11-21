package com.clarku.ot.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.clarku.ot.exception.GlobalException;
import com.clarku.ot.repository.IExamRepo;
import com.clarku.ot.utils.Constants;
import com.clarku.ot.vo.AssignExamVO;
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

@Service
@Log4j2
public class ExamServiceImpl implements IExamService {

	@Autowired
	private IExamRepo examRepo;

	@Autowired
	private INotificationService notify;

	@Override
	public Boolean createExam(CreateExamVO examVO, UserVO user) throws GlobalException {
		validateAndPopulateDefaults(examVO);
		examRepo.createExam(examVO, user);
		if (examVO.getQuestions() != null && !examVO.getQuestions().isEmpty()) {
			saveQuestions(examVO, user);
		}
		return true;
	}

	private void validateAndPopulateDefaults(CreateExamVO exam) {
		if (exam.getAutoGrade() == null) {
			exam.setAutoGrade(false);
		}
		if (exam.getDescription() == null) {
			exam.setDescription("");
		}
		if (exam.getDuration() == null) {
			exam.setDuration(60);
		}
		if(exam.getStatus() == null) {
			exam.setStatus(Constants.HOLD);
		}
		if (exam.getQuestions() != null) {
			for (CreateQuestionVO question : exam.getQuestions()) {
				validateAndPopulateDefaultQuestion(question);
			}
		}
	}
	
	private void validateAndPopulateDefaultQuestion(CreateQuestionVO question) {
		if (question.getQuestionDetails() == null) {
			question.setQuestionDetails("");
		}
		if (question.getQuestionImagePath() == null) {
			question.setQuestionImagePath("");
		}
		if (question.getQuestionType() == null) {
			question.setQuestionType(Constants.MCQ);
		}
		if (question.getOptions() != null) {
			for (CreateOptionVO option : question.getOptions()) {
				validateAndPopulateDefaultOptions(option);
			}
		}
	}

	private void validateAndPopulateDefaultOptions(CreateOptionVO option) {
		if (option.getOptionImagePath() == null) {
			option.setOptionImagePath("");
		}
		if (option.getOptionType() == null) {
			option.setOptionType(Constants.CHOOSE);
		}
		if (option.getIsCorrect() == null) {
			option.setIsCorrect(false);
		}
	}

	private void saveQuestions(CreateExamVO examVO, UserVO user) throws GlobalException {
		for (CreateQuestionVO question : examVO.getQuestions()) {
			question.setQuestionId(examRepo.createQuestion(question, user.getUserId()));
			for (CreateOptionVO option: question.getOptions()) {
				option.setOptionId(examRepo.createOption(option, user.getUserId()));
				examRepo.mapOptionWithQuestion(question.getQuestionId(), option.getOptionId(), option.getIsCorrect());
			}
			examRepo.mapQuestionWithExam(examVO.getExamId(), question.getQuestionId());
		}
	}

	@Override
	public List<ExamVO> retrieveExamsByUser(UserVO user) throws GlobalException {
		List<ExamVO> exams = examRepo.retrieveExamsByUser(user);

		for (ExamVO exam: exams) {
			List<String> examAssignedUser = examRepo.retrieveAssignedUsersToExam(exam.getExamId());
			exam.setAssignedUserEmails(examAssignedUser);
			validateExamStatus(exam);
		}

		return exams;
	}

	@Override
	public ExamVO retrieveExamDetails(Integer examId) throws GlobalException {
		ExamVO exam = examRepo.retrieveExamDetails(examId);
		List<String> examAssignedUser = examRepo.retrieveAssignedUsersToExam(exam.getExamId());
		exam.setAssignedUserEmails(examAssignedUser);
		return exam;
	}

	@Override
	public List<ExamVO> retrieveAllExams() throws GlobalException {
		return examRepo.retrieveAllExams();
	}

	@Override
	public QuestionVO createQuestion(CreateQuestionVO question, UserVO user) throws GlobalException {
		question.setQuestionId(examRepo.createQuestion(question, user.getUserId()));
		if (question.getOptions() != null && !question.getOptions().isEmpty()) {
			for (CreateOptionVO option: question.getOptions()) {
				option.setOptionId(examRepo.createOption(option, user.getUserId()));
				examRepo.mapOptionWithQuestion(question.getQuestionId(), option.getOptionId(), option.getIsCorrect());
			}
		}
		examRepo.mapQuestionWithExam(question.getExamId(), question.getQuestionId());
		
		return retrieveQuestionDetails(question.getQuestionId());
	}

	@Override
	public CreateOptionVO createOption(CreateOptionVO option, UserVO user) throws GlobalException {
		option.setOptionId(examRepo.createOption(option, user.getUserId()));
		examRepo.mapOptionWithQuestion(option.getQuestionId(), option.getOptionId(), option.getIsCorrect());
		return option;
	}

	@Override
	public List<ExamVO> retrieveExamsAssignedToUser(UserVO user) throws GlobalException {
		List<ExamVO> exams = examRepo.retrieveExamsAssignedToUser(user);
		assignExamsIndicatorbyUser(exams, user);
		return exams;
	}

	private void assignExamsIndicatorbyUser(List<ExamVO> exams, UserVO user) throws GlobalException {
		for (ExamVO exam : exams) {
			exam.setIsUserAssigned(Boolean.TRUE);
			validateAndSetIndicator(exam, user);
			validateExamStatus(exam);
		}
	}

	private void validateAndSetIndicator(ExamVO exam, UserVO user) throws GlobalException {
		ExamSessionVO examSession = examRepo.retrieveUserExamSessionByExamIdUserId(exam.getExamId(), user.getUserId());
		if (examSession != null) {
			if (examSession.getEndTime().isAfter(LocalDateTime.now())) {
				exam.setExamIndicator(Constants.INPROGRESS_SMALL);
			} else {
				exam.setExamIndicator(Constants.COMPLETED_SMALL);
			}
		} else {
			exam.setExamIndicator(Constants.UN_ATTEMPTED);
		}
	}

	@Override
	public QuestionVO retrieveQuestionDetails(Integer questionId) throws GlobalException {
		return examRepo.retrieveQuestionDetails(questionId);
	}

	@Override
	public OptionVO retrieveOptionDetails(Integer optionId) throws GlobalException {
		return examRepo.retrieveOptionDetails(optionId);
	}

	@Override
	public Boolean updateExam(UpdateExamVO updatedExam, UserVO user) throws GlobalException {
		ExamVO examDetails = retrieveExamDetails(updatedExam.getExamId());
		if (examDetails == null) {
			throw new GlobalException("No Existing Exam Found to Update", HttpStatus.NOT_FOUND);
		}
		validateExamChanges(examDetails, updatedExam);
		
		return examRepo.updateExam(updatedExam, user);
	}

	private void validateExamChanges(ExamVO examDetails, UpdateExamVO updatedExam) throws GlobalException {
		Boolean isThereAnyChanges = !Objects.equals(updatedExam.getTitle(), examDetails.getTitle())
				|| !Objects.equals(updatedExam.getDescription(), examDetails.getDescription())
				|| !Objects.equals(updatedExam.getDuration(), examDetails.getDuration())
				|| !Objects.equals(updatedExam.getStartTime(), examDetails.getStartTime())
				|| !Objects.equals(updatedExam.getEndTime(), examDetails.getEndTime())
				|| !Objects.equals(updatedExam.getAutoGrade(), examDetails.getAutoGrade())
				|| !Objects.equals(updatedExam.getStatus(), examDetails.getStatus())
				|| !Objects.equals(updatedExam.getTotalPoints(), examDetails.getTotalPoints());

		if (Boolean.FALSE.equals(isThereAnyChanges)) {
		    throw new GlobalException("No changes detected in the exam. Please update at least one exam field to proceed.", HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@Override
	public Boolean updateQuestion(UpdateQuestionVO updatedQuestion, UserVO user) throws GlobalException {
		QuestionVO questionDetails = retrieveQuestionDetails(updatedQuestion.getQuestionId());
		if (questionDetails == null) {
			throw new GlobalException("No Existing Question Found to Update", HttpStatus.NOT_FOUND);
		}
		validateQuestionChanges(questionDetails, updatedQuestion);

		return examRepo.updateQuestion(updatedQuestion, user.getUserId());
	}

	private void validateQuestionChanges(QuestionVO questionDetails, UpdateQuestionVO updatedQuestion) throws GlobalException {
		Boolean isThereAnyChanges = !Objects.equals(updatedQuestion.getQuestionTitle(), questionDetails.getQuestionTitle())
				|| !Objects.equals(updatedQuestion.getQuestionDetails(), questionDetails.getQuestionDetails())
				|| !Objects.equals(updatedQuestion.getQuestionImagePath(), questionDetails.getQuestionImagePath())
				|| !Objects.equals(updatedQuestion.getQuestionType(), questionDetails.getQuestionType())
				|| !Objects.equals(updatedQuestion.getPoints(), questionDetails.getPoints());

		if (Boolean.FALSE.equals(isThereAnyChanges)) {
		    throw new GlobalException("No changes detected in the question. Please update at least one question field to proceed.", HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@Override
	public Boolean updateOption(UpdateOptionVO updatedOption, UserVO user) throws GlobalException {
		OptionVO optionDetails = retrieveOptionDetails(updatedOption.getOptionId());
		if (optionDetails == null) {
			throw new GlobalException("No Existing Option Found to Update", HttpStatus.NOT_FOUND);
		}
		validateOptionChanges(optionDetails, updatedOption);

		examRepo.mapUpdateOptionWithQuestion(updatedOption.getQuestionId(), updatedOption.getOptionId(), updatedOption.getIsCorrect());

		return examRepo.updateOption(updatedOption, user.getUserId());
	}

	private void validateOptionChanges(OptionVO optionDetails, UpdateOptionVO updatedOption) throws GlobalException {
		Boolean isThereAnyChanges = !Objects.equals(updatedOption.getOptionText(), optionDetails.getOptionText())
				|| !Objects.equals(updatedOption.getOptionImagePath(), optionDetails.getOptionImagePath())
				|| !Objects.equals(updatedOption.getOptionType(), optionDetails.getOptionType())
				|| !Objects.equals(updatedOption.getIsCorrect(), optionDetails.getIsCorrect());

		if (Boolean.FALSE.equals(isThereAnyChanges)) {
		    throw new GlobalException("No changes detected in the option. Please update at least one option field to proceed.", HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@Override
	public Boolean assignExamsToUser(AssignExamVO assignExam, UserVO user) throws GlobalException {
		ExamVO exam = retrieveExamDetails(assignExam.getExamId());
		List<String> unassignedUsers = new ArrayList<>();
		for (String assignEmail : assignExam.getUserEmails()) {
			if (!exam.getAssignedUserEmails().contains(assignEmail)) {
				unassignedUsers.add(assignEmail);
			}
		}
		List<String> unAssignUsers = new ArrayList<>();
		for (String assignEmail : exam.getAssignedUserEmails()) {
			if (!assignExam.getUserEmails().contains(assignEmail)) {
				unAssignUsers.add(assignEmail);
			}
		}
		
		if (unassignedUsers.isEmpty() && unAssignUsers.isEmpty()) {
			throw new GlobalException("Already assigned to the Exam", HttpStatus.BAD_REQUEST);
		}
		
		if (Constants.COMPLETED.equals(exam.getStatus()) || Constants.CLOSED.equals(exam.getStatus())) {
			throw new GlobalException("Can't Assign to Exam, Either Exam is Completed/Closed.", HttpStatus.BAD_REQUEST);
		}
		
		if (!unAssignUsers.isEmpty()) {
			examRepo.unAssignUserToExam(unAssignUsers, assignExam.getExamId(), user);
			for (String userEmail: unAssignUsers) {
				notify.sendExamUnAssignEmail(userEmail, exam.getTitle());
			}
		}

		if (!unassignedUsers.isEmpty()) {
			for (String userEmail: unassignedUsers) {
				notify.sendExamAssignEmail(userEmail, exam.getTitle());
			}
		}

		return examRepo.assignUserToExam(unassignedUsers, assignExam.getExamId(), user);
	}

	@Override
	public ExamVO retrieveExam(Integer examId, UserVO user) throws GlobalException {
		ExamVO exam = examRepo.retrieveExamDetails(examId);
		validateExamStatus(exam);
		retrieveExamQuestions(exam);
		List<String> assignedUsers = examRepo.retrieveAssignedUsersToExam(examId);
		if (Constants.STUDENT.equalsIgnoreCase(user.getUserType())) {
			for (QuestionVO question: exam.getQuestions()) {
				if (Constants.TRUE_FALSE.equalsIgnoreCase(question.getQuestionType()) ||
					Constants.MCQ.equalsIgnoreCase(question.getQuestionType()) ||
					Constants.MSQ.equalsIgnoreCase(question.getQuestionType()) ) {
					for (OptionVO option: question.getOptions()) {
						option.setIsCorrect(null);
					}
				}
			}
			Boolean isRegistered = false;
			for (String email: assignedUsers) {
				if (email.equalsIgnoreCase(user.getEmailId())) {
					isRegistered = true;
				}
			}
			if (Boolean.FALSE.equals(isRegistered)) {
				throw new GlobalException("Unauthorised to the Exam", HttpStatus.FORBIDDEN);
			}
			validateAndSetIndicator(exam, user);
			return exam;
		}
		exam.setIsUserAssigned(!assignedUsers.isEmpty() && assignedUsers.contains(user.getEmailId()));
		exam.setAssignedUserEmails(assignedUsers);
		return exam;
	}

	private void validateExamStatus(ExamVO exam) throws GlobalException {
		if (LocalDateTime.now().isAfter(LocalDateTime.parse(exam.getEndTime(), Constants.FORMATTER))) {
			examRepo.updateExamStatus(exam, Constants.COMPLETED);
			exam.setStatus(Constants.COMPLETED);
		}
		if (LocalDateTime.now().isBefore(LocalDateTime.parse(exam.getStartTime(), Constants.FORMATTER))) {
			examRepo.updateExamStatus(exam, Constants.POSTED);
			exam.setStatus(Constants.POSTED);
		}
		if (LocalDateTime.now().isAfter(LocalDateTime.parse(exam.getStartTime(), Constants.FORMATTER)) && 
				LocalDateTime.now().isBefore(LocalDateTime.parse(exam.getEndTime(), Constants.FORMATTER))) {
			examRepo.updateExamStatus(exam, Constants.ACTIVE);
			exam.setStatus(Constants.ACTIVE);
		}
	}

	private void retrieveExamQuestions(ExamVO exam) throws GlobalException {
		List<QuestionVO> questions = examRepo.retrieveExamQuestionAndOptions(exam.getExamId());
		exam.setQuestions(questions);		
	}

	@Override
	public Boolean deleteExam(Integer examId, UserVO user) throws GlobalException {
		ExamVO exam = examRepo.retrieveExamDetails(examId);
		if (exam == null) {
			throw new GlobalException("Exam Not Found to Delete or Already deleted", HttpStatus.NOT_FOUND);
		}
		if (!Constants.ADMIN.equals(user.getUserType()) && !exam.getCreatedUserId().equals(user.getUserId())) {
			throw new GlobalException("UnAuthorised to Perform this operation", HttpStatus.FORBIDDEN);
		}
		return examRepo.deleteExam(examId);
	}

	@Override
	public Boolean deleteQuestion(Integer questionId, UserVO user) throws GlobalException {
		QuestionVO question = examRepo.retrieveQuestionDetails(questionId);
		if (question == null) {
			throw new GlobalException("Question Not Found to Delete or Already deleted", HttpStatus.NOT_FOUND);
		}
		if (!Constants.ADMIN.equals(user.getUserType()) && !question.getCreatedUserId().equals(user.getUserId())) {
			throw new GlobalException("UnAuthorised to Perform this operation", HttpStatus.FORBIDDEN);
		}
		return examRepo.deleteQuestion(questionId);
	}

	@Override
	public Boolean deleteOption(Integer optionId, UserVO user) throws GlobalException {
		OptionVO option = examRepo.retrieveOptionDetails(optionId);
		if (option == null) {
			throw new GlobalException("Option Not Found to Delete or Already deleted", HttpStatus.NOT_FOUND);
		}
		if (!Constants.ADMIN.equals(user.getUserType()) && !option.getCreatedUserId().equals(user.getUserId())) {
			throw new GlobalException("UnAuthorised to Perform this operation", HttpStatus.FORBIDDEN);
		}
		return examRepo.deleteOption(optionId);
	}

	@Override
	public ExamSessionVO startUserExam(Integer examId, UserVO user) throws GlobalException {
		ExamVO exam = examRepo.retrieveExamDetails(examId);
		List<String> assignedUsers = examRepo.retrieveAssignedUsersToExam(examId);
		if (Constants.STUDENT.equalsIgnoreCase(user.getUserType())) {
			Boolean isRegistered = false;
			for (String email: assignedUsers) {
				if (email.equalsIgnoreCase(user.getEmailId())) {
					isRegistered = true;
				}
			}
			if (Boolean.FALSE.equals(isRegistered)) {
				throw new GlobalException("Unauthorised to the Exam", HttpStatus.FORBIDDEN);
			}
		}
		if (LocalDateTime.now().isAfter(LocalDateTime.parse(exam.getEndTime(), Constants.FORMATTER))) {
			throw new GlobalException("Exam Already Finished", HttpStatus.FORBIDDEN);
		}
		if (LocalDateTime.now().isBefore(LocalDateTime.parse(exam.getStartTime(), Constants.FORMATTER))) {
			throw new GlobalException("Exam not yet Started", HttpStatus.FORBIDDEN);
		}
		
		List<ExamSessionVO> examSessions = examRepo.retrieveUserExamSession(exam.getExamId());
		
		ExamSessionVO examSessionDetails = null;
		
		for (ExamSessionVO examSession : examSessions) {
			if (Objects.equals(examSession.getUserId(), user.getUserId())) {
				if (examSession.getEndTime().isAfter(LocalDateTime.now())) {
					log.info("Exam Already Started, So redirecting to ongoing exam");
					examSession.setExam(exam);
					examSession.setUser(user);
					examSessionDetails = fetchExamSessionDetails(examSession);
				} else {
					throw new GlobalException("User Already Attempted", HttpStatus.FORBIDDEN);
				}
			}
		}
		if (examSessionDetails != null) {
			return examSessionDetails;
		} else {
			if (examRepo.startUserExam(exam, user.getUserId())) {
				ExamSessionVO examSession = examRepo.retrieveUserExamSessionByExamIdUserId(examId, user.getUserId());
				examSession.setExam(exam);
				examSession.setUser(user);
				examSessionDetails = fetchExamSessionDetails(examSession);
				return examSessionDetails;
			} else {
				throw new GlobalException("Unable to start the exam", HttpStatus.BAD_REQUEST);
			}
		}
	}

	private ExamSessionVO fetchExamSessionDetails(ExamSessionVO examSession) throws GlobalException {
		List<UserExamResponseVO> responses = examRepo.retrieveUserExamResponses(examSession);
		examSession.setUserResponses(responses);
		return examSession;
	}

	@Override
	public UserExamResponseVO saveUserExamResponse(UserExamResponseVO examResponse, UserVO user, ExamSessionVO examSession) throws GlobalException {
		if ( examSession == null ) {
			examSession = examRepo.retrieveUserExamSessionByExamIdUserId(examResponse.getExamId(), user.getUserId());
		}
		if (examSession != null) {
			QuestionVO question = examRepo.retrieveQuestionDetails(examResponse.getQuestionId());
			List<UserExamResponseVO> examResponses = examRepo.retrieveUserExamResponses(examSession);
			if (question.getQuestionType().equalsIgnoreCase(Constants.MSQ)) {
				List<UserExamResponseVO> filteredResponses = examResponses.stream()
					    .filter(examResp -> examResp.getQuestionId().equals(examResponse.getQuestionId()))
					    .collect(Collectors.toList());
				
				for (UserExamResponseVO examResp : filteredResponses ) {
					examRepo.deleteUserExamResponse(examSession, user, examResp);
					if (examResp.getQuestionId().equals(examResponse.getQuestionId()) && examResp.getOptionId().equals(examResponse.getOptionId())) {
						examRepo.updateUserExamResponse(examResponse, user, examSession);
					}
				}
				for (Integer optionId : examResponse.getMsqOptions() ) {
					examResponse.setOptionId(optionId);
					examRepo.saveUserExamResponse(examResponse, user, examSession);
				}
			} else {
				for (UserExamResponseVO examResp : examResponses ) {
					if (examResp.getQuestionId().equals(examResponse.getQuestionId())) {
						examRepo.updateUserExamResponse(examResponse, user, examSession);
						return examResponse;
					}
				}
				examResponse.setResponseId(examRepo.saveUserExamResponse(examResponse, user, examSession));
			}
			
			return examResponse;
		} else {
			throw new GlobalException("Unable to save the response", HttpStatus.BAD_REQUEST);
		}
		
	}

	@Override
	public Boolean endUserExam(UserVO user, Integer examId, ExamSessionVO examSession) throws GlobalException {
		return examRepo.endUserExam(user.getUserId(), examId, examSession.getExamSessionId());
	}

}
