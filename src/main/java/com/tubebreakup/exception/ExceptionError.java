package com.tubebreakup.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

@JsonSerialize
public class ExceptionError {
	@JsonProperty private Integer status;
	@JsonProperty private Integer errorCode;
	@JsonProperty private Date timestamp;
	@JsonProperty private String message;
	@JsonProperty private String detail;
	public ExceptionError(Integer status, Integer errorCode, Date timestamp, String message, String detail) {
		super();
		this.status = status;
		this.errorCode = errorCode;
		this.timestamp = timestamp;
		this.message = message;
		this.detail = detail;
	}
	protected ExceptionError() {
		super();
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public String getMessage() {
		return message;
	}
	public String getDetail() {
		return detail;
	}
	public Integer getStatus() {
		return status;
	}
	public Integer getErrorCode() {
		return errorCode;
	}
}
