/**
 * 
 */
package com.clarku.ot.exception;

import org.springframework.http.HttpStatus;

/**
 * 
 */
public class LoginException extends Exception {

	private static final long serialVersionUID = 1L;
	private final HttpStatus httpStatus;
	
	public LoginException(String message, HttpStatus httpStatus) {
		super(message, null, false, false);
		this.httpStatus = httpStatus;
	}
	
	public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}
