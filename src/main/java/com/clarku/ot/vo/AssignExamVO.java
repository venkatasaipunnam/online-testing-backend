package com.clarku.ot.vo;

import java.util.List;

import com.clarku.ot.config.AtLeastOneValidEmail;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignExamVO {

	@NotNull(message = "Please provide ExamId", groups = { AssignExamValidation.class })
	private Integer examId;

	@AtLeastOneValidEmail(message = "Please provide atleast one valid email", groups = { AssignExamValidation.class })
	private List<String> userEmails;

	public interface AssignExamValidation {}
}
