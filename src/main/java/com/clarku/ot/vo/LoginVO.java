package com.clarku.ot.vo;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginVO {

	private String username;

	private String emailId;

	@NotEmpty(message = "Password should not be empty", groups = { LoginValidation.class })
	private String password;
	
	private Boolean isLoginByEmail;

	public interface LoginValidation {

	}

}