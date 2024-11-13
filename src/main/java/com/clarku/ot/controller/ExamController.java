package com.clarku.ot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.clarku.ot.service.IExamService;
import com.clarku.ot.service.INotificationService;
import com.clarku.ot.service.ISessionService;
import com.clarku.ot.service.IUserService;
import com.clarku.ot.utils.Constants;
import com.clarku.ot.vo.AssignExamVO;
import com.clarku.ot.vo.AssignExamVO.AssignExamValidation;
import com.clarku.ot.vo.CreateExamVO;
import com.clarku.ot.vo.CreateExamVO.CreateExamValidation;
import com.clarku.ot.vo.CreateOptionVO;
import com.clarku.ot.vo.CreateOptionVO.CreateQuestionOptionValidation;
import com.clarku.ot.vo.CreateQuestionVO;
import com.clarku.ot.vo.CreateQuestionVO.CreateExamQuestionValidation;
import com.clarku.ot.vo.ErrorVO;
import com.clarku.ot.vo.ExamVO;
import com.clarku.ot.vo.SessionVO;
import com.clarku.ot.vo.UpdateExamVO;
import com.clarku.ot.vo.UpdateExamVO.UpdateExamValidation;
import com.clarku.ot.vo.UpdateOptionVO;
import com.clarku.ot.vo.UpdateOptionVO.UpdateOptionValidation;
import com.clarku.ot.vo.UpdateQuestionVO;
import com.clarku.ot.vo.UpdateQuestionVO.UpdateQuestionValidation;
import com.clarku.ot.vo.UserVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("exam/")
public class ExamController {

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

