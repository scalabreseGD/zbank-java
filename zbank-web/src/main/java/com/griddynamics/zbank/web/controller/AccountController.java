package com.griddynamics.zbank.web.controller;

import com.griddynamics.zbank.service.AccountService;
import com.griddynamics.zbank.service.dto.AccountDto;
import com.griddynamics.zbank.service.dto.TransactionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for account operations.
 * Replaces BMS screen interactions from the mainframe system.
 * 
 * BMS Screen -> REST endpoint mapping:
 * - ZBNK Login screen -> POST /api/accounts/authenticate
 * - ZBNK Menu screen -> Multiple endpoints
 * - Balance inquiry -> GET /api/accounts/{accountNumber}/balance
 * - Deposit screen -> POST /api/accounts/deposit
 * - Withdrawal screen -> POST /api/accounts/withdraw
 */
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Account Operations", description = "Banking operations API - COBOL ZBANK migration")
public class AccountController {

    private final AccountService accountService;

    /**
     * Authenticate account with PIN (Login screen equivalent)
     * Corresponds to COBOL LOGIN-PROCESS
     */
    @PostMapping("/authenticate")
    @Operation(summary = "Authenticate account", description = "Verify account number and PIN")
    public ResponseEntity<AccountDto> authenticate(@RequestBody @Valid Map<String, String> credentials) {
        String accountNumber = credentials.get("accountNumber");
        String pin = credentials.get("pin");
        
        log.info("Authentication request for account: {}", accountNumber);
        AccountDto account = accountService.authenticateAccount(accountNumber, pin);
        return ResponseEntity.ok(account);
    }

    /**
     * Get account balance (Balance inquiry screen equivalent)
     * Corresponds to COBOL BALANCE-INQUIRY
     */
    @GetMapping("/{accountNumber}/balance")
    @Operation(summary = "Get account balance", description = "Retrieve current balance for account")
    public ResponseEntity<AccountDto> getBalance(@PathVariable String accountNumber) {
        log.info("Balance inquiry for account: {}", accountNumber);
        AccountDto account = accountService.getBalance(accountNumber);
        return ResponseEntity.ok(account);
    }

    /**
     * Deposit funds (Deposit screen equivalent)
     * Corresponds to COBOL DEPOSIT-PROCESS
     */
    @PostMapping("/deposit")
    @Operation(summary = "Deposit funds", description = "Add funds to account")
    public ResponseEntity<AccountDto> deposit(@RequestBody @Valid TransactionRequest request) {
        log.info("Deposit request for account: {}, amount: {}", 
                request.getAccountNumber(), request.getAmount());
        AccountDto account = accountService.deposit(request);
        return ResponseEntity.ok(account);
    }

    /**
     * Withdraw funds (Withdrawal screen equivalent)
     * Corresponds to COBOL WITHDRAWAL-PROCESS
     */
    @PostMapping("/withdraw")
    @Operation(summary = "Withdraw funds", description = "Remove funds from account")
    public ResponseEntity<AccountDto> withdraw(@RequestBody @Valid TransactionRequest request) {
        log.info("Withdrawal request for account: {}, amount: {}", 
                request.getAccountNumber(), request.getAmount());
        AccountDto account = accountService.withdraw(request);
        return ResponseEntity.ok(account);
    }

    /**
     * Get all accounts (Admin function - not in original COBOL)
     */
    @GetMapping
    @Operation(summary = "List all accounts", description = "Admin endpoint to list all accounts")
    public ResponseEntity<List<AccountDto>> getAllAccounts() {
        log.info("Retrieving all accounts");
        List<AccountDto> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    /**
     * Create new account (Admin function - not in original COBOL)
     */
    @PostMapping
    @Operation(summary = "Create account", description = "Create a new bank account")
    public ResponseEntity<AccountDto> createAccount(@RequestBody @Valid AccountDto accountDto) {
        log.info("Creating new account: {}", accountDto.getAccountNumber());
        AccountDto created = accountService.createAccount(accountDto);
        return ResponseEntity.ok(created);
    }
}




