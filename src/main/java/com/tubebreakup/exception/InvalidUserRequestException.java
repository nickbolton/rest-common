package com.tubebreakup.exception;

import com.tubebreakup.model.NameProvider;
import org.springframework.http.HttpStatus;

public class InvalidUserRequestException extends ErrorCodedHttpException {

	private static final long serialVersionUID = -5290386331999102153L;

	public InvalidUserRequestException(NameProvider nameProvider, String message) {
		super(HttpStatus.FORBIDDEN, CommonErrors.INVALID_USER_REQUEST, nameProvider, message);
	}
	public InvalidUserRequestException(NameProvider nameProvider, String message, Throwable throwable) {
		super(HttpStatus.FORBIDDEN, CommonErrors.INVALID_USER_REQUEST, nameProvider, message, throwable);
	}
}
