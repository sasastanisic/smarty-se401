package com.smarty.infrastructure.exception;

import org.springframework.http.HttpStatus;

public record ApiErrorResponse(

        String message,
        HttpStatus status,
        String timestamp

) { }
