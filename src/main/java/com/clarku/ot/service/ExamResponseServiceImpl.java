package com.clarku.ot.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clarku.ot.exception.GlobalException;
import com.clarku.ot.repository.IExamRepo;
import com.clarku.ot.repository.IExamResponseRepo;
import com.clarku.ot.utils.Constants;
import com.clarku.ot.vo.ExamKeyVO;
import com.clarku.ot.vo.ExamResponseVO;
import com.clarku.ot.vo.ExamVO;
import com.clarku.ot.vo.QuestionVO;
import com.clarku.ot.vo.ResponseFeedbackVO;
import com.clarku.ot.vo.StudentResponseVO;
import com.clarku.ot.vo.UserVO;

@Service
public class ExamResponseServiceImpl implements IExamResponseService {

	@Autowired
	private IExamResponseRepo responseRepo;

	@Autowired
	private IExamRepo examRepo;

	@Autowired
	private INotificationService notify;


	@Override
	public ExamResponseVO getStudentResponses(ExamVO exam, UserVO user) throws GlobalException {
		ExamResponseVO examResponses = new ExamResponseVO();
		populateExamInResponses(examResponses, exam);
		populateExamTakers(examResponses);
		populateAnswerKey(examResponses);
		populateStudentResponses(examResponses);
		if (Boolean.TRUE.equals(exam.getAutoGrade())) {
			gradeAutoGradeQuestions(examResponses, user);
		}
		return examResponses;
	}

	private void gradeAutoGradeQuestions(ExamResponseVO examResponses, UserVO user) throws GlobalException {
		List<StudentResponseVO> responses = examResponses.getStudentResponses();
		List<ExamKeyVO> examKeys = examResponses.getExamKeys();
		Map<Integer, Set<Integer>> examQuestionAnswerMap = examKeys.stream()
		        .filter(ExamKeyVO::getIsCorrect)
		        .collect(Collectors.groupingBy(
		            ExamKeyVO::getQuestionId,
		            Collectors.mapping(ExamKeyVO::getOptionId, Collectors.toSet())
		        ));

		Map<Integer, QuestionVO> examQuestionMap = examResponses.getQuestions().stream()
                .collect(Collectors.toMap(QuestionVO::getQuestionId, Function.identity()));
		
		for (StudentResponseVO response: responses) {
			QuestionVO question = examQuestionMap.get(response.getQuestionId());
			ResponseFeedbackVO feedback = new ResponseFeedbackVO();
			feedback.setFeedbackerName(user.getFirstName() + " "+ user.getLastName());
			feedback.setResponseId(response.getResponseId());
			feedback.setFeedbackerId(user.getUserId());
			if (question.getQuestionType().equalsIgnoreCase(Constants.MCQ) || question.getQuestionType().equalsIgnoreCase(Constants.TRUE_FALSE)) {
				if (examQuestionAnswerMap.get(response.getQuestionId()).contains(response.getChoosenOption())) {
					response.setIsCorrect(Boolean.TRUE);
					response.setPointsGained(question.getPoints());
					feedback.setFeedback("Correct");
				} else {
					response.setIsCorrect(Boolean.FALSE);
					response.setPointsGained(0.0);
					feedback.setFeedback("InCorrect");
				}
				response.setFeedback(feedback);
				saveAutoGradeResponse(user, response, question);
			}else if (question.getQuestionType().equalsIgnoreCase(Constants.MSQ)) {
				if (response.getPointsGained() == null) {
					response.setPointsGained(0.0);
				}
				if (examQuestionAnswerMap.get(response.getQuestionId()).contains(response.getChoosenOption())) {
					response.setPointsGained(response.getPointsGained() + question.getPoints() / examQuestionAnswerMap.get(response.getQuestionId()).size());
				} else {
					response.setIsCorrect(Boolean.FALSE);
				}
				if( response.getPointsGained().equals(question.getPoints())) {
					response.setIsCorrect(Boolean.TRUE);
					feedback.setFeedback("Correct");
				} else if (response.getPointsGained() > 0.0) {
					response.setIsCorrect(Boolean.FALSE);
					feedback.setFeedback("Partially Correct");
				} else {
					response.setIsCorrect(Boolean.FALSE);
					feedback.setFeedback("InCorrect");
				}
				response.setFeedback(feedback);
				saveAutoGradeResponse(user, response, question);
			}
		}
	}
	
