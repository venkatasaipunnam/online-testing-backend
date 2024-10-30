package com.clarku.ot.vo;

import java.util.List;

import lombok.Data;

@Data
public class QuestionVO {

	private Integer questionId;

	private String questionTitle;

	private String questionDetails;

	private Double points;

	private String questionType;
	
	private String questionImagePath;
	
	private String createdBy;
	
	private String createdOn;
	
	private String lastUpdatedBy;
	
	private String lastUpdatedOn;
	
	private List<OptionVO> options;
}