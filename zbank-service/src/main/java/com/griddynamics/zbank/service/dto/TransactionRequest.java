package com.griddynamics.zbank.service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for transaction requests (deposit/withdrawal).
 * Corresponds to BMS screen input from COBOL application.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequest {

    @NotNull(message = "Account number is required")
    @Size(min = 10, max = 10, message = "Account number must be exactly 10 characters")
    private String accountNumber;

    @NotNull(message = "PIN is required")
    @Size(min = 4, max = 10, message = "PIN must be between 4 and 10 characters")
    private String pin;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    private TransactionType type;

    public enum TransactionType {
        DEPOSIT,
        WITHDRAWAL,
        BALANCE_INQUIRY
    }
}




