package com.clarku.ot.vo;

import java.util.List;

import lombok.Data;

@Data
public class UserExamResponseVO {
	
	private Integer responseId;
	
	private Integer userId;
	
	private Integer examId;
	
	private Integer questionId;
	
	private Integer optionId;
	
	private List<Integer> msqOptions;
	
	private String answerResponse;
	
}
