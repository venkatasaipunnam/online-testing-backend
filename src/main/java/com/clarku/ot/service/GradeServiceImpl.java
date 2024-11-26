package com.clarku.ot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clarku.ot.exception.GlobalException;
import com.clarku.ot.repository.IExamRepo;
import com.clarku.ot.repository.IExamResponseRepo;
import com.clarku.ot.repository.IGradesRepo;
import com.clarku.ot.vo.ExamGradeVO;
import com.clarku.ot.vo.ExamResponseVO;
import com.clarku.ot.vo.ExamVO;
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
		return new ExamGradeVO();
	}

	@Override
	public StudentResultVO getStudentGrades(ExamVO examDetails, UserVO user) throws GlobalException {
		// TODO Auto-generated method stub
		return null;
	}

}
