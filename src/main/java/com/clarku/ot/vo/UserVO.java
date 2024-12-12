package com.clarku.ot.vo;

import lombok.Data;

@Data
public class UserVO {

	private Integer userId;

	private String firstName;

	private String lastName;
	
	private String username;

	private String emailId;

	private String userType;
	
	private Boolean isActive;
	
	private Boolean isTempPassword;
	
	private Boolean isLocked;

	private SessionVO session;
}
