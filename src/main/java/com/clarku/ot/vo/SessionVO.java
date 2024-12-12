package com.clarku.ot.vo;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class SessionVO {

	private UUID sessionId;

	private Integer userId;

	private LocalDateTime sessionStartTime;

	private LocalDateTime sessionEndTime;

	private Integer extendCount;

	private Boolean active;
}