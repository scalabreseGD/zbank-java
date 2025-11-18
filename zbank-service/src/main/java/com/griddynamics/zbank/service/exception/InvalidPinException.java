package com.griddynamics.zbank.service.exception;

/**
 * Exception thrown when PIN authentication fails.
 * Corresponds to COBOL LOGIN-PROCESS validation failure.
 */
public class InvalidPinException extends RuntimeException {
    
    public InvalidPinException(String message) {
        super(message);
    }
}




