package com.griddynamics.zbank.service.exception;

/**
 * Exception thrown when withdrawal amount exceeds available balance.
 * Corresponds to COBOL WITHDRAWAL-PROCESS balance check failure.
 */
public class InsufficientFundsException extends RuntimeException {
    
    public InsufficientFundsException(String message) {
        super(message);
    }
}




