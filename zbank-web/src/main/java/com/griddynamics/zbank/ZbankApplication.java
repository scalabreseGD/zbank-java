package com.griddynamics.zbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main Spring Boot application class for zBANK.
 * 
 * This is the modern Java replacement for the COBOL/CICS mainframe application.
 * 
 * Architecture mapping:
 * - COBOL ZBANK program -> @SpringBootApplication
 * - CICS transaction (ZBNK) -> REST endpoints
 * - BMS screens -> REST API clients (web/mobile)
 * - VSAM files -> JPA/H2/PostgreSQL database
 */
@SpringBootApplication(scanBasePackages = "com.griddynamics.zbank")
@EnableJpaRepositories(basePackages = "com.griddynamics.zbank.data.repository")
@EntityScan(basePackages = "com.griddynamics.zbank.data.entity")
public class ZbankApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZbankApplication.class, args);
    }
}




