package com.clarku.ot.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clarku.ot.exception.GlobalException;
import com.clarku.ot.repository.IGradesRepo;
import com.clarku.ot.utils.Constants;
import com.clarku.ot.vo.ExamGradeVO;
import com.clarku.ot.vo.ExamRespondentVO;
import com.clarku.ot.vo.ExamResponseVO;
import com.clarku.ot.vo.ExamVO;
import com.clarku.ot.vo.QuestionVO;
import com.clarku.ot.vo.StudentResponseVO;
import com.clarku.ot.vo.StudentResultVO;
import com.clarku.ot.vo.UserVO;

@Service
public class GradeServiceImpl implements IGradeService {

	@Autowired
	private IExamResponseService responseService;

	@Autowired
	private IGradesRepo gradesRepo;

	@Autowired
	private INotificationService notify;

	@Override
	public ExamGradeVO getExamGrades(ExamVO examDetails, UserVO user) throws GlobalException {
		ExamResponseVO studentResponses = responseService.getStudentResponses(examDetails, user);
		ExamGradeVO grades = populateExamInfo(studentResponses);
		populateAndCalculateStudentGrades(grades, studentResponses);
		populateExamStatistics(grades);
		return grades;
	}

	private void populateExamStatistics(ExamGradeVO grades) {
		OptionalDouble averageScore = grades.getStudentResults().stream().mapToDouble(student -> student.getExamScore())
				.average();
		if (averageScore.isPresent()) {
			grades.setAverageScore(averageScore.getAsDouble());
			grades.setMeanScore(averageScore.getAsDouble());
		} else {
			grades.setAverageScore(0.0);
			grades.setMeanScore(0.0);
		}

		List<Double> scores = grades.getStudentResults().stream().map(student -> student.getExamScore()).sorted()
				.collect(Collectors.toList());

		int size = scores.size();
		if (size == 0) {
			grades.setMedainScore(0.0);
		} else {
			double median = (size % 2 == 1) ? scores.get(size / 2)
					: (scores.get(size / 2 - 1) + scores.get(size / 2)) / 2.0;
			grades.setMedainScore(median);
		}

	}

	private void populateAndCalculateStudentGrades(ExamGradeVO grades, ExamResponseVO studentResponses) {
		Map<Integer, List<StudentResponseVO>> studentResponsesMap = studentResponses.getStudentResponses().stream()
				.collect(Collectors.groupingBy(studentResponse -> studentResponse.getStudentId()));
		Map<Integer, List<StudentResponseVO>> questionResponsesMap = studentResponses.getStudentResponses().stream()
				.collect(Collectors.groupingBy(studentResponse -> studentResponse.getQuestionId()));
		Map<Integer, QuestionVO> questionMap = studentResponses.getQuestions().stream()
				.collect(Collectors.toMap(QuestionVO::getQuestionId, question -> question));
		grades.setStudentResults(new ArrayList<>());
		for (ExamRespondentVO student : studentResponses.getRespondedUsers()) {
			StudentResultVO result = new StudentResultVO();
			result.setExamEndTime(student.getExamEndTime());
			result.setExamSession(student.getExamRespondentSession());
			result.setExamStartTime(student.getExamStartedTime());
			result.setStudentId(student.getRespondentId());
			result.setStudentName(student.getFirstName() + " " + student.getLastName());
			result.setStudentResponses(studentResponsesMap.getOrDefault(student.getRespondentId(), new ArrayList<>()));
			double totalPointsGained = result.getStudentResponses().stream()
					.filter(response -> response.getPointsGained() != null)
					.mapToDouble(StudentResponseVO::getPointsGained).sum();
			result.setExamScore(totalPointsGained);
			long skippedQuestionsCount = result.getStudentResponses().stream()
					.filter(response -> isQuestionSkipped(questionMap, response)) // Use the updated isQuestionSkipped
																					// method
					.count();

			long totalCorrect = questionMap.keySet().stream()
					.filter(question -> questionResponsesMap.get(question).get(0).getIsCorrect()).count();

			result.setTotalSkipped((int) skippedQuestionsCount);
			result.setTotalCorrect((int) totalCorrect);
			result.setTotalWrong(questionMap.size() - result.getTotalCorrect());
			result.setTotalAnswered(questionMap.size() - result.getTotalSkipped());
			result.setExamPercentage(result.getExamScore()/studentResponses.getTotalPoints());
			grades.getStudentResults().add(result);
		}
	}

	private Boolean isQuestionSkipped(Map<Integer, QuestionVO> questionMap, StudentResponseVO response) {
		if (questionMap.get(response.getQuestionId()).getQuestionType().equalsIgnoreCase(Constants.MSQ)
				&& (response.getChoosenOptions() == null || response.getChoosenOption() == null
						|| response.getChoosenOptions().isEmpty())) {
			return true;
		}
		if (questionMap.get(response.getQuestionId()).getQuestionType().equalsIgnoreCase(Constants.MSQ)
				&& questionMap.get(response.getQuestionId()).getQuestionType().equalsIgnoreCase(Constants.MCQ)
				&& questionMap.get(response.getQuestionId()).getQuestionType().equalsIgnoreCase(Constants.TRUE_FALSE)
				&& (response.getChoosenOption() == null)) {
			return true;
		}
		if (!questionMap.get(response.getQuestionId()).getQuestionType().equalsIgnoreCase(Constants.MSQ)
				&& !questionMap.get(response.getQuestionId()).getQuestionType().equalsIgnoreCase(Constants.MCQ)
				&& !questionMap.get(response.getQuestionId()).getQuestionType().equalsIgnoreCase(Constants.TRUE_FALSE)
				&& (response.getAnswerText() == null)) {
			return true;
		}
		return false;
	}

	private ExamGradeVO populateExamInfo(ExamResponseVO exam) {
		ExamGradeVO examGrade = new ExamGradeVO();
		examGrade.setExamId(exam.getExamId());
		examGrade.setAutoGrade(exam.getAutoGrade());
		examGrade.setCreatedOn(exam.getCreatedOn());
		examGrade.setAssignedUserEmails(exam.getAssignedUserEmails());
		examGrade.setCreatedUser(exam.getCreatedUser());
		examGrade.setCreatedUserId(exam.getCreatedUserId());
		examGrade.setDescription(exam.getDescription());
		examGrade.setDuration(exam.getDuration());
		examGrade.setEndTime(exam.getEndTime());
		examGrade.setQuestions(exam.getQuestions());
		examGrade.setStartTime(exam.getStartTime());
		examGrade.setStatus(exam.getStatus());
		examGrade.setTitle(exam.getTitle());
		examGrade.setTotalPoints(exam.getTotalPoints());
		examGrade.setAllowedAttempts(exam.getAllowedAttempts());
		examGrade.setIsResultsPublished(exam.getIsResultsPublished());
		examGrade.setLastUpdatedOn(exam.getLastUpdatedOn());

		examGrade.setNumberOfStudentsTaken(exam.getRespondedUsers().size());
		return examGrade;
	}

	@Override
	public ExamGradeVO getStudentGrades(ExamVO examDetails, UserVO user) throws GlobalException {
		ExamGradeVO grades = getExamGrades(examDetails, user);

		List<StudentResultVO> studentResult = grades.getStudentResults().stream()
				.filter(results -> results.getStudentId().equals(user.getUserId())).collect(Collectors.toList());
		
		grades.setStudentResults(studentResult);
		return grades;
	}

}
