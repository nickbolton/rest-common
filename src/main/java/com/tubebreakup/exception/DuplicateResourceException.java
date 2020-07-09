package com.tubebreakup.exception;

import com.tubebreakup.model.NameProvider;
import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends ErrorCodedHttpException {

	private static final long serialVersionUID = 7529238312691827941L;

	public DuplicateResourceException(NameProvider nameProvider, String message) {
		super(HttpStatus.GONE, CommonErrors.DUPLICATE_RESOURCE, nameProvider, message);
	}
	
	public DuplicateResourceException(NameProvider nameProvider, String message, Throwable throwable) {
		super(HttpStatus.GONE, CommonErrors.DUPLICATE_RESOURCE, nameProvider, message, throwable);
	}
}
