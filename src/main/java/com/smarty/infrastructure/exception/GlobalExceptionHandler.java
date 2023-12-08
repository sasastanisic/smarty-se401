package com.smarty.infrastructure.exception;

import com.smarty.infrastructure.exception.exceptions.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Object> handleMethodArgumentException(MethodArgumentNotValidException e) {
        Map<String, Object> errors = new HashMap<>();
        HttpStatus status = HttpStatus.BAD_REQUEST;

        e.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        errors.put("status", status);
        errors.put("timestamp", ZonedDateTime.now());

        return new ResponseEntity<>(errors, status);
    }

    @ExceptionHandler(value = {BaseException.class})
    public ResponseEntity<ApiErrorResponse> handleApiRequestException(BaseException e) {
        var status = e.getClass()
                .getAnnotation(ResponseStatus.class)
                .value();

        ApiErrorResponse response = new ApiErrorResponse(e.getMessage(), status, ZonedDateTime.now());

        return new ResponseEntity<>(response, status);
    }

}