	@Operation(summary = "Create Exam API", description = "This API is mainly for the post login and passes the sessionid through headers. It creates the Exam for the user.")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Successfully Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))), 
	    @ApiResponse(responseCode = "400", description = "Bad request. Please check all the required fields are entered or not", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "401", description = "Authorization information is missing or invalid.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "404", description = "A user with the specified ID was not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "500", description = "Unexpected error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class)))
	})
	@PostMapping("create")
	public ResponseEntity<Boolean> createExam(@RequestHeader HttpHeaders headers, @Validated(CreateExamValidation.class) @RequestBody CreateExamVO examVo) throws GlobalException {
		SessionVO sessionDetails = authService.retrieveSession(headers);
		UserVO user = userService.getUser(sessionDetails.getUserId());
		if (Constants.STUDENT.equalsIgnoreCase(user.getUserType())) {
			throw new GlobalException("Access not granted", HttpStatus.FORBIDDEN);
		}
		Boolean isExamCreated = examService.createExam(examVo, user);
		return new ResponseEntity<>(isExamCreated, HttpStatus.OK);
	}

	@Operation(summary = "Update Exam API", description = "This API is mainly for the post login and passes the sessionid through headers. It updates the Exam for the instructor.")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Successfully Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))), 
	    @ApiResponse(responseCode = "400", description = "Bad request. Please check all the required fields are entered or not", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "401", description = "Authorization information is missing or invalid.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "404", description = "A user with the specified ID was not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "500", description = "Unexpected error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class)))
	})
	@PutMapping("update")
	public ResponseEntity<Boolean> updateExam(@RequestHeader HttpHeaders headers, @Validated(UpdateExamValidation.class) @RequestBody UpdateExamVO examVo) throws GlobalException {
		SessionVO sessionDetails = authService.retrieveSession(headers);
		UserVO user = userService.getUser(sessionDetails.getUserId());
		if (Constants.STUDENT.equalsIgnoreCase(user.getUserType())) {
			throw new GlobalException("Access not granted", HttpStatus.FORBIDDEN);
		}
		Boolean isExamUpdated = examService.updateExam(examVo, user);
		return new ResponseEntity<>(isExamUpdated, HttpStatus.OK);
	}

	@Operation(summary = "Delete Exam API", description = "This API is mainly for the post login and passes the sessionid through headers. It deleted the Exam for the instructor.")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Successfully Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))), 
	    @ApiResponse(responseCode = "400", description = "Bad request. Please check all the required fields are entered or not", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "401", description = "Authorization information is missing or invalid.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "404", description = "A user with the specified ID was not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "500", description = "Unexpected error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class)))
	})
	@DeleteMapping("delete")
	public ResponseEntity<Boolean> deleteExam(@RequestHeader HttpHeaders headers, @RequestParam(name = "examId") Integer examId) throws GlobalException {
		SessionVO sessionDetails = authService.retrieveSession(headers);
		UserVO user = userService.getUser(sessionDetails.getUserId());
		if (Constants.STUDENT.equalsIgnoreCase(user.getUserType())) {
			throw new GlobalException("Access not granted", HttpStatus.FORBIDDEN);
		}
		Boolean isExamDeleted = examService.deleteExam(examId, user);
		return new ResponseEntity<>(isExamDeleted, HttpStatus.OK);
	}

	@Operation(summary = "Create Question API", description = "This API is mainly for the post login and passes the sessionid through headers. It creates the Question for the Exam.")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Successfully Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))), 
	    @ApiResponse(responseCode = "400", description = "Bad request. Please check all the required fields are entered or not", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "401", description = "Authorization information is missing or invalid.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "404", description = "A user with the specified ID was not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "500", description = "Unexpected error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class)))
	})
	@PostMapping("question/create")
	public ResponseEntity<Boolean> createQuestion(@RequestHeader HttpHeaders headers, @Validated(CreateExamQuestionValidation.class) @RequestBody CreateQuestionVO question) throws GlobalException {
		SessionVO sessionDetails = authService.retrieveSession(headers);
		UserVO user = userService.getUser(sessionDetails.getUserId());
		if (Constants.STUDENT.equalsIgnoreCase(user.getUserType())) {
			throw new GlobalException("Access not granted", HttpStatus.FORBIDDEN);
		}
		Boolean isQuestionCreated = examService.createQuestion(question, user);
		return new ResponseEntity<>(isQuestionCreated, HttpStatus.OK);
	}

	@Operation(summary = "Update Question API", description = "This API is mainly for the post login and passes the sessionid through headers. It updates the Question for the Exam.")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Successfully Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))), 
	    @ApiResponse(responseCode = "400", description = "Bad request. Please check all the required fields are entered or not", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "401", description = "Authorization information is missing or invalid.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "404", description = "A user with the specified ID was not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "500", description = "Unexpected error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class)))
	})
	@PutMapping("question/update")
	public ResponseEntity<Boolean> updateQuestion(@RequestHeader HttpHeaders headers, @Validated(UpdateQuestionValidation.class) @RequestBody UpdateQuestionVO question) throws GlobalException {
		SessionVO sessionDetails = authService.retrieveSession(headers);
		UserVO user = userService.getUser(sessionDetails.getUserId());
		if (Constants.STUDENT.equalsIgnoreCase(user.getUserType())) {
			throw new GlobalException("Access not granted", HttpStatus.FORBIDDEN);
		}
		Boolean isQuestionUpdated = examService.updateQuestion(question, user);
		return new ResponseEntity<>(isQuestionUpdated, HttpStatus.OK);
	}

	@Operation(summary = "Delete Question API", description = "This API is mainly for the post login and passes the sessionid through headers. It deletes the Question for the Exam.")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Successfully Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))), 
	    @ApiResponse(responseCode = "400", description = "Bad request. Please check all the required fields are entered or not", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "401", description = "Authorization information is missing or invalid.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "404", description = "A user with the specified ID was not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "500", description = "Unexpected error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class)))
	})
	@DeleteMapping("question/delete")
	public ResponseEntity<Boolean> deleteQuestion(@RequestHeader HttpHeaders headers, @RequestParam(name = "questionId") Integer questionId) throws GlobalException {
		SessionVO sessionDetails = authService.retrieveSession(headers);
		UserVO user = userService.getUser(sessionDetails.getUserId());
		if (Constants.STUDENT.equalsIgnoreCase(user.getUserType())) {
			throw new GlobalException("Access not granted", HttpStatus.FORBIDDEN);
		}
		Boolean isQuestionDeleted = examService.deleteQuestion(questionId, user);
		return new ResponseEntity<>(isQuestionDeleted, HttpStatus.OK);
	}

	@Operation(summary = "Create Option API", description = "This API is mainly for the post login and passes the sessionid through headers. It creates the Option for the Question.")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Successfully Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))), 
	    @ApiResponse(responseCode = "400", description = "Bad request. Please check all the required fields are entered or not", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "401", description = "Authorization information is missing or invalid.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "404", description = "A user with the specified ID was not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "500", description = "Unexpected error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class)))
	})
	@PostMapping("question/option/create")
	public ResponseEntity<Boolean> createOption(@RequestHeader HttpHeaders headers, @Validated(CreateQuestionOptionValidation.class) @RequestBody CreateOptionVO option) throws GlobalException {
		SessionVO sessionDetails = authService.retrieveSession(headers);
		UserVO user = userService.getUser(sessionDetails.getUserId());
		if (Constants.STUDENT.equalsIgnoreCase(user.getUserType())) {
			throw new GlobalException("Access not granted", HttpStatus.FORBIDDEN);
		}
		Boolean isOptionCreated = examService.createOption(option, user);
		return new ResponseEntity<>(isOptionCreated, HttpStatus.OK);
	}

	@Operation(summary = "Update Option API", description = "This API is mainly for the post login and passes the sessionid through headers. It updates the Options in the question.")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Successfully Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))), 
	    @ApiResponse(responseCode = "400", description = "Bad request. Please check all the required fields are entered or not", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "401", description = "Authorization information is missing or invalid.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "404", description = "A user with the specified ID was not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "500", description = "Unexpected error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class)))
	})
	@PutMapping("question/option/update")
	public ResponseEntity<Boolean> updateOption(@RequestHeader HttpHeaders headers, @Validated(UpdateOptionValidation.class) @RequestBody UpdateOptionVO option) throws GlobalException {
		SessionVO sessionDetails = authService.retrieveSession(headers);
		UserVO user = userService.getUser(sessionDetails.getUserId());
		if (Constants.STUDENT.equalsIgnoreCase(user.getUserType())) {
			throw new GlobalException("Access not granted", HttpStatus.FORBIDDEN);
		}
		Boolean isOptionUpdated = examService.updateOption(option, user);
		return new ResponseEntity<>(isOptionUpdated, HttpStatus.OK);
	}

	@Operation(summary = "Delete Option API", description = "This API is mainly for the post login and passes the sessionid through headers. It deletes the Option for the Question.")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Successfully Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))), 
	    @ApiResponse(responseCode = "400", description = "Bad request. Please check all the required fields are entered or not", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "401", description = "Authorization information is missing or invalid.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "404", description = "A user with the specified ID was not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "500", description = "Unexpected error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class)))
	})
	@DeleteMapping("question/option/delete")
	public ResponseEntity<Boolean> deleteOption(@RequestHeader HttpHeaders headers, @RequestParam(name = "optionId") Integer optionId) throws GlobalException {
		SessionVO sessionDetails = authService.retrieveSession(headers);
		UserVO user = userService.getUser(sessionDetails.getUserId());
		if (Constants.STUDENT.equalsIgnoreCase(user.getUserType())) {
			throw new GlobalException("Access not granted", HttpStatus.FORBIDDEN);
		}
		Boolean isOptionDeleted = examService.deleteOption(optionId, user);
		return new ResponseEntity<>(isOptionDeleted, HttpStatus.OK);
	}

	@Operation(summary = "Retrieve Exam Details Based on User", description = "This API is mainly for the post login and passes the sessionid through headers. It retrieves the istructor created Exams.")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Successfully Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExamVO.class))), 
	    @ApiResponse(responseCode = "400", description = "Bad request. Please check all the required fields are entered or not", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "401", description = "Authorization information is missing or invalid.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "404", description = "A user with the specified ID was not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "500", description = "Unexpected error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class)))
	})
	@GetMapping("created")
	public ResponseEntity<List<ExamVO>> retrieveExamByUser(@RequestHeader HttpHeaders headers) throws GlobalException {
		SessionVO sessionDetails = authService.retrieveSession(headers);
		UserVO user = userService.getUser(sessionDetails.getUserId());
		if (Constants.STUDENT.equalsIgnoreCase(user.getUserType())) {
			throw new GlobalException("Access not granted", HttpStatus.FORBIDDEN);
		}
		List<ExamVO> exams = examService.retrieveExamsByUser(user);
		return new ResponseEntity<>(exams, HttpStatus.OK);
	}

	@Operation(summary = "Retrieve Exam Details By exam Id", description = "This API is mainly for the post login and passes the sessionid through headers. It retrieves the Exam.")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Successfully Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExamVO.class))), 
	    @ApiResponse(responseCode = "400", description = "Bad request. Please check all the required fields are entered or not", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "401", description = "Authorization information is missing or invalid.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "404", description = "A user with the specified ID was not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "500", description = "Unexpected error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class)))
	})
	@GetMapping("get")
	public ResponseEntity<ExamVO> retrieveExam(@RequestHeader HttpHeaders headers, @RequestParam(name = "examId") Integer examId) throws GlobalException {
		SessionVO sessionDetails = authService.retrieveSession(headers);
		UserVO user = userService.getUser(sessionDetails.getUserId());
		ExamVO exam = examService.retrieveExam(examId, user);
		return new ResponseEntity<>(exam, HttpStatus.OK);
	}


	@Operation(summary = "Retrieve Exam Details Assigned on User", description = "This API is mainly for the post login and passes the sessionid through headers. It retrieves the Exams assigned to the logged-in user.")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Successfully Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExamVO.class))), 
	    @ApiResponse(responseCode = "400", description = "Bad request. Please check all the required fields are entered or not", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "401", description = "Authorization information is missing or invalid.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "404", description = "A user with the specified ID was not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "500", description = "Unexpected error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class)))
	})
	@GetMapping("assigned")
	public ResponseEntity<List<ExamVO>> retrieveExamsAssignedToUser(@RequestHeader HttpHeaders headers) throws GlobalException {
		SessionVO sessionDetails = authService.retrieveSession(headers);
		UserVO user = userService.getUser(sessionDetails.getUserId());
		List<ExamVO> exams = examService.retrieveExamsAssignedToUser(user);
		return new ResponseEntity<>(exams, HttpStatus.OK);
	}

	@Operation(summary = "Assign Exams to User", description = "This API is mainly for the post login and passes the sessionid through headers. It assignes the Exams to the students.")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Successfully Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))), 
	    @ApiResponse(responseCode = "400", description = "Bad request. Please check all the required fields are entered or not", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "401", description = "Authorization information is missing or invalid.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "404", description = "A user with the specified ID was not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class))),
	    @ApiResponse(responseCode = "500", description = "Unexpected error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class)))
	})
	@PostMapping("assign")
	public ResponseEntity<Boolean> assignExamsToUser(@RequestHeader HttpHeaders headers, @Validated(AssignExamValidation.class) @RequestBody AssignExamVO assignExam ) throws GlobalException {
		SessionVO sessionDetails = authService.retrieveSession(headers);
		UserVO user = userService.getUser(sessionDetails.getUserId());
		if (Constants.STUDENT.equalsIgnoreCase(user.getUserType())) {
			throw new GlobalException("Access not granted", HttpStatus.FORBIDDEN);
		}
		Boolean isExamAssigned = examService.assignExamsToUser(assignExam, user);
		return new ResponseEntity<>(isExamAssigned, HttpStatus.OK);
	}

}