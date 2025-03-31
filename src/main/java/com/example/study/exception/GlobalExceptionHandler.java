package com.example.study.exception;

import com.example.study.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
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


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorResponse response = ErrorResponse.builder()
            .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus().value())
            .code(ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            .message(ErrorCode.INTERNAL_SERVER_ERROR.getMessage())
            .build();

        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus()).body(response);
    }

}
