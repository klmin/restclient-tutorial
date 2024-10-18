package com.restclient.api.exception.handler;


import com.restclient.api.exception.ApiRuntimeException;
import com.restclient.api.response.ApiExceptionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.security.GeneralSecurityException;
import java.security.SignatureException;
import java.util.List;
import java.util.Map;
import java.util.Objects;



@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ApiExceptionHandler {

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiExceptionResponse> httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException e) {
        log.error("## httpRequestMethodNotSupportedExceptionHandler ##");
        this.log(e);
        return ApiExceptionResponse.exception(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiExceptionResponse> httpMediaTypeNotSupportedExceptionHandler(HttpMediaTypeNotSupportedException e) {
        log.error("## httpMediaTypeNotSupportedExceptionHandler ##");
        this.log(e);
        return ApiExceptionResponse.exception(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiExceptionResponse> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        log.error("## methodArgumentNotValidExceptionHandler ##");
        this.log(e);

        List<Map<String, String>> list = e.getFieldErrors()
                .stream()
                .map(err -> Map.of(
                                "field", err.getField(),
                                "msg", Objects.requireNonNull(err.getDefaultMessage())
                        )
                )
                .toList();

        return ApiExceptionResponse.exception(HttpStatus.BAD_REQUEST, "필수값이 누락되었습니다.", list);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiExceptionResponse> httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException e) {
        log.error("## httpMessageNotReadableExceptionHandler ##");
        this.log(e);
        return ApiExceptionResponse.exception(HttpStatus.BAD_REQUEST, "필수값이 누락되었습니다.");
    }

    @ExceptionHandler(ApiRuntimeException.class)
    public ResponseEntity<ApiExceptionResponse> apiExceptionHandler(ApiRuntimeException e) {
        log.error("## apiExceptionHandler ##");
        this.log(e);
        return ApiExceptionResponse.exception(e.getStatus(), e.getHttpCode(), e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiExceptionResponse> accessDeniedExceptionHandler(AccessDeniedException e) {
        log.error("## accessDeniedExceptionHandler ##");
        this.log(e);
        return ApiExceptionResponse.exception(HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiExceptionResponse> exceptionHandler(Exception e) {
        log.error("## exceptionHandler ##");
        this.log(e);
        return ApiExceptionResponse.exception(HttpStatus.BAD_REQUEST);
    }

    private void log(Exception e){
        log.error("Exception Message : {}", e.getMessage());
    }
}
