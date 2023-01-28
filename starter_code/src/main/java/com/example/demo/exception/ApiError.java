package com.example.demo.exception;

import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

public class ApiError {
    private final HttpStatus status;
    private final String message;
    private final ZonedDateTime timestamp;

    public ApiError(HttpStatus status, String message, ZonedDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }
}
