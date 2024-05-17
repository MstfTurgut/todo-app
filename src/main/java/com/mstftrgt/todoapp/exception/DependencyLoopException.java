package com.mstftrgt.todoapp.exception;

public class DependencyLoopException extends RuntimeException{

    public DependencyLoopException(String message) {
        super(message);
    }
}
