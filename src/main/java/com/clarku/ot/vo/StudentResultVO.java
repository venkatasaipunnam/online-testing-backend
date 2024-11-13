package com.clarku.ot.vo;

import java.util.List;

import lombok.Data;

@Data
public class StudentResultVO {

	private Integer studentId;
	
	private String studentName;
	
	private String examStartTime;
	
	private String examEndTime;
	
	private Double examScore;
	
	private Integer totalCorrect;
	
	private Integer totalWrong;
	
	private Integer totalAnswered;
	
	private Integer totalSkipped;
	
	private List<StudentResponseVO> studentResponses;
	
}
