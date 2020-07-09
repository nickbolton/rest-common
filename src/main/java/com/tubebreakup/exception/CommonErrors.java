package com.tubebreakup.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CommonErrors implements ErrorCode {
    UNKNOWN(-1, "Unknown error"),

    // server errors
    SERVER_ERROR(-100, "Server error"),

    // resources
    RESOURCE_NOT_FOUND(-1000, "Resource not found"),
    INVALID_REQUEST(-1001, "Invalid request"),
    DUPLICATE_RESOURCE(-1002, "Duplicate resource"),
    INVALID_PAYLOAD(-1003, "Invalid payload"),

    // authentication
    BAD_CREDENTIALS(-2000, "Bad credentials"),

    // user requests
    INVALID_USER_REQUEST(-3000, "Invalid user request"),
    INVALID_PASSWORD(-3001, "Invalid password"),
    UNFINISHED_USER_REGISTRATION(-3002, "Unfinished user registration"),
    VERIFICATION_TOKEN_NOT_GENERATED(-3003, "Verification token not generated"),
    VERIFICATION_TOKEN_HAS_EXPIRED(-3004, "Verification token has expired"),
    WRONG_VERIFICATION_TOKEN(-3005, "Wrong verification token"),
    EMAIL_CHANGE_NOT_ISSUED(-3006, "Email change not issued"),
    RESOURCE_SERVER_EXCHANGE_FAILED(-3007, "Resource server exchange failed"),

    ;

    @JsonProperty
    private Integer value;

    @JsonProperty
    private String message;

    private CommonErrors(final Integer value, final String message) {
        this.value = value;
        this.message = message;
    }

    public Integer value() { return value; }
    public String message() { return message; }
}
