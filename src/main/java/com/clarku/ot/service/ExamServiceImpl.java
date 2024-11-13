package com.clarku.ot.service;

import java.util.List;
import java.util.Objects;

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
import com.clarku.ot.vo.ExamVO;
import com.clarku.ot.vo.OptionVO;
import com.clarku.ot.vo.QuestionVO;
import com.clarku.ot.vo.UpdateExamVO;
import com.clarku.ot.vo.UpdateOptionVO;
import com.clarku.ot.vo.UpdateQuestionVO;
import com.clarku.ot.vo.UserVO;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class ExamServiceImpl implements IExamService {

	@Autowired
	private IExamRepo examRepo;

	@Override
	public Boolean createExam(CreateExamVO examVO, UserVO user) throws GlobalException {
		examRepo.createExam(examVO, user);
		if (examVO.getQuestions() != null && !examVO.getQuestions().isEmpty()) {
			saveQuestions(examVO, user);
		}
		return true;
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
		}

		return exams;
	}

	@Override
	public ExamVO retrieveExamDetails(Integer examId) throws GlobalException {
		return examRepo.retrieveExamDetails(examId);
	}

	@Override
	public List<ExamVO> retrieveAllExams() throws GlobalException {
		return examRepo.retrieveAllExams();
	}

	@Override
	public Boolean createQuestion(CreateQuestionVO question, UserVO user) throws GlobalException {
		question.setQuestionId(examRepo.createQuestion(question, user.getUserId()));
		if (question.getOptions() != null && !question.getOptions().isEmpty()) {
			for (CreateOptionVO option: question.getOptions()) {
				option.setOptionId(examRepo.createOption(option, user.getUserId()));
				examRepo.mapOptionWithQuestion(question.getQuestionId(), option.getOptionId(), option.getIsCorrect());
			}
		}
		return examRepo.mapQuestionWithExam(question.getExamId(), question.getQuestionId());
	}

	@Override
	public Boolean createOption(CreateOptionVO option, UserVO user) throws GlobalException {
		option.setOptionId(examRepo.createOption(option, user.getUserId()));
		return examRepo.mapOptionWithQuestion(option.getQuestionId(), option.getOptionId(), option.getIsCorrect());
	}

	@Override
	public List<ExamVO> retrieveExamsAssignedToUser(UserVO user) throws GlobalException {
		return examRepo.retrieveExamsAssignedToUser(user);
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
		
		if (Constants.COMPLETED.equals(exam.getStatus()) || Constants.CLOSED.equals(exam.getStatus())) {
			throw new GlobalException("Can't Assign to Exam, Either Exam is Completed/Closed.", HttpStatus.BAD_REQUEST);
		}

		return examRepo.assignUserToExam(assignExam, user);
	}

	@Override
	public ExamVO retrieveExam(Integer examId, UserVO user) throws GlobalException {
		ExamVO exam = examRepo.retrieveExamDetails(examId);
		List<String> assignedUsers = examRepo.retrieveAssignedUsersToExam(examId);
		exam.setIsUserAssigned(!assignedUsers.isEmpty() && assignedUsers.contains(user.getEmailId()));

		return exam;
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
		if (!Constants.ADMIN.equals(user.getUserType()) && !question.getCreatedBy().equals(user.getUserId())) {
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
		if (!Constants.ADMIN.equals(user.getUserType()) && !option.getCreatedBy().equals(user.getUserId())) {
			throw new GlobalException("UnAuthorised to Perform this operation", HttpStatus.FORBIDDEN);
		}
		return examRepo.deleteOption(optionId);
	}


}
