package com.griddynamics.zbank.service;

import com.griddynamics.zbank.data.entity.Account;
import com.griddynamics.zbank.data.repository.AccountRepository;
import com.griddynamics.zbank.service.dto.AccountDto;
import com.griddynamics.zbank.service.dto.TransactionRequest;
import com.griddynamics.zbank.service.exception.AccountNotFoundException;
import com.griddynamics.zbank.service.exception.InsufficientFundsException;
import com.griddynamics.zbank.service.exception.InvalidPinException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Account service - implements business logic from COBOL ZBANK program.
 * 
 * COBOL procedures mapped to service methods:
 * - LOGIN-PROCESS -> authenticateAccount()
 * - BALANCE-INQUIRY -> getBalance()
 * - DEPOSIT-PROCESS -> deposit()
 * - WITHDRAWAL-PROCESS -> withdraw()
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;

    /**
     * Authenticate account with PIN (COBOL: LOGIN-PROCESS)
     * 
     * @param accountNumber the account number
     * @param pin the PIN to verify
     * @return the account DTO if authentication successful
     * @throws AccountNotFoundException if account not found
     * @throws InvalidPinException if PIN is incorrect
     */
    public AccountDto authenticateAccount(String accountNumber, String pin) {
        log.info("Authenticating account: {}", accountNumber);
        
        Account account = accountRepository.findActiveAccountByNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
        
        // TODO: In production, use BCrypt or similar for PIN verification
        if (!account.getPinHash().equals(pin)) {
            log.warn("Invalid PIN attempt for account: {}", accountNumber);
            throw new InvalidPinException("Invalid PIN");
        }
        
        return mapToDto(account);
    }

    /**
     * Get account balance (COBOL: BALANCE-INQUIRY)
     * 
     * @param accountNumber the account number
     * @return the account with balance information
     * @throws AccountNotFoundException if account not found
     */
    @Transactional(readOnly = true)
    public AccountDto getBalance(String accountNumber) {
        log.info("Getting balance for account: {}", accountNumber);
        
        Account account = accountRepository.findActiveAccountByNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
        
        return mapToDto(account);
    }

    /**
     * Deposit funds into account (COBOL: DEPOSIT-PROCESS)
     * 
     * @param request the transaction request
     * @return updated account DTO
     * @throws AccountNotFoundException if account not found
     * @throws InvalidPinException if PIN is incorrect
     */
    public AccountDto deposit(TransactionRequest request) {
        log.info("Processing deposit for account: {}, amount: {}", 
                request.getAccountNumber(), request.getAmount());
        
        Account account = accountRepository.findActiveAccountByNumber(request.getAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + request.getAccountNumber()));
        
        // Verify PIN
        if (!account.getPinHash().equals(request.getPin())) {
            throw new InvalidPinException("Invalid PIN");
        }
        
        // Perform deposit
        BigDecimal newBalance = account.getBalance().add(request.getAmount());
        account.setBalance(newBalance);
        
        Account savedAccount = accountRepository.save(account);
        log.info("Deposit successful. New balance: {}", newBalance);
        
        return mapToDto(savedAccount);
    }

    /**
     * Withdraw funds from account (COBOL: WITHDRAWAL-PROCESS)
     * 
     * @param request the transaction request
     * @return updated account DTO
     * @throws AccountNotFoundException if account not found
     * @throws InvalidPinException if PIN is incorrect
     * @throws InsufficientFundsException if balance is insufficient
     */
    public AccountDto withdraw(TransactionRequest request) {
        log.info("Processing withdrawal for account: {}, amount: {}", 
                request.getAccountNumber(), request.getAmount());
        
        Account account = accountRepository.findActiveAccountByNumber(request.getAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + request.getAccountNumber()));
        
        // Verify PIN
        if (!account.getPinHash().equals(request.getPin())) {
            throw new InvalidPinException("Invalid PIN");
        }
        
        // Check sufficient funds
        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            log.warn("Insufficient funds for withdrawal. Balance: {}, Requested: {}", 
                    account.getBalance(), request.getAmount());
            throw new InsufficientFundsException("Insufficient funds");
        }
        
        // Perform withdrawal
        BigDecimal newBalance = account.getBalance().subtract(request.getAmount());
        account.setBalance(newBalance);
        
        Account savedAccount = accountRepository.save(account);
        log.info("Withdrawal successful. New balance: {}", newBalance);
        
        return mapToDto(savedAccount);
    }

    /**
     * Get all accounts (for admin purposes)
     * 
     * @return list of all accounts
     */
    @Transactional(readOnly = true)
    public List<AccountDto> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Create new account
     * 
     * @param accountDto the account data
     * @return created account DTO
     */
    public AccountDto createAccount(AccountDto accountDto) {
        log.info("Creating new account: {}", accountDto.getAccountNumber());
        
        if (accountRepository.existsByAccountNumber(accountDto.getAccountNumber())) {
            throw new IllegalArgumentException("Account already exists: " + accountDto.getAccountNumber());
        }
        
        Account account = Account.builder()
                .accountNumber(accountDto.getAccountNumber())
                .pinHash(accountDto.getAccountHolderName()) // TODO: Set proper PIN hash
                .balance(accountDto.getBalance() != null ? accountDto.getBalance() : BigDecimal.ZERO)
                .accountHolderName(accountDto.getAccountHolderName())
                .status(Account.AccountStatus.ACTIVE)
                .build();
        
        Account savedAccount = accountRepository.save(account);
        return mapToDto(savedAccount);
    }

    /**
     * Map Account entity to DTO
     */
    private AccountDto mapToDto(Account account) {
        return AccountDto.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .accountHolderName(account.getAccountHolderName())
                .status(account.getStatus().name())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
}




