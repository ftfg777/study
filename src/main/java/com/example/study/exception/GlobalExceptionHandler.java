package com.example.study.exception;

import com.example.study.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CommonExceptionHandler.class)
    public ResponseEntity<ApiResponse<Void>> handleUserCommonException(CommonExceptionHandler ex){
        return ResponseEntity.ok(ApiResponse.error(ex.getErrorCode()));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException() {
        return ResponseEntity.ok(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }

}
