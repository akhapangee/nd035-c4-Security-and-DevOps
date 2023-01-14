package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ApiRequestException.class)
    public ResponseEntity<Object> handeApiException(ApiRequestException e) {
        ApiException apiException = new ApiException(
                HttpStatus.BAD_REQUEST,
                e.getMessage(),
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(apiException, apiException.getHttpStatus());
    }
}
