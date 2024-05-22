package com.mstftrgt.todoapp.exception;

public class DependencyLoopException extends RuntimeException{

    public DependencyLoopException() {
        super("Cannot create dependency, this dependency causing a dependency loop.");
    }
}
