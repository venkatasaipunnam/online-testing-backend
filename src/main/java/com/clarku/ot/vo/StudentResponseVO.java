package com.clarku.ot.vo;

import lombok.Data;

@Data
public class StudentResponseVO {
	
	private Integer responseId;

	private Integer studentId;
	
	private Integer questionId;
	
	private Integer choosenOption;
	
	private String answerText;
	
	private String answerImagePath;
	
	private String answerFilePath;
	
	private Double pointsGained;
	
	private Boolean isCorrect;

	private Integer correctOption;

	private ResponseFeedbackVO feedback;
}
