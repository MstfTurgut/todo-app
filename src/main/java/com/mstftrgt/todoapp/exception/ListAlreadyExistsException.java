package com.mstftrgt.todoapp.exception;

public class ListAlreadyExistsException extends RuntimeException{
    public ListAlreadyExistsException(String listName) {
        super("List already exists by name : " + listName);
    }
}
