package com.tubebreakup.exception;

import com.tubebreakup.model.NameProvider;
import org.springframework.http.HttpStatus;

public class InvalidRequestException extends ErrorCodedHttpException {

	private static final long serialVersionUID = -353920316504918936L;

	public InvalidRequestException(NameProvider nameProvider, String message) {
		super(HttpStatus.BAD_REQUEST, CommonErrors.INVALID_REQUEST, nameProvider, message);
	}
	public InvalidRequestException(NameProvider nameProvider, String message, Throwable throwable) {
		super(HttpStatus.BAD_REQUEST, CommonErrors.INVALID_REQUEST, nameProvider, message, throwable);
	}
}
