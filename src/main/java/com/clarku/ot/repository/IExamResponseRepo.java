package com.clarku.ot.repository;

import java.util.List;
import java.util.UUID;

import com.clarku.ot.exception.GlobalException;
import com.clarku.ot.vo.ExamKeyVO;
import com.clarku.ot.vo.ExamRespondentVO;
import com.clarku.ot.vo.ResponseFeedbackVO;
import com.clarku.ot.vo.StudentResponseVO;
import com.clarku.ot.vo.UserVO;

public interface IExamResponseRepo {

	List<ExamRespondentVO> retreiveExamTakers(Integer examId) throws GlobalException;

	List<ExamKeyVO> retrieveExamKey(Integer examId) throws GlobalException;

	List<ResponseFeedbackVO> retrieveExamFeedbacks(Integer examId) throws GlobalException;

	List<StudentResponseVO> retrieveStudentResponses(Integer examId) throws GlobalException;

	List<StudentResponseVO> retrieveStudentResponsesByStudentSession(UUID examSession) throws GlobalException;

	List<ResponseFeedbackVO> retrieveExamFeedbacksBySession(UUID examSession) throws GlobalException;

	Boolean saveExamFeedback(UserVO user, StudentResponseVO response, ResponseFeedbackVO feedback) throws GlobalException;

	ResponseFeedbackVO retrieveExamFeedbackByFeedbackId(Integer feedbackId) throws GlobalException;

	Boolean updateExamFeedbackByFeedbackId(ResponseFeedbackVO feedback, UserVO user) throws GlobalException;

	ResponseFeedbackVO checkIsFeedbackProvided(Integer responseId) throws GlobalException;

	Boolean saveExamFeedback(UserVO user, ResponseFeedbackVO feedback) throws GlobalException;

	Boolean saveStudentGrades(UUID examSession) throws GlobalException;

	Integer getExamIdByExamSession(UUID examSession) throws GlobalException;

	Boolean savestudentResults(Integer studentId, Integer examId, Double totalPoints) throws GlobalException;

	Boolean publishExamResults(Integer examId) throws GlobalException;

}
