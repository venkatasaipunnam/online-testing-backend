package com.clarku.ot.vo;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class ExamRespondentVO {

	private UUID examRespondentSession;

	private Integer respondentId;

	private String firstName;

	private String lastName;

	private String emailId;

	private String userType;

	private Boolean isGraded;

	private LocalDateTime examStartedTime;

	private LocalDateTime examEndTime;

}