	private Boolean saveAutoGradeResponse(UserVO user, StudentResponseVO response, QuestionVO question) throws GlobalException {
		return responseRepo.saveExamFeedback(user, response, response.getFeedback());
	}

	private void populateStudentResponses(ExamResponseVO examResponses) throws GlobalException {
		List<StudentResponseVO> responses = responseRepo.retrieveStudentResponses(examResponses.getExamId());
		List<ResponseFeedbackVO> feedbacks = responseRepo.retrieveExamFeedbacks(examResponses.getExamId());
		
		Map<Integer, ResponseFeedbackVO> feedbackMap = new HashMap<>();
		for (ResponseFeedbackVO feedback : feedbacks) {
		    int responseId = feedback.getResponseId();
		    if (!feedbackMap.containsKey(responseId)) {
		        feedbackMap.put(responseId, feedback);
		    }
		}
		for (StudentResponseVO response : responses) {
		    ResponseFeedbackVO correspondingFeedbacks = feedbackMap.getOrDefault(response.getResponseId(), null);
		    response.setFeedback(correspondingFeedbacks);
		}
		examResponses.setStudentResponses(responses);
	}
	
	private void populateStudentResponsesBySession(UUID examSession, ExamResponseVO examResponses) throws GlobalException {
		List<StudentResponseVO> responses = responseRepo.retrieveStudentResponsesByStudentSession(examSession);
		List<ResponseFeedbackVO> feedbacks = responseRepo.retrieveExamFeedbacksBySession(examSession);
		
		Map<Integer, ResponseFeedbackVO> feedbackMap = new HashMap<>();
		for (ResponseFeedbackVO feedback : feedbacks) {
		    int responseId = feedback.getResponseId();
		    if (!feedbackMap.containsKey(responseId)) {
		        feedbackMap.put(responseId, feedback);
		    }
		}
		for (StudentResponseVO response : responses) {
		    ResponseFeedbackVO correspondingFeedbacks = feedbackMap.getOrDefault(response.getResponseId(), null);
		    response.setFeedback(correspondingFeedbacks);
		}
		examResponses.setStudentResponses(responses);
	}

	private void populateAnswerKey(ExamResponseVO examResponses) throws GlobalException {
		examResponses.setExamKeys(responseRepo.retrieveExamKey(examResponses.getExamId()));
	}


	private void populateExamTakers(ExamResponseVO examResponses) throws GlobalException {
		examResponses.setRespondedUsers(responseRepo.retreiveExamTakers(examResponses.getExamId()));
	}


	private void populateExamInResponses(ExamResponseVO examResponse, ExamVO exam) {
		examResponse.setExamId(exam.getExamId());
		examResponse.setAutoGrade(exam.getAutoGrade());
		examResponse.setCreatedOn(exam.getCreatedOn());
		examResponse.setAssignedUserEmails(exam.getAssignedUserEmails());
		examResponse.setCreatedUser(exam.getCreatedUser());
		examResponse.setCreatedUserId(exam.getCreatedUserId());
		examResponse.setDescription(exam.getDescription());
		examResponse.setDuration(exam.getDuration());
		examResponse.setEndTime(exam.getEndTime());
		examResponse.setQuestions(exam.getQuestions());
		examResponse.setStartTime(exam.getStartTime());
		examResponse.setStatus(exam.getStatus());
		examResponse.setTitle(exam.getTitle());
		examResponse.setTotalPoints(exam.getTotalPoints());
	}

	@Override
	public ExamResponseVO getStudentExamResponsesBySession(UUID examSession, ExamVO exam, UserVO user) throws GlobalException {
		ExamResponseVO examResponses = new ExamResponseVO();
		
		populateExamInResponses(examResponses, exam);
		populateExamTakers(examResponses);
		populateAnswerKey(examResponses);
		populateStudentResponsesBySession(examSession, examResponses);
		
		return examResponses;
	}

}
