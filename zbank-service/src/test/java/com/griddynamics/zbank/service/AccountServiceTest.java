package com.griddynamics.zbank.service;

import com.griddynamics.zbank.data.entity.Account;
import com.griddynamics.zbank.data.repository.AccountRepository;
import com.griddynamics.zbank.service.dto.AccountDto;
import com.griddynamics.zbank.service.dto.TransactionRequest;
import com.griddynamics.zbank.service.exception.AccountNotFoundException;
import com.griddynamics.zbank.service.exception.InsufficientFundsException;
import com.griddynamics.zbank.service.exception.InvalidPinException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AccountService.
 * Tests business logic that was in COBOL ZBANK program.
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = Account.builder()
                .id(1L)
                .accountNumber("1234567890")
                .pinHash("1234")
                .balance(new BigDecimal("1000.00"))
                .accountHolderName("Test User")
                .status(Account.AccountStatus.ACTIVE)
                .build();
    }

    @Test
    void authenticateAccount_Success() {
        // Given
        when(accountRepository.findActiveAccountByNumber("1234567890"))
                .thenReturn(Optional.of(testAccount));

        // When
        AccountDto result = accountService.authenticateAccount("1234567890", "1234");

        // Then
        assertNotNull(result);
        assertEquals("1234567890", result.getAccountNumber());
        assertEquals(new BigDecimal("1000.00"), result.getBalance());
        verify(accountRepository, times(1)).findActiveAccountByNumber("1234567890");
    }

    @Test
    void authenticateAccount_InvalidPin() {
        // Given
        when(accountRepository.findActiveAccountByNumber("1234567890"))
                .thenReturn(Optional.of(testAccount));

        // When & Then
        assertThrows(InvalidPinException.class, () -> 
                accountService.authenticateAccount("1234567890", "wrong"));
    }

    @Test
    void authenticateAccount_AccountNotFound() {
        // Given
        when(accountRepository.findActiveAccountByNumber("9999999999"))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(AccountNotFoundException.class, () -> 
                accountService.authenticateAccount("9999999999", "1234"));
    }

    @Test
    void deposit_Success() {
        // Given
        TransactionRequest request = TransactionRequest.builder()
                .accountNumber("1234567890")
                .pin("1234")
                .amount(new BigDecimal("500.00"))
                .type(TransactionRequest.TransactionType.DEPOSIT)
                .build();

        when(accountRepository.findActiveAccountByNumber("1234567890"))
                .thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        AccountDto result = accountService.deposit(request);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("1500.00"), result.getBalance());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void withdraw_Success() {
        // Given
        TransactionRequest request = TransactionRequest.builder()
                .accountNumber("1234567890")
                .pin("1234")
                .amount(new BigDecimal("300.00"))
                .type(TransactionRequest.TransactionType.WITHDRAWAL)
                .build();

        when(accountRepository.findActiveAccountByNumber("1234567890"))
                .thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        AccountDto result = accountService.withdraw(request);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("700.00"), result.getBalance());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void withdraw_InsufficientFunds() {
        // Given
        TransactionRequest request = TransactionRequest.builder()
                .accountNumber("1234567890")
                .pin("1234")
                .amount(new BigDecimal("2000.00"))
                .type(TransactionRequest.TransactionType.WITHDRAWAL)
                .build();

        when(accountRepository.findActiveAccountByNumber("1234567890"))
                .thenReturn(Optional.of(testAccount));

        // When & Then
        assertThrows(InsufficientFundsException.class, () -> 
                accountService.withdraw(request));
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void getBalance_Success() {
        // Given
        when(accountRepository.findActiveAccountByNumber("1234567890"))
                .thenReturn(Optional.of(testAccount));

        // When
        AccountDto result = accountService.getBalance("1234567890");

        // Then
        assertNotNull(result);
        assertEquals("1234567890", result.getAccountNumber());
        assertEquals(new BigDecimal("1000.00"), result.getBalance());
    }
}




