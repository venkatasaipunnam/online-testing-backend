package com.clarku.ot.service;

import java.util.UUID;

import com.clarku.ot.exception.GlobalException;
import com.clarku.ot.vo.ExamResponseVO;
import com.clarku.ot.vo.ExamVO;
import com.clarku.ot.vo.ResponseFeedbackVO;
import com.clarku.ot.vo.UserVO;

public interface IExamResponseService {

	ExamResponseVO getStudentResponses(ExamVO examDetails, UserVO user) throws GlobalException;

	ExamResponseVO getStudentExamResponsesBySession(UUID examSession, ExamVO examDetails, UserVO user) throws GlobalException;

	ResponseFeedbackVO updateExamFeedback(ResponseFeedbackVO feedback, UserVO user) throws GlobalException;

	ResponseFeedbackVO createExamFeedback(ResponseFeedbackVO feedback, UserVO user) throws GlobalException;

	Boolean publishResults(Integer examId) throws GlobalException;

	Integer getExamIdByExamSession(UUID examSession) throws GlobalException;

	Boolean saveStudentGrades(UUID examSession, ExamVO exam, UserVO user) throws GlobalException;

}