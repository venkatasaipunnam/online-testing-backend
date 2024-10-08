package com.clarku.ot.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clarku.ot.exception.GlobalException;
import com.clarku.ot.exception.LoginException;
import com.clarku.ot.service.ILoginService;
import com.clarku.ot.service.INotificationService;
import com.clarku.ot.service.ISessionService;
import com.clarku.ot.service.IUserService;
import com.clarku.ot.vo.ErrorVO;
import com.clarku.ot.vo.LoginVO;
import com.clarku.ot.vo.LoginVO.LoginValidation;
import com.clarku.ot.vo.SessionVO;
import com.clarku.ot.vo.SignUpVO;
import com.clarku.ot.vo.SignUpVO.SignUpValidation;
import com.clarku.ot.vo.UserVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/")
public class LoginController {

	@Autowired
	ILoginService loginService;

	@Autowired
	IUserService userService;

	@Autowired
	ISessionService session;

	@Autowired
	INotificationService notify;

	@Operation(summary = "Login API", description = "Return the user profile and session detail upon successful login")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Successfully logged in", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserVO.class))), 
	    @ApiResponse(responseCode = "400", description = "Bad request. Please check all the required fields are entered or not", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "401", description = "Authorization information is missing or invalid.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "404", description = "A user with the specified ID was not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "500", description = "Unexpected error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class)))
	})
	@PostMapping("login")
	public ResponseEntity<UserVO> signIn(@Validated(LoginValidation.class) @RequestBody LoginVO loginDetails) throws GlobalException, LoginException {
		UserVO userDetails = loginService.signIn(loginDetails);
		loginService.updateLastLogin(userDetails.getUserId());
		SessionVO userSession = session.createSession(userDetails.getUserId());
		userDetails.setSession(userSession);
		return new ResponseEntity<>(userDetails, HttpStatus.OK);
	}

	@Operation(summary = "SignUp API", description = "Return True or False based on the profile created")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Successfully signup", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))), 
	    @ApiResponse(responseCode = "400", description = "Bad request. Please check all the required fields are entered or not", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "401", description = "Authorization information is missing or invalid.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "404", description = "When No records of that request found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "500", description = "Unexpected error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class)))
	})
	@PostMapping("signup")
	public ResponseEntity<Boolean> signUp(@Validated(SignUpValidation.class) @RequestBody SignUpVO userDetails) throws GlobalException {
		Boolean isSignUpSuccess = loginService.signUpUser(userDetails);
		if (Boolean.TRUE.equals(isSignUpSuccess)) {
			notify.sendSuccessSignUpMail(userDetails);
		}
		return new ResponseEntity<>(isSignUpSuccess, HttpStatus.OK);
	}

	@Operation(summary = "Logout API", description = "Return True or False based on the logout status")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Successfully logged out", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))), 
	    @ApiResponse(responseCode = "400", description = "Bad request. Please check all the required fields are entered or not", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "401", description = "Authorization information is missing or invalid.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "404", description = "When No records of that request found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "500", description = "Unexpected error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class)))
	})
	@PostMapping("logout")
	public ResponseEntity<Boolean> logout(@RequestBody UUID sessionId) throws GlobalException {
		session.endSession(sessionId.toString());
		return new ResponseEntity<>(true, HttpStatus.OK);
	}

}