package com.clarku.ot.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOptionVO {

	@Schema(hidden = true)
	@NotNull(message = "Please provide ExamId", groups = { CreateQuestionOptionValidation.class })
	private Integer questionId;
	
	@Schema(hidden = true)
	private Integer optionId;
	
	@NotNull(message = "Please provide option content", groups = { CreateQuestionOptionValidation.class })
	private String optionText;
	
	private String optionImagePath;

	@NotNull(message = "Please provide option type", groups = { CreateQuestionOptionValidation.class })
	private String optionType;
	
	@NotNull(message = "Please provide is option correct or wrong", groups = { CreateQuestionOptionValidation.class })
	private Boolean isCorrect;

	public interface CreateQuestionOptionValidation {}
}
