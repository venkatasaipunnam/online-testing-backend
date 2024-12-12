package com.clarku.ot.vo;

import lombok.Data;

@Data
public class OptionVO {
	
	private Integer questionId;
	
	private Integer optionId;
	
	private String optionText;
	
	private String optionImagePath;
	
	private Boolean isCorrect;
	
	private String optionType;
	
	private Integer createdUserId;
	
	private String createdUser;
	
	private String lastUpdatedUser;
	
	private String lastUpdatedOn;

}
