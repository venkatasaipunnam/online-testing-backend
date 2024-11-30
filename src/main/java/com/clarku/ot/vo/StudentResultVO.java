package com.clarku.ot.vo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class StudentResultVO {

	private UUID examSession;

	private Integer studentId;
	
	private String studentName;
	
	private LocalDateTime examStartTime;
	
	private LocalDateTime examEndTime;
	
	private Double examScore;
	
	private Double examPercentage;
	
	private Integer totalCorrect;
	
	private Integer totalWrong;
	
	private Integer totalAnswered;
	
	private Integer totalSkipped;
	
	private List<StudentResponseVO> studentResponses;
	
}
