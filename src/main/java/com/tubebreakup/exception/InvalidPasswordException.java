package com.tubebreakup.exception;

import com.tubebreakup.model.NameProvider;
import org.springframework.http.HttpStatus;

public class InvalidPasswordException extends ErrorCodedHttpException {

	private static final long serialVersionUID = -2259404999381719616L;

	public InvalidPasswordException(NameProvider nameProvider, String message) {
		super(HttpStatus.CONFLICT, CommonErrors.INVALID_PASSWORD, nameProvider, message);
	}
	
	public InvalidPasswordException(NameProvider nameProvider, String message, Throwable throwable) {
		super(HttpStatus.CONFLICT, CommonErrors.INVALID_PASSWORD, nameProvider, message, throwable);
	}
}
