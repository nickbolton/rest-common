package com.tubebreakup.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class ExceptionResponse {
	@JsonProperty
	private ExceptionError error;
	protected ExceptionResponse(ExceptionError error) {
		super();
		this.error = error;
	}
	public ExceptionError getError() {
		return error;
	}
	public void setError(ExceptionError error) {
		this.error = error;
	}
	protected ExceptionResponse() {
		super();
	}	
	
	@JsonIgnore
	public String getResponseBody() {
		ObjectMapper mapper = new ObjectMapper();
		String body;
		try {
			body = mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			body = "";
		}
		return body;
	}
}
