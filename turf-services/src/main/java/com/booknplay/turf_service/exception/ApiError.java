package com.booknplay.turf_service.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ApiError { // CHANGE
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}