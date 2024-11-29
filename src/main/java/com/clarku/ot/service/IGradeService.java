package com.clarku.ot.service;

import com.clarku.ot.exception.GlobalException;
import com.clarku.ot.vo.ExamGradeVO;
import com.clarku.ot.vo.ExamVO;
import com.clarku.ot.vo.StudentResultVO;
import com.clarku.ot.vo.UserVO;

public interface IGradeService {

	ExamGradeVO getExamGrades(ExamVO examDetails, UserVO user) throws GlobalException;

	ExamGradeVO getStudentGrades(ExamVO examDetails, UserVO user) throws GlobalException;

}