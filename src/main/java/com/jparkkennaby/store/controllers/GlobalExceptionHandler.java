package com.jparkkennaby.store.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.jparkkennaby.store.dtos.ErrorDto;

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
}
