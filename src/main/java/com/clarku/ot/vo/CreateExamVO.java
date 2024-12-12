package com.clarku.ot.vo;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateExamVO {

	@Schema(hidden = true)
	private Integer examId;

	@NotNull(message = "Please provide Exam Title", groups = { CreateExamValidation.class })
	@Pattern(regexp = "^(?=.*[A-Za-z])[A-Za-z0-9. ]{1,100}$", message = "Enter Valid Exam Title", groups = { CreateExamValidation.class })
	private String title;

	private String description;

	@NotNull(message = "Please provide Exam Start Time", groups = { CreateExamValidation.class })
	private String startTime;
	
	@NotNull(message = "Please provide Exam End Time", groups = { CreateExamValidation.class })
	private String endTime;

	@NotNull(message = "Please provide Exam Duration", groups = { CreateExamValidation.class })
	private Integer duration;

	@NotNull(message = "Please provide Exam total points", groups = { CreateExamValidation.class })
	private Double totalPoints;

	private Boolean autoGrade;

	private String status;
	
	private List<CreateQuestionVO> questions;

	public interface CreateExamValidation {
	}
}