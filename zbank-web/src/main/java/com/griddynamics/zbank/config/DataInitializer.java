package com.griddynamics.zbank.config;

import com.griddynamics.zbank.data.entity.Account;
import com.griddynamics.zbank.data.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;

/**
 * Initialize sample data for development/testing.
 * Corresponds to initial data load from SEQDAT.ZBANK.cbl file in COBOL system.
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class DataInitializer {

    @Bean
    @Profile("!prod")
    public CommandLineRunner initializeData(AccountRepository accountRepository) {
        return args -> {
            log.info("Initializing sample data for development...");
            
            // Create sample accounts matching original COBOL test data
            Account account1 = Account.builder()
                    .accountNumber("1234567890")
                    .pinHash("1234")  // TODO: Hash in production
                    .balance(new BigDecimal("1000.00"))
                    .accountHolderName("John Doe")
                    .status(Account.AccountStatus.ACTIVE)
                    .build();
            
            Account account2 = Account.builder()
                    .accountNumber("9876543210")
                    .pinHash("5678")  // TODO: Hash in production
                    .balance(new BigDecimal("5000.50"))
                    .accountHolderName("Jane Smith")
                    .status(Account.AccountStatus.ACTIVE)
                    .build();
            
            Account account3 = Account.builder()
                    .accountNumber("5555555555")
                    .pinHash("0000")  // TODO: Hash in production
                    .balance(new BigDecimal("250.75"))
                    .accountHolderName("Bob Johnson")
                    .status(Account.AccountStatus.ACTIVE)
                    .build();
            
            accountRepository.save(account1);
            accountRepository.save(account2);
            accountRepository.save(account3);
            
            log.info("Sample data initialized successfully. {} accounts created.", 
                    accountRepository.count());
        };
    }
}




