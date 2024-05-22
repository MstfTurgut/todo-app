package com.mstftrgt.todoapp.exception;

public class ListNotFoundException extends RuntimeException {
    public ListNotFoundException(String listId) {
        super("List not found for id : " + listId);
    }
}