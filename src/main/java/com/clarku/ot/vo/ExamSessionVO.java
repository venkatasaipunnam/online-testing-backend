package com.clarku.ot.vo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class ExamSessionVO {

	private UUID examSessionId;

	private Integer userId;
	
	private Integer examId;

	private LocalDateTime startTime;

	private LocalDateTime endTime;

	private ExamVO exam;
	
	private UserVO user;
	
	private List<UserExamResponseVO> userResponses;

}
