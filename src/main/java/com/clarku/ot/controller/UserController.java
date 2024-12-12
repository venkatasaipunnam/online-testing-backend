package com.clarku.ot.controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clarku.ot.exception.GlobalException;
import com.clarku.ot.service.IAuthService;
import com.clarku.ot.service.INotificationService;
import com.clarku.ot.service.ISessionService;
import com.clarku.ot.service.IUserService;
import com.clarku.ot.utils.Constants;
import com.clarku.ot.vo.ChangePassVO;
import com.clarku.ot.vo.ChangePassVO.ChangePassValidation;
import com.clarku.ot.vo.ErrorVO;
import com.clarku.ot.vo.SessionVO;
import com.clarku.ot.vo.UserVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/")
public class UserController {

	@Autowired
	IUserService userService;

	@Autowired
	IAuthService authService;

	@Autowired
	INotificationService notify;

	@Autowired
	ISessionService sessionService;

	@Operation(summary = "forget password API", description = "Return true if the password is reseted")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Successfully resetting password", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))), 
	    @ApiResponse(responseCode = "400", description = "Bad request. Please check all the required fields are entered or not", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "401", description = "Authorization information is missing or invalid.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "404", description = "A user with the specified ID was not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "500", description = "Unexpected error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class)))
	})
	@GetMapping("forgetpassword")
	public ResponseEntity<Boolean> forgetPassword(@RequestParam("emailId") String emailId) throws GlobalException {
		return new ResponseEntity<>(userService.resetPassword(emailId), HttpStatus.OK);
	}

	@Operation(summary = "Authenticate User API", description = "This API is mainly for the post login and passes the sessionid through headers. It validates every API requests that needs validation.")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Successfully Authentication", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserVO.class))), 
	    @ApiResponse(responseCode = "400", description = "Bad request. Please check all the required fields are entered or not", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "401", description = "Authorization information is missing or invalid.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "404", description = "A user with the specified ID was not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "500", description = "Unexpected error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class)))
	})
	@GetMapping("authuser")
	public ResponseEntity<UserVO> getUser(@RequestHeader HttpHeaders headers) throws GlobalException {
		SessionVO sessionDetails = authService.retrieveSession(headers);
		UserVO user = userService.getUser(sessionDetails.getUserId());
		user.setSession(sessionDetails);
		user.setIsTempPassword(false);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	@Operation(summary = "User Profile API", description = "Return the user profile of individuals as well as for Admin any user profile details.")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserVO.class))), 
	    @ApiResponse(responseCode = "400", description = "Bad request. Please check all the required fields are entered or not", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "401", description = "Authorization information is missing or invalid.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "404", description = "A user with the specified ID was not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "500", description = "Unexpected error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class)))
	})
	@GetMapping("user/profile")
	public ResponseEntity<UserVO> getUserProfile(@RequestHeader HttpHeaders headers, @RequestParam(name = "userId") Integer userId) throws GlobalException {
		SessionVO sessionDetails = authService.retrieveSession(headers);
		UserVO user = userService.getUser(sessionDetails.getUserId());
		if (!user.getUserType().equals(Constants.ADMIN) && !Objects.equals(user.getUserId(), userId) ) {
			throw new GlobalException("Unauthorised to access", HttpStatus.UNAUTHORIZED);
		}
		UserVO searchedUser = userService.getUser(userId);
		return new ResponseEntity<>(searchedUser, HttpStatus.OK);
	}

	@Operation(summary = "User Change Password API", description = "Return True upon successful change of password")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Successfully Change of Password", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserVO.class))), 
	    @ApiResponse(responseCode = "400", description = "Bad request. Please check all the required fields are entered or not", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "401", description = "Authorization information is missing or invalid.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "404", description = "A user with the specified ID was not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "500", description = "Unexpected error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class)))
	})
	@PostMapping("user/changepassword")
	public ResponseEntity<Boolean> userChangePassword(@RequestHeader HttpHeaders headers, @Validated(ChangePassValidation.class) @RequestBody ChangePassVO passVO) throws GlobalException, GlobalException {
		SessionVO sessionDetails = authService.retrieveSession(headers);
		Boolean isChanged = userService.changePassword(sessionDetails.getUserId(), passVO);

		if (Boolean.TRUE.equals(isChanged)) {
			UserVO user = userService.getUser(sessionDetails.getUserId());
			notify.sendPasswordChangeEmail(user.getEmailId(), user.getFirstName());
		}		
		return new ResponseEntity<>(isChanged, HttpStatus.OK);
	}
	
	@Operation(summary = "Update User Profile", description = "Return True upon successful update of password")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Successfully logged in", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserVO.class))), 
	    @ApiResponse(responseCode = "400", description = "Bad request. Please check all the required fields are entered or not", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "401", description = "Authorization information is missing or invalid.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "404", description = "A user with the specified ID was not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "500", description = "Unexpected error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class)))
	})
	@PutMapping("user/update")
	public ResponseEntity<Boolean> updateProfile(@RequestHeader HttpHeaders headers, @RequestBody UserVO updatedUser) throws GlobalException {
		SessionVO sessionDetails = authService.retrieveSession(headers);
		UserVO user = userService.getUser(sessionDetails.getUserId());
		Boolean isUpdated = userService.updateUser(user, updatedUser);

		if (Boolean.TRUE.equals(isUpdated)) {
			notify.sendProfileUpdateEmail(user.getEmailId(), updatedUser.getFirstName());
		}		
		return new ResponseEntity<>(isUpdated, HttpStatus.OK);
	}

}