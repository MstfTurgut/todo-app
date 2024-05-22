package com.mstftrgt.todoapp.exception;

public class ItemNotFoundException extends RuntimeException{
    public ItemNotFoundException(String itemId) {
        super("Item not found by id : " + itemId);
    }
}
