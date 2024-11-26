/**
 * 
 */
package com.clarku.ot.utils;

import java.time.format.DateTimeFormatter;

/**
 * 
 */
public class Constants {

	private Constants() {
	}
	
	// Define the date and time pattern
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public static final String STUDENT = "Student";

	public static final String INSTRUCTOR = "Instructor";

	public static final String ADMIN = "Admin";

	public static final String HEADER_SESSION_ID = "sessionid";

	public static final String REQUESTED = "Requested";
	
	public static final String EMAIL_USERNAME_REQUIRED = "Email/Username required to Login";

	public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";

	public static final String LOGIN_WRONG_PASS_EXP = "Username or password wrong, please try again";

	public static final String USER_LOCKED_EXP = "Account is locked, please contact helpdesk";

	public static final String USER_INACTIVE_EXP = "Account is unaccessable, please contact helpdesk";

	public static final String NOT_REGISTERED_EXP = "Invaild user, Please sign up to login";

	public static final String SESSION_ALREADY_EXISTS_EXP = "Already Logged-in, please logout and try again";

	public static final String INVALID_SESSION_EXP = "Invalid Session, please try to Login";

	public static final String USER_NOT_EXISTS_FORGET_PASS_EXP = "Email Id does not exists, Please check and try again";

	public static final String USER_EXISTS_SIGNUP_EXP = "User Already Exists, Please Sign In";

	public static final String CREATE_CONFIRM_PASS_MISSMATCH_EXP = "Create and Confirm password should be same";

	public static final String WELCOME_SUBJECT = "Welcome to Online Testing Application";

	public static final String SIGNUP_INSTRUCTOR_SUCCESS_TEMPLATE = "signUpInstructorSuccess";

	public static final String SIGNUP_SUCCESS_TEMPLATE = "signUpSuccess";

	public static final String RESET_PASS_SUBJECT = "Password Reset Successful, Temporary Credentials";

	public static final String RESET_PASS_SUCCESS_TEMPLATE = "resetPassSuccess";

	public static final String USER_PASS_CHANGED_SUB = "Password Changed Successfully";

	public static final String USER_PASS_CHANGE_TEMPLATE = "userPassChange";

	public static final String USER_PROFILE_CHANGED_SUB = "Profile Updated Successfully";

	public static final String USER_PROFILE_CHANGE_TEMPLATE = "userProfChange";

	public static final String CUSTOM_MESSAGE_NOTIFCATION = "customMessageTemp";

	public static final String TEMP_PASS_SUBJECT = "Temporary Credentials";

	public static final String TEMP_PASS_SUCCESS_TEMPLATE = "tempPassSuccess";

	public static final String COMPLETED = "COMPLETED";

	public static final String COMPLETED_SMALL = "Completed";

	public static final String CREATED = "CREATED";

	public static final String ACTIVE = "ACTIVE";

	public static final String INACTIVE = "INACTIVE";

	public static final String CLOSED = "CLOSED";

	public static final String HOLD = "HOLD";
	
	public static final String POSTED = "POSTED";

	public static final String CANCELED = "CANCELED";

	public static final String INPROGRESS = "INPROGRESS";

	public static final String INPROGRESS_SMALL = "InProgress";

	public static final String MCQ = "MCQ";
	
	public static final String MSQ = "MSQ";

	public static final String BLANK = "BLANK";
	
	public static final String TRUE_FALSE = "TF";

	public static final String SHORT = "SHORT";
	
	public static final String ESSAY = "ESSAY";
	
	public static final String CHOOSE = "CHOOSE";

	public static final Object HEADER_EXAM_SESSION_ID = "examsessionid";

	public static final String UN_ATTEMPTED = "UnAttempted";
	
	public static final String NOTIFY_EXAM_CREATION_SUB = "New Exam Created : ";

	public static final String NOTIFY_EXAM_CREATION_TEMPLATE = "examCreationSuccess";
	
	public static final String NOTIFY_EXAM_UPDATE_SUB = "Update on your Exam : ";

	public static final String NOTIFY_EXAM_UPDATE_TEMPLATE = "examUpdateSuccess";

	public static final String NOTIFY_EXAM_ASSIGNED_SUB = "New Exam Assigned : ";

	public static final String NOTIFY_EXAM_ASSIGNED_TEMPLATE = "examAssigning";

	public static final String NOTIFY_EXAM_UNASSIGNED_SUB = "Exam UnAssigned : ";

	public static final String NOTIFY_EXAM_UNASSIGNED_TEMPLATE = "examUnAssigned";

	public static final String NOTIFY_EXAM_PRIOR_DAY_SUB = "Reminder: Your Exam is Tomorrow - ";

	public static final String NOTIFY_EXAM_PRIOR_DAY_TEMPLATE = "notifyStudentAboutExam";

	public static final String NOTIFY_EXAM_COMPLETION_SUB = "Exam Completed Successfully : ";

	public static final String NOTIFY_EXAM_COMPLETION_TEMPLATE = "examCompletion";

	public static final String NOTIFY_EXAM_GRADED_SUB = "Your Results are Ready for Exam : ";

	public static final String NOTIFY_EXAM_GRADED_TEMPLATE = "examGraded";

	public static final String GRADED = "GRADED";

	public static final String GRADES_INPROGRESS = "GRADES INPROGRESS";

}
