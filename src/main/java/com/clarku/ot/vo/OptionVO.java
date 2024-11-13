package com.clarku.ot.vo;

import lombok.Data;

@Data
public class OptionVO {
	
	private Integer optionId;
	
	private String optionText;
	
	private String optionImagePath;
	
	private Boolean isCorrect;
	
	private String optionType;
	
	private String createdBy;
	
	private String lastUpdatedBy;
	
	private String lastUpdatedOn;

}
