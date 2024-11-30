package com.clarku.ot.service;

import java.util.List;

import com.clarku.ot.exception.GlobalException;
import com.clarku.ot.vo.AssignExamVO;
import com.clarku.ot.vo.CreateExamVO;
import com.clarku.ot.vo.CreateOptionVO;
import com.clarku.ot.vo.CreateQuestionVO;
import com.clarku.ot.vo.ExamMetaDataVO;
import com.clarku.ot.vo.ExamSessionVO;
import com.clarku.ot.vo.ExamVO;
import com.clarku.ot.vo.OptionVO;
import com.clarku.ot.vo.QuestionVO;
import com.clarku.ot.vo.UpdateExamVO;
import com.clarku.ot.vo.UpdateOptionVO;
import com.clarku.ot.vo.UpdateQuestionVO;
import com.clarku.ot.vo.UserExamResponseVO;
import com.clarku.ot.vo.UserVO;

public interface IExamService {

	Boolean createExam(CreateExamVO examVO, UserVO user) throws GlobalException;

	List<ExamVO> retrieveExamsByUser(UserVO user) throws GlobalException;

	ExamVO retrieveExamDetails(Integer examId) throws GlobalException;

	List<ExamVO> retrieveAllExams() throws GlobalException;

	QuestionVO createQuestion(CreateQuestionVO question, UserVO user) throws GlobalException;

	CreateOptionVO createOption(CreateOptionVO option, UserVO user) throws GlobalException;

	List<ExamVO> retrieveExamsAssignedToUser(UserVO user) throws GlobalException;

	Boolean updateExam(UpdateExamVO examVO, UserVO user) throws GlobalException;

	Boolean updateQuestion(UpdateQuestionVO question, UserVO user) throws GlobalException;

	Boolean updateOption(UpdateOptionVO option, UserVO user) throws GlobalException;

	QuestionVO retrieveQuestionDetails(Integer questionId) throws GlobalException;

	OptionVO retrieveOptionDetails(Integer optionId) throws GlobalException;

	Boolean assignExamsToUser(AssignExamVO assignExam, UserVO user) throws GlobalException;

	ExamVO retrieveExam(Integer examId, UserVO user) throws GlobalException;

	Boolean deleteExam(Integer examId, UserVO user) throws GlobalException;

	Boolean deleteQuestion(Integer questionId, UserVO user) throws GlobalException;

	Boolean deleteOption(Integer optionId, UserVO user) throws GlobalException;

	ExamSessionVO startUserExam(Integer examId, UserVO user) throws GlobalException;

	UserExamResponseVO saveUserExamResponse(UserExamResponseVO examResponse, UserVO user, ExamSessionVO examSession) throws GlobalException;

	Boolean endUserExam(UserVO user, Integer examId, ExamSessionVO examSession) throws GlobalException;

	ExamMetaDataVO getUsersExamMetaData(UserVO user) throws GlobalException;

}