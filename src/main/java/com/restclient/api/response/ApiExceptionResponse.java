package com.restclient.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiExceptionResponse {

    @Builder.Default
    private boolean result = false;
    private int code;
    private String message;
    private Object error;

    public static ResponseEntity<ApiExceptionResponse> exception(HttpStatus status) {
        return exception(status, status.value(), status.getReasonPhrase(), null);
    }

    public static ResponseEntity<ApiExceptionResponse> exception(HttpStatus status, String message) {
        return exception(status, status.value(), message, null);
    }

    public static ResponseEntity<ApiExceptionResponse> exception(HttpStatus status, String message, Object error) {
        return exception(status, status.value(), message, error);
    }

    public static ResponseEntity<ApiExceptionResponse> exception(HttpStatus status, int code, String message) {
        return exception(status, code, message, null);
    }

    private static ResponseEntity<ApiExceptionResponse> exception(HttpStatus status, int code, String message, Object error) {
        return ResponseEntity.status(status).body(
                ApiExceptionResponse.builder()
                        .code(code)
                        .message(message)
                        .error(error)
                        .build()
        );
    }
}
