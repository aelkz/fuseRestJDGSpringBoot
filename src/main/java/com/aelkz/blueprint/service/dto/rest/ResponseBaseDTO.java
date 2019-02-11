package com.aelkz.blueprint.service.dto.rest;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ResponseBaseDTO implements Serializable {

    private LocalDateTime timestamp;

    private int httpStatus;

    private String message;

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
