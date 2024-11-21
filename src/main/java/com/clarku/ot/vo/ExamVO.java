package com.clarku.ot.vo;

import java.util.List;

import lombok.Data;

@Data
public class ExamVO {

	private Integer examId;

	private String title;

	private String description;

	private String startTime;

	private String endTime;

	private Integer duration;

	private Double totalPoints;

	private Boolean autoGrade;

	private String status;

	private Integer createdUserId;

	private String createdUser;

	private String createdOn;

	private String lastUpdatedUserId;

	private String lastUpdatedUser;

	private String lastUpdatedOn;

	private List<QuestionVO> questions;

	private List<String> assignedUserEmails;

	private Boolean isUserAssigned;

	private String examIndicator;

}