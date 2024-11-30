package com.clarku.ot.vo;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateExamVO {

	@NotNull(message = "Please provide Exam Id", groups = { UpdateExamValidation.class })
	private Integer examId;

	@NotNull(message = "Please provide Exam Title", groups = { UpdateExamValidation.class })
	@Pattern(regexp = "^(?=.*[A-Za-z])[A-Za-z0-9. ]{1,100}$", message = "Enter Valid Exam Title", groups = { UpdateExamValidation.class })
	private String title;

	private String description;

	@NotNull(message = "Please provide Exam Start Time", groups = { UpdateExamValidation.class })
	private String startTime;

	@NotNull(message = "Please provide Exam End Time", groups = { UpdateExamValidation.class })
	private String endTime;

	@NotNull(message = "Please provide Exam Duration", groups = { UpdateExamValidation.class })
	private Integer duration;

	@NotNull(message = "Please provide Exam Total Points", groups = { UpdateExamValidation.class })
	private Double totalPoints;

	private Boolean autoGrade;

	private String status;

	public interface UpdateExamValidation {
	}
}