package com.griddynamics.zbank.service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Account.
 * Used for communication between service and web layers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDto {

    private Long id;

    @NotNull(message = "Account number is required")
    @Size(min = 10, max = 10, message = "Account number must be exactly 10 characters")
    private String accountNumber;

    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Balance cannot be negative")
    private BigDecimal balance;

    private String accountHolderName;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}




