package com.griddynamics.zbank.service.exception;

/**
 * Exception thrown when an account is not found.
 * Corresponds to COBOL INVALID KEY condition in VSAM operations.
 */
public class AccountNotFoundException extends RuntimeException {
    
    public AccountNotFoundException(String message) {
        super(message);
    }
}




