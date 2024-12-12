package com.clarku.ot.vo;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateQuestionVO {

	@Schema(hidden = true)
	private Integer questionId;
	
	@Schema(hidden = true)
	@NotNull(message = "Please provide ExamId", groups = { CreateExamQuestionValidation.class })
	private Integer examId;

	@NotNull(message = "Please provide Question Title", groups = { CreateQuestionValidation.class, CreateExamQuestionValidation.class })
	@Pattern(regexp = "^(?=.*[A-Za-z])[A-Za-z0-9. ]{1,100}$", message = "Enter Valid Exam Title", groups = { CreateQuestionValidation.class, CreateExamQuestionValidation.class })
	private String questionTitle;

	private String questionDetails;

	private String questionImagePath;

	@NotNull(message = "Please provide question type", groups = { CreateQuestionValidation.class, CreateExamQuestionValidation.class })
	private String questionType;
	
	@NotNull(message = "Please provide question Points", groups = { CreateQuestionValidation.class, CreateExamQuestionValidation.class })
	private Double points;
	
	private List<CreateOptionVO> options;

	public interface CreateQuestionValidation {
	}
	
	public interface CreateExamQuestionValidation {
	}
}