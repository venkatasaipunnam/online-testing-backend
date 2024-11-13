package com.clarku.ot.repository;

import java.util.List;

import com.clarku.ot.exception.GlobalException;
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

public interface IExamRepo {

	Boolean createExam(CreateExamVO examVO, UserVO user) throws GlobalException;

	Integer createQuestion(CreateQuestionVO question, Integer userId) throws GlobalException;

	Integer createOption(CreateOptionVO option, Integer userId) throws GlobalException;

	Boolean mapQuestionWithExam(Integer examId, Integer questionId) throws GlobalException;

	Boolean mapOptionWithQuestion(Integer questionId, Integer optionId, Boolean isCorrect) throws GlobalException;

	Boolean updateQuestion(UpdateQuestionVO updatedQuestion, Integer userId) throws GlobalException;

	Boolean updateOption(UpdateOptionVO option, Integer userId) throws GlobalException;

	List<CreateQuestionVO> retrieveQuestionsByIds(List<Integer> questionIds) throws GlobalException;

	List<CreateOptionVO> retrieveOptionsByIds(List<Integer> optionIds) throws GlobalException;

	List<ExamVO> retrieveExamsByUser(UserVO user) throws GlobalException;

	ExamVO retrieveExamDetails(Integer examId) throws GlobalException;

	List<ExamVO> retrieveAllExams() throws GlobalException;

	List<ExamVO> retrieveExamsAssignedToUser(UserVO user) throws GlobalException;

	Boolean updateExam(UpdateExamVO examVO, UserVO user) throws GlobalException;

	QuestionVO retrieveQuestionDetails(Integer questionId) throws GlobalException;

	OptionVO retrieveOptionDetails(Integer optionId) throws GlobalException;

	Boolean assignUserToExam(AssignExamVO assignExam, UserVO user) throws GlobalException;

	List<String> retrieveAssignedUsersToExam(Integer examId) throws GlobalException;

	Boolean deleteExam(Integer examId) throws GlobalException;

	Boolean deleteQuestion(Integer questionId) throws GlobalException;

	Boolean deleteOption(Integer optionId) throws GlobalException;

}
