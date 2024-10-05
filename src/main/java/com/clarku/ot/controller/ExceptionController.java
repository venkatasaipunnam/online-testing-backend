package com.clarku.ot.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.clarku.ot.exception.GlobalException;
import com.clarku.ot.exception.LoginException;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<Object> handleGlobalException(GlobalException exp, WebRequest request) {
        return createException(exp);
    }

    @ExceptionHandler(LoginException.class)
    public ResponseEntity<Object> handleLoginException(LoginException exp, WebRequest request) {
        return createException(exp);
    }

    private ResponseEntity<Object> createException(GlobalException exp) {
        HttpStatus status = exp.getHttpStatus();
        String errorMessage = exp.getMessage();
        ErrorResponse errorResponse = new ErrorResponse(status.value(), errorMessage);
        return new ResponseEntity<>(errorResponse, status);
    }

    private ResponseEntity<Object> createException(LoginException exp) {
    	HttpStatus status = exp.getHttpStatus();
        ErrorResponse errorResponse = new ErrorResponse(status.value(), exp.getMessage());
        return new ResponseEntity<>(errorResponse, status);
    }

    private static class ErrorResponse {
        private final int status;
        private final String message;

        public ErrorResponse(int status, String message) {
            this.status = status;
            this.message = message;
        }

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
    }
}
