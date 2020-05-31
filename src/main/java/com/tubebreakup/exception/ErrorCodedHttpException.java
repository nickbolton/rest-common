package com.tubebreakup.exception;

import com.tubebreakup.model.NameProvider;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorCodedHttpException extends RuntimeException {

    private static final long serialVersionUID = -2249921507621536125L;
    private HttpStatus httpStatus;
    private ErrorCode errorCode;

    private static String buildMessage(NameProvider nameProvider, String defaultMessage) {
        return nameProvider != null ? String.format("(%s) %s", nameProvider.getEmail(), defaultMessage) : defaultMessage;
    }

    private static String buildMessage(String message, Throwable cause) {
        return new StringBuilder(message).append(" : ").append(cause.getLocalizedMessage()).toString();
    }

    public ErrorCodedHttpException(HttpStatus httpStatus, ErrorCode errorCode) {
        super(errorCode.message());
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public ErrorCodedHttpException(HttpStatus httpStatus, ErrorCode errorCode, String messageIn) {
        super(messageIn != null ? messageIn : errorCode.message());
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public ErrorCodedHttpException(HttpStatus httpStatus, ErrorCode errorCode, Throwable cause) {
        super(buildMessage(errorCode.message(), cause), cause);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public ErrorCodedHttpException(HttpStatus httpStatus, ErrorCode errorCode, NameProvider nameProvider) {
        super(ErrorCodedHttpException.buildMessage(nameProvider, errorCode.message()));
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public ErrorCodedHttpException(HttpStatus httpStatus, ErrorCode errorCode, NameProvider nameProvider, Throwable cause) {
        super(buildMessage(buildMessage(nameProvider, errorCode.message()), cause), cause);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public ErrorCodedHttpException(HttpStatus httpStatus, ErrorCode errorCode, NameProvider nameProvider, String messageIn, Throwable cause) {
        super(buildMessage(buildMessage(nameProvider, messageIn != null ? messageIn : errorCode.message()), cause), cause);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public ErrorCodedHttpException(HttpStatus httpStatus, ErrorCode errorCode, String messageIn, Throwable cause) {
        super(buildMessage(messageIn != null ? messageIn : errorCode.message(), cause), cause);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public ErrorCodedHttpException(HttpStatus httpStatus, ErrorCode errorCode, NameProvider nameProvider, String messageIn) {
        super(ErrorCodedHttpException.buildMessage(nameProvider, messageIn != null ? messageIn : errorCode.message()));
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }
}
