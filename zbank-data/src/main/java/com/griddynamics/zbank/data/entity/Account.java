package com.griddynamics.zbank.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Account entity - replaces VSAM KSDS record structure from mainframe.
 * Original COBOL structure: 30-byte fixed record with Account Number (10), PIN (10), Balance (10)
 */
@Entity
@Table(name = "accounts", indexes = {
    @Index(name = "idx_account_number", columnList = "account_number", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Account number - corresponds to COBOL ACC-NO (PIC X(10))
     * Maximum 10 characters to match mainframe constraint
     */
    @NotNull
    @Size(min = 10, max = 10)
    @Column(name = "account_number", length = 10, nullable = false, unique = true)
    private String accountNumber;

    /**
     * PIN for account authentication - corresponds to COBOL ACC-PIN (PIC X(10))
     * Stored as hash in production (plain text in COBOL original)
     */
    @NotNull
    @Size(min = 4, max = 255)
    @Column(name = "pin_hash", nullable = false)
    private String pinHash;

    /**
     * Account balance - corresponds to COBOL ACC-BAL (PIC 9(10))
     * Using BigDecimal for precision (COBOL used integer cents)
     */
    @NotNull
    @Column(name = "balance", precision = 19, scale = 2, nullable = false)
    private BigDecimal balance;

    /**
     * Account holder name - additional field not in original COBOL
     */
    @Size(max = 100)
    @Column(name = "account_holder_name", length = 100)
    private String accountHolderName;

    /**
     * Account status - additional field for modern banking requirements
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @Builder.Default
    private AccountStatus status = AccountStatus.ACTIVE;

    /**
     * Audit fields - not present in original COBOL system
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum AccountStatus {
        ACTIVE,
        LOCKED,
        CLOSED
    }
}




