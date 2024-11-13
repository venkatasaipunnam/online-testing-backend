package com.clarku.ot.vo;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateQuestionVO {

	@NotNull(message = "Please provide Question Id", groups = { UpdateQuestionValidation.class })
	private Integer questionId;

	@NotNull(message = "Please provide Question Title", groups = { UpdateQuestionValidation.class })
	@Pattern(regexp = "^(?=.*[A-Za-z])[A-Za-z0-9. ]{1,100}$", message = "Enter Valid Exam Title", groups = { UpdateQuestionValidation.class })
	private String questionTitle;

	private String questionDetails;

	private String questionImagePath;

	@NotNull(message = "Please provide question type", groups = { UpdateQuestionValidation.class })
	private String questionType;
	
	@NotEmpty(message = "Please provide question Points", groups = { UpdateQuestionValidation.class })
	private Double points;
	
	private List<UpdateOptionVO> options;
	
	public interface UpdateQuestionValidation {
	}
}