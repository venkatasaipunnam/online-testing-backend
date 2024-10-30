package com.clarku.ot.vo;

import java.util.List;

import lombok.Data;

@Data
public class ExamResultVO {

	private Integer examId;
	
	private String examTitle;
	
	private String examDescription;
	
	private Integer numberOfStudentsTaken;
	
	private Double averageScore;
	
	private Double meanScore;
	
	private Double medainScore;
	
	private Double topScore;
	
	private Double leastScore;
	
	private List<StudentResultVO> studentResults;
}
