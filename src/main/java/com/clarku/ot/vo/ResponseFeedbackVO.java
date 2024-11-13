package com.clarku.ot.vo;

import lombok.Data;

@Data
public class ResponseFeedbackVO {

	private Integer feedbackerId;
	
	private String feedbackerName;
	
	private String feedback;
	
	private String feedbackPostedTime;
}
