package com.griddynamics.zbank.data.repository;

import com.griddynamics.zbank.data.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Account entity - replaces VSAM KSDS operations.
 * 
 * COBOL VSAM operations mapped to JPA:
 * - READ (sequential/random) -> findAll(), findById(), findByAccountNumber()
 * - WRITE -> save()
 * - REWRITE -> save()
 * - DELETE -> delete()
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Find account by account number (COBOL: READ VSAM with key)
     * 
     * @param accountNumber the 10-character account number
     * @return Optional containing the account if found
     */
    Optional<Account> findByAccountNumber(String accountNumber);

    /**
     * Find account by account number and verify it's active
     * 
     * @param accountNumber the account number
     * @param status the account status
     * @return Optional containing the account if found and active
     */
    Optional<Account> findByAccountNumberAndStatus(String accountNumber, Account.AccountStatus status);

    /**
     * Check if account number exists (for validation)
     * 
     * @param accountNumber the account number to check
     * @return true if account exists
     */
    boolean existsByAccountNumber(String accountNumber);

    /**
     * Custom query to find account with balance check
     * Useful for overdraft protection logic from COBOL
     * 
     * @param accountNumber the account number
     * @return Optional containing the account
     */
    @Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber AND a.status = 'ACTIVE'")
    Optional<Account> findActiveAccountByNumber(String accountNumber);
}




