package com.mstftrgt.todoapp.exception;

public class UsernameAlreadyInUseException extends RuntimeException {

    public UsernameAlreadyInUseException(String message) {
        super(message);
    }
}
