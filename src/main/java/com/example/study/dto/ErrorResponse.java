package com.example.study.dto;

import java.time.LocalDateTime;

public class ErrorResponse {
    private String code;
    private String message;
    private String timestamp;
    private int status;

    public ErrorResponse(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now().toString();
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }
}
