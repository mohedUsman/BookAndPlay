package com.booknplay.userservice.exception;

public class ConflictException extends RuntimeException { //used for 409 conflict
    public ConflictException(String message) {
        super(message);
    }
}