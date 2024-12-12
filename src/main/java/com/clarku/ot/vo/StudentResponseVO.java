package com.clarku.ot.vo;

import java.util.List;

import lombok.Data;

@Data
public class StudentResponseVO {
	
	private Integer responseId;

	private Integer studentId;
	
	private Integer questionId;
	
	private Integer choosenOption;
	
	private List<Integer> choosenOptions;
	
	private String answerText;
	
	private String answerImagePath;
	
	private String answerFilePath;
	
	private Double pointsGained;
	
	private Boolean isCorrect;

	private Integer correctOption;

	private List<Integer> correctOptions;

	private ResponseFeedbackVO feedback;
}
