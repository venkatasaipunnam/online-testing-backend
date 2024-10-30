package com.clarku.ot.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOptionVO {

	@NotNull(message = "Please provide Option Id", groups = { UpdateOptionValidation.class })
	private Integer optionId;
	
	@NotNull(message = "Please provide Option Text", groups = { UpdateOptionValidation.class })
	private String optionText;
	
	private String optionImagePath;

	@NotNull(message = "Please provide Option Type", groups = { UpdateOptionValidation.class })
	private String optionType;
	
	@NotNull(message = "Please provide Option is Correct or Not", groups = { UpdateOptionValidation.class })
	private Boolean isCorrect;

	public interface UpdateOptionValidation {}
}
