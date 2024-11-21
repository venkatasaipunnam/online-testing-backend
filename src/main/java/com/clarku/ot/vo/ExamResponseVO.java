package com.clarku.ot.vo;

import java.util.List;

import lombok.Data;

@Data
public class ExamResponseVO extends ExamVO {
	
	private List<StudentResponseVO> studentResponses;
	
	private List<ExamRespondentVO> respondedUsers;
	
	private List<ExamKeyVO> examKeys;
	
}
