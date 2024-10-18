package com.restclient.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private static final HttpStatus DEFAULT_SUCCESS_STATUS = HttpStatus.OK;

    private boolean result;
    private int code;
    private String message;
    private T data;

    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return create(true, null, DEFAULT_SUCCESS_STATUS, data);
    }

    public static <T> ResponseEntity<ApiResponse<T>> create(boolean result, String message, HttpStatus status, T data) {
        return ResponseEntity.status(status).body(
                ApiResponse.<T>builder()
                        .result(result)
                        .code(status.value())
                        .message(Optional.ofNullable(message).orElse(status.getReasonPhrase()))
                        .data(data)
                        .build()
        );
    }

}
