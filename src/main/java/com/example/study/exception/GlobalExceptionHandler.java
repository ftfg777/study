package com.example.study.exception;

import com.example.study.dto.ErrorResponse;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CommonExceptionHandler.class)
    public ResponseEntity<ErrorResponse> handleUserCommonException(CommonExceptionHandler ex){
        ErrorResponse response = new ErrorResponse(
            ex.getErrorCode().getCode(),
            ex.getErrorCode().getMessage(),
            ex.getErrorCode().getStatus().value()
        );

        return ResponseEntity.status(ex.getErrorCode().getStatus()).body(response);
    }


    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String,String>> handleMethodNotAllowed() {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("code", "METHOD_NOT_ALLOWED");
        errorResponse.put("message", "잘못된 접근입니다.");

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
    }

}
