package com.mstftrgt.todoapp.exception;

public class UsernameAlreadyInUseException extends RuntimeException {

    public UsernameAlreadyInUseException() {
        super("This username is taken.");
    }
}
