package com.mstftrgt.todoapp.exception;

public class DependentItemNotFoundException extends RuntimeException{

    public DependentItemNotFoundException() {
        super("Dependent item not found.");
    }
}
