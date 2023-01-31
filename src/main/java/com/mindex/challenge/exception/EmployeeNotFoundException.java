package com.mindex.challenge.exception;

public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
