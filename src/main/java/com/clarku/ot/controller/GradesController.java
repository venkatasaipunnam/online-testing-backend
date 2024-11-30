package com.clarku.ot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clarku.ot.exception.GlobalException;
import com.clarku.ot.service.IAuthService;
import com.clarku.ot.service.IExamService;
import com.clarku.ot.service.IGradeService;
import com.clarku.ot.service.INotificationService;
import com.clarku.ot.service.ISessionService;
import com.clarku.ot.service.IUserService;
import com.clarku.ot.utils.Constants;
import com.clarku.ot.vo.ErrorVO;
import com.clarku.ot.vo.ExamGradeVO;
import com.clarku.ot.vo.ExamResponseVO;
import com.clarku.ot.vo.ExamVO;
import com.clarku.ot.vo.SessionVO;
import com.clarku.ot.vo.StudentResultVO;
import com.clarku.ot.vo.UserVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("exam/grade")
public class GradesController {

	@Autowired
	IUserService userService;

	@Autowired
	IAuthService authService;

	@Autowired
	INotificationService notify;

	@Autowired
	ISessionService sessionService;

	@Autowired
	IExamService examService;

	@Autowired
	IGradeService gradeService;


	@Operation(summary = "Retrieve Exam Grades By exam Id", description = "This API is mainly for the post login and passes the sessionid through headers. It retrieves the Exam.")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Successfully Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExamGradeVO.class))), 
	    @ApiResponse(responseCode = "400", description = "Bad request. Please check all the required fields are entered or not", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "401", description = "Authorization information is missing or invalid.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "404", description = "A user with the specified ID was not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "500", description = "Unexpected error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class)))
	})
	@GetMapping("")
	public ResponseEntity<ExamGradeVO> retrieveExamResponses(@RequestHeader HttpHeaders headers, @RequestParam(name = "examId") Integer examId) throws GlobalException {
		SessionVO sessionDetails = authService.retrieveSession(headers);
		UserVO user = userService.getUser(sessionDetails.getUserId());
		if (Constants.STUDENT.equalsIgnoreCase(user.getUserType())) {
			throw new GlobalException("Access not granted", HttpStatus.FORBIDDEN);
		}
		ExamVO examDetails = examService.retrieveExam(examId, user);
		ExamGradeVO responses = gradeService.getExamGrades(examDetails, user);
		return new ResponseEntity<>(responses, HttpStatus.OK);
	}
	

	@Operation(summary = "Retrieve Exam Grades By student", description = "This API is mainly for the post login and passes the sessionid through headers. It retrieves the Exam.")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Successfully Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StudentResultVO.class))), 
	    @ApiResponse(responseCode = "400", description = "Bad request. Please check all the required fields are entered or not", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "401", description = "Authorization information is missing or invalid.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "404", description = "A user with the specified ID was not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "500", description = "Unexpected error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class)))
	})
	@GetMapping("/student")
	public ResponseEntity<ExamGradeVO> retrieveStudentGrades(@RequestHeader HttpHeaders headers, @RequestParam(name = "examId") Integer examId) throws GlobalException {
		SessionVO sessionDetails = authService.retrieveSession(headers);
		UserVO user = userService.getUser(sessionDetails.getUserId());
		ExamVO examDetails = examService.retrieveExam(examId, user);
		ExamGradeVO responses = gradeService.getStudentGrades(examDetails, user);
		return new ResponseEntity<>(responses, HttpStatus.OK);
	}

}