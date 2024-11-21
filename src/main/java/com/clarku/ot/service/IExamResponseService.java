package com.clarku.ot.service;

import java.util.UUID;

import com.clarku.ot.exception.GlobalException;
import com.clarku.ot.vo.ExamResponseVO;
import com.clarku.ot.vo.ExamVO;
import com.clarku.ot.vo.UserVO;

public interface IExamResponseService {

	ExamResponseVO getStudentResponses(ExamVO examDetails, UserVO user) throws GlobalException;

	ExamResponseVO getStudentExamResponsesBySession(UUID examSession, ExamVO examDetails, UserVO user) throws GlobalException;

}