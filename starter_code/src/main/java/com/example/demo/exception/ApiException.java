package com.example.demo.exception;

import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

public class ApiException {
    private final HttpStatus httpStatus;
    private final String message;
    private final ZonedDateTime timestamp;

    public ApiException(HttpStatus httpStatus, String message, ZonedDateTime timestamp) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.timestamp = timestamp;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

}
