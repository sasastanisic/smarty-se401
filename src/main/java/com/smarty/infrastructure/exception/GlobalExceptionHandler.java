package com.smarty.infrastructure.exception;

import com.smarty.infrastructure.exception.exceptions.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String TIMESTAMP = ZonedDateTime.now(ZoneOffset.of("+01:00")).toString();

    @ExceptionHandler
    public ResponseEntity<Object> handleMethodArgumentException(MethodArgumentNotValidException e) {
        Map<String, Object> errors = new HashMap<>();
        HttpStatus status = HttpStatus.BAD_REQUEST;

        e.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        errors.put("status", status);
        errors.put("timestamp", TIMESTAMP);

        return new ResponseEntity<>(errors, status);
    }

    @ExceptionHandler(value = {BaseException.class})
    public ResponseEntity<ApiErrorResponse> handleApiRequestException(BaseException e) {
        var status = e.getClass()
                .getAnnotation(ResponseStatus.class)
                .value();

        ApiErrorResponse response = new ApiErrorResponse(e.getMessage(), status, TIMESTAMP);

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(value = {SQLIntegrityConstraintViolationException.class})
    public ResponseEntity<ApiErrorResponse> handleSqlConstraintException() {
        HttpStatus status = HttpStatus.FORBIDDEN;
        ApiErrorResponse response = new ApiErrorResponse("Parent row can't be deleted or updated", status, TIMESTAMP);

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ApiErrorResponse> handleRequestNotSupportedException(Exception e) {
        HttpStatus status = HttpStatus.METHOD_NOT_ALLOWED;
        ApiErrorResponse response = new ApiErrorResponse(e.getMessage(), status, TIMESTAMP);

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiErrorResponse> handleArgumentTypeMismatchException() {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiErrorResponse response = new ApiErrorResponse("The provided path argument is not a valid type", status, TIMESTAMP);

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(value = {MissingServletRequestParameterException.class})
    public ResponseEntity<ApiErrorResponse> handleMissingParameterException(Exception e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiErrorResponse response = new ApiErrorResponse(e.getMessage(), status, TIMESTAMP);

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    public ResponseEntity<ApiErrorResponse> handleInvalidTypeException() {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiErrorResponse response = new ApiErrorResponse("Entered value is not a valid type", status, TIMESTAMP);

        return new ResponseEntity<>(response, status);
    }

}
