package com.clarku.ot.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
			if (response.getFeedback() != null && response.getFeedback().getFeedbackerId() != null) {
				continue;
			}
			QuestionVO question = examQuestionMap.get(response.getQuestionId());
			ResponseFeedbackVO feedback = new ResponseFeedbackVO();
			feedback.setFeedbackerName(user.getFirstName() + " "+ user.getLastName());
			feedback.setResponseId(response.getResponseId());
			feedback.setFeedbackerId(user.getUserId());
			if (question.getQuestionType().equalsIgnoreCase(Constants.MCQ) || question.getQuestionType().equalsIgnoreCase(Constants.TRUE_FALSE)) {
				if (examQuestionAnswerMap.containsKey(response.getQuestionId()) && examQuestionAnswerMap.get(response.getQuestionId()).contains(response.getChoosenOption())) {
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
					response.setIsCorrect(Boolean.TRUE);
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
		Map<Integer, QuestionVO> questionMap = new HashMap<>();

		Map<Integer, List<Integer>> msqQuestionOptions = new HashMap<>();
		for (QuestionVO question : examResponses.getQuestions()) {
		    if (!questionMap.containsKey(question.getQuestionId())) {
		        questionMap.put(question.getQuestionId(), question);
		    }
		}
		for (StudentResponseVO response : responses) {
		    ResponseFeedbackVO correspondingFeedbacks = feedbackMap.getOrDefault(response.getResponseId(), new ResponseFeedbackVO());
		    response.setFeedback(correspondingFeedbacks);
		    if (correspondingFeedbacks != null) {
			    response.setIsCorrect(correspondingFeedbacks.getIsCorrect());
			    response.setPointsGained(correspondingFeedbacks.getGainedPoints());
		    }
		}
		for (StudentResponseVO response : responses) {
		    if (questionMap.get(response.getQuestionId()).getQuestionType() == Constants.MSQ) {
		    	if (!msqQuestionOptions.containsKey(response.getQuestionId())) {
			        msqQuestionOptions.put(response.getQuestionId(), new ArrayList<>());
			        msqQuestionOptions.get(response.getQuestionId()).add(response.getChoosenOption());
			    } else {
			        msqQuestionOptions.get(response.getQuestionId()).add(response.getChoosenOption());
			    }
		    }
		}
		for (StudentResponseVO response : responses) {
		    if (questionMap.get(response.getQuestionId()).getQuestionType() == Constants.MSQ) {
		    	response.setChoosenOptions(msqQuestionOptions.getOrDefault(response.getQuestionId(), new ArrayList<>()));
		    }
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
		    if (correspondingFeedbacks != null) {
			    response.setIsCorrect(correspondingFeedbacks.getIsCorrect());
			    response.setPointsGained(correspondingFeedbacks.getGainedPoints());
		    }
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
		examResponse.setAllowedAttempts(exam.getAllowedAttempts());
		examResponse.setIsResultsPublished(exam.getIsResultsPublished());
		examResponse.setLastUpdatedOn(exam.getLastUpdatedOn());
	}

	@Override
	public ExamResponseVO getStudentExamResponsesBySession(UUID examSession, ExamVO exam, UserVO user) throws GlobalException {
		ExamResponseVO examResponses = new ExamResponseVO();
		
		populateExamInResponses(examResponses, exam);
		populateExamTakers(examResponses);
		populateAnswerKey(examResponses);
		populateStudentResponsesBySession(examSession, examResponses);
		
		if (Boolean.TRUE.equals(exam.getAutoGrade())) {
			gradeAutoGradeQuestions(examResponses, user);
		}

		return examResponses;
	}

	@Override
	public ResponseFeedbackVO updateExamFeedback(ResponseFeedbackVO feedback, UserVO user) throws GlobalException {
		ResponseFeedbackVO existingFeedback = responseRepo.retrieveExamFeedbackByFeedbackId(feedback.getFeedbackId());
		if (existingFeedback == null) {
			throw new GlobalException("Update Failed, Feedback Not Found", HttpStatus.NOT_FOUND);
		}
		Boolean isChangesPresent = Boolean.FALSE;
		if (!existingFeedback.getFeedback().equalsIgnoreCase(feedback.getFeedback())) {
			isChangesPresent = Boolean.TRUE;
		}
		if (!existingFeedback.getGainedPoints().equals(feedback.getGainedPoints())) {
			isChangesPresent = Boolean.TRUE;
		}
		if (isChangesPresent.equals(Boolean.FALSE)) {
			throw new GlobalException("No Fields to Update", HttpStatus.BAD_REQUEST);
		}
		Boolean isUpdated = responseRepo.updateExamFeedbackByFeedbackId(feedback, user);
		if (Boolean.TRUE.equals(isUpdated)) {
			return feedback;
		}
		
		return existingFeedback;
		
	}

	@Override
	public ResponseFeedbackVO createExamFeedback(ResponseFeedbackVO feedback, UserVO user) throws GlobalException {
		ResponseFeedbackVO checkedFeedback = responseRepo.checkIsFeedbackProvided(feedback.getResponseId());
		if (checkedFeedback != null) {
			if (feedback.getFeedbackId() == null) {
				feedback.setFeedbackId(checkedFeedback.getFeedbackId());
			}
			updateExamFeedback(feedback, user);
			return feedback;
		}
		Boolean isSaved = responseRepo.saveExamFeedback(user, feedback);
		if (Boolean.FALSE.equals(isSaved)) {
			throw new GlobalException("Save Failed", HttpStatus.BAD_REQUEST);
		}
		return feedback;
	}

	@Override
	public Boolean saveStudentGrades(UUID examSession, ExamVO exam, UserVO user) throws GlobalException {
		ExamResponseVO studentExamDetails = getStudentExamResponsesBySession(examSession, exam, user);
		Double totalPoints = getTotalPoints(studentExamDetails);
		
		responseRepo.savestudentResults(studentExamDetails.getStudentResponses().get(0).getStudentId(), exam.getExamId(), totalPoints);
		return responseRepo.saveStudentGrades(examSession);
	}

	private Double getTotalPoints(ExamResponseVO studentExamDetails) {
		Double totalPoints = 0.0;
		for (StudentResponseVO response : studentExamDetails.getStudentResponses()) {
            if (response.getPointsGained() != null) {
                totalPoints += response.getPointsGained();
            }
        }
        return totalPoints;
	}

	@Override
	public Boolean publishResults(Integer examId) throws GlobalException {
		return responseRepo.publishExamResults(examId);
	}

	@Override
	public Integer getExamIdByExamSession(UUID examSession) throws GlobalException {
		return responseRepo.getExamIdByExamSession(examSession);
	}

}
