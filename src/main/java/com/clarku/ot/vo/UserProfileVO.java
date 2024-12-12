package com.clarku.ot.vo;

import lombok.Data;

@Data
public class UserProfileVO {
	
	private Integer userId;

	private String username;

	private String firstName;

	private String lastName;

	private String emailId;

	private String userType;
	
	private Boolean isActive;
	
	private Boolean isLocked;
	
	private Integer failedLoginAttempts;
	
	private String password;
	
	private String tempPassword;

}
