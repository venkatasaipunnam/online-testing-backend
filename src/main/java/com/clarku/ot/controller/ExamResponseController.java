package com.clarku.ot.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clarku.ot.exception.GlobalException;
import com.clarku.ot.service.IAuthService;
import com.clarku.ot.service.IExamResponseService;
import com.clarku.ot.service.IExamService;
import com.clarku.ot.service.INotificationService;
import com.clarku.ot.service.ISessionService;
import com.clarku.ot.service.IUserService;
import com.clarku.ot.utils.Constants;
import com.clarku.ot.vo.ErrorVO;
import com.clarku.ot.vo.ExamResponseVO;
import com.clarku.ot.vo.ExamVO;
import com.clarku.ot.vo.SessionVO;
import com.clarku.ot.vo.UserVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("exam/response/")
public class ExamResponseController {

	@Autowired
	IUserService userService;

	@Autowired
	IAuthService authService;

	@Autowired
	INotificationService notify;

	@Autowired
	ISessionService sessionService;

	@Autowired
	IExamResponseService responseService;

	@Autowired
	IExamService examService;

	@Operation(summary = "Retrieve Exam Responses By exam Id", description = "This API is mainly for the post login and passes the sessionid through headers. It retrieves the Exam.")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Successfully Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExamResponseVO.class))), 
	    @ApiResponse(responseCode = "400", description = "Bad request. Please check all the required fields are entered or not", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "401", description = "Authorization information is missing or invalid.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "404", description = "A user with the specified ID was not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "500", description = "Unexpected error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class)))
	})
	@GetMapping("get")
	public ResponseEntity<ExamResponseVO> retrieveExamResponses(@RequestHeader HttpHeaders headers, @RequestParam(name = "examId") Integer examId) throws GlobalException {
		SessionVO sessionDetails = authService.retrieveSession(headers);
		UserVO user = userService.getUser(sessionDetails.getUserId());
		if (Constants.STUDENT.equalsIgnoreCase(user.getUserType())) {
			throw new GlobalException("Access not granted", HttpStatus.FORBIDDEN);
		}
		ExamVO examDetails = examService.retrieveExam(examId, user);
		ExamResponseVO responses = responseService.getStudentResponses(examDetails, user);
		return new ResponseEntity<>(responses, HttpStatus.OK);
	}
	
	@Operation(summary = "Retrieve Student Exam Responses By exam Id and Student Id", description = "This API is mainly for the post login and passes the sessionid through headers. It retrieves the Exam.")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Successfully Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExamResponseVO.class))), 
	    @ApiResponse(responseCode = "400", description = "Bad request. Please check all the required fields are entered or not", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "401", description = "Authorization information is missing or invalid.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "404", description = "A user with the specified ID was not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "500", description = "Unexpected error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class)))
	})
	@GetMapping("/student/get")
	public ResponseEntity<ExamResponseVO> retrieveStudentExamResponses(@RequestHeader HttpHeaders headers, @RequestParam(name = "examId") Integer examId, @RequestParam(name = "examSession") UUID examSession) throws GlobalException {
		SessionVO sessionDetails = authService.retrieveSession(headers);
		UserVO user = userService.getUser(sessionDetails.getUserId());
		if (Constants.STUDENT.equalsIgnoreCase(user.getUserType())) {
			throw new GlobalException("Access not granted", HttpStatus.FORBIDDEN);
		}
		ExamVO examDetails = examService.retrieveExam(examId, user);
		ExamResponseVO responses = responseService.getStudentExamResponsesBySession(examSession, examDetails, user);
		return new ResponseEntity<>(responses, HttpStatus.OK);
	}

	
	@Operation(summary = "Retrieve Student Exam Responses By exam Id and Student Id", description = "This API is mainly for the post login and passes the sessionid through headers. It retrieves the Exam.")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Successfully Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExamResponseVO.class))), 
	    @ApiResponse(responseCode = "400", description = "Bad request. Please check all the required fields are entered or not", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "401", description = "Authorization information is missing or invalid.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "404", description = "A user with the specified ID was not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "500", description = "Unexpected error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class)))
	})
	@PostMapping("/feedback/create")
	public ResponseEntity<ExamResponseVO> saveFeedbackToResponse(@RequestHeader HttpHeaders headers, @RequestParam(name = "examId") Integer examId, @RequestParam(name = "examSession") UUID examSession) throws GlobalException {
		SessionVO sessionDetails = authService.retrieveSession(headers);
		UserVO user = userService.getUser(sessionDetails.getUserId());
		if (Constants.STUDENT.equalsIgnoreCase(user.getUserType())) {
			throw new GlobalException("Access not granted", HttpStatus.FORBIDDEN);
		}
		ExamVO examDetails = examService.retrieveExam(examId, user);
		ExamResponseVO responses = responseService.getStudentExamResponsesBySession(examSession, examDetails, user);
		return new ResponseEntity<>(responses, HttpStatus.OK);
	}

}