package com.clarku.ot.vo;

import lombok.Data;

@Data
public class ExamKeyVO {
	
	private Integer examId;
	
	private Integer questionId;
	
	private Integer optionId;
	
	private Boolean isCorrect;

}
