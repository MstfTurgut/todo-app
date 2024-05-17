package com.mstftrgt.todoapp.exception;

public class ListAlreadyExistsException extends RuntimeException{
    public ListAlreadyExistsException(String message) {
        super(message);
    }
}
