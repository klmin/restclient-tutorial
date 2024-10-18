package com.restclient.api.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ApiRuntimeException extends RuntimeException {

	private static final HttpStatus DEFAULT_FAIL_STATUS = HttpStatus.BAD_REQUEST;

	private final HttpStatus status;
	private final int httpCode;

	public ApiRuntimeException(String message) {
		this(DEFAULT_FAIL_STATUS, DEFAULT_FAIL_STATUS.value(), message);
	}

	public ApiRuntimeException(HttpStatus status, String message) {
		this(status, status.value(), message);
	}

	public ApiRuntimeException(HttpStatus status, int httpCode, String message) {
		super(message);
		this.status = status;
		this.httpCode = httpCode;
	}

}
