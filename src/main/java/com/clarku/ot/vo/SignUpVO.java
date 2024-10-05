package com.clarku.ot.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SignUpVO {

	@Schema(hidden = true)
	private Integer userId;

	@NotNull(message = "First name should be provided", groups = { SignUpValidation.class, CreateUserValidation.class })
	@Pattern(regexp = "^(?=.*[A-Za-z])[A-Za-z. ]{1,30}$", message = "Enter Valid First Name", groups = { SignUpValidation.class })
	private String firstName;

	@NotNull(message = "Last name should be provided", groups = { SignUpValidation.class, CreateUserValidation.class })
	@Pattern(regexp = "^(?=.*[A-Za-z])[A-Za-z. ]{1,30}$", message = "Enter Valid Last Name", groups = { SignUpValidation.class })
	private String lastName;

	@Email(message = "Email should be valid", groups = { SignUpValidation.class, CreateUserValidation.class })
	@NotNull(message = "Email should be valid", groups = { SignUpValidation.class, CreateUserValidation.class })
	private String emailId;
	
	@NotNull(message = "User Name should be provided", groups = { SignUpValidation.class, CreateUserValidation.class })
	@Pattern(regexp = "^(?=.*[A-Za-z0-9])[A-Za-z. 0-9]{1,30}$", message = "Enter Valid User Name", groups = { SignUpValidation.class })
	private String userName;

	@NotNull(message = "user type should be selected", groups = { SignUpValidation.class, CreateUserValidation.class })
	private String userType;

	@NotEmpty(message = "Password should not be empty", groups = { SignUpValidation.class })
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!.@#$&*])[A-Za-z0-9!.@#$&*]{8,20}$", message = "Create Password should match requirements", groups = { SignUpValidation.class })
	private String createPassword;

	@NotEmpty(message = "Password should not be empty", groups = { SignUpValidation.class })
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!.@#$&*])[A-Za-z0-9!.@#$&*]{8,20}$", message = "Confirm Password should match requirements", groups = { SignUpValidation.class })
	private String confirmPassword;

	public interface SignUpValidation {
	}
	public interface CreateUserValidation {
	}
}