package com.jparkkennaby.store.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.jparkkennaby.store.exceptions.MaxTableRecordLimitReached;

@ControllerAdvice
public class GlobalExceptionHandler {

    // handles invalid request body (was returning 401 Unauthorized)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDto> handleUnreadableMessage() {
        return ResponseEntity.badRequest().body(
                new ErrorDto("Invalid request body"));
    }

    // overrides the default exception handler
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException exception) {

        var errors = new HashMap<String, String>();

        exception.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        // returns a map of fields with errors and the reason for the error
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(MaxTableRecordLimitReached.class)
    public ResponseEntity<ErrorDto> handleTableRecordLimitReached(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body((new ErrorDto(ex.getMessage())));
    }
}
