package com.mstftrgt.todoapp.exception;

public class DependentItemNotFoundException extends RuntimeException{

    public DependentItemNotFoundException(String message) {
        super(message);
    }
}
