package com.mstftrgt.todoapp.exception;

public class CannotMarkItemCompletedBeforeTheDependentItemException extends RuntimeException {

    public CannotMarkItemCompletedBeforeTheDependentItemException(String message) {
        super(message);
    }
}
