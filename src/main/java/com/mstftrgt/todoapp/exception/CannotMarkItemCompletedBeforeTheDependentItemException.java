package com.mstftrgt.todoapp.exception;

public class CannotMarkItemCompletedBeforeTheDependentItemException extends RuntimeException {

    public CannotMarkItemCompletedBeforeTheDependentItemException() {
        super("This item has dependency to other items, mark them first.");
    }
}
