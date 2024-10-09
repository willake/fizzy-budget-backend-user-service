package com.huiun.fizzybudget.userservice.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
        super("User already exists");
    }

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
