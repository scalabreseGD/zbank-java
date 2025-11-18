package com.griddynamics.zbank.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.griddynamics.zbank.service.AccountService;
import com.griddynamics.zbank.service.dto.AccountDto;
import com.griddynamics.zbank.service.dto.TransactionRequest;
import com.griddynamics.zbank.service.exception.AccountNotFoundException;
import com.griddynamics.zbank.service.exception.InvalidPinException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for AccountController.
 * Tests REST API endpoints that replace BMS screens.
 */
@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    @Test
    void authenticate_Success() throws Exception {
        // Given
        Map<String, String> credentials = new HashMap<>();
        credentials.put("accountNumber", "1234567890");
        credentials.put("pin", "1234");

        AccountDto accountDto = AccountDto.builder()
                .accountNumber("1234567890")
                .balance(new BigDecimal("1000.00"))
                .accountHolderName("Test User")
                .status("ACTIVE")
                .build();

        when(accountService.authenticateAccount("1234567890", "1234"))
                .thenReturn(accountDto);

        // When & Then
        mockMvc.perform(post("/api/accounts/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("1234567890"))
                .andExpect(jsonPath("$.balance").value(1000.00));
    }

    @Test
    void authenticate_InvalidPin() throws Exception {
        // Given
        Map<String, String> credentials = new HashMap<>();
        credentials.put("accountNumber", "1234567890");
        credentials.put("pin", "wrong");

        when(accountService.authenticateAccount("1234567890", "wrong"))
                .thenThrow(new InvalidPinException("Invalid PIN"));

        // When & Then
        mockMvc.perform(post("/api/accounts/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getBalance_Success() throws Exception {
        // Given
        AccountDto accountDto = AccountDto.builder()
                .accountNumber("1234567890")
                .balance(new BigDecimal("1000.00"))
                .build();

        when(accountService.getBalance("1234567890")).thenReturn(accountDto);

        // When & Then
        mockMvc.perform(get("/api/accounts/1234567890/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("1234567890"))
                .andExpect(jsonPath("$.balance").value(1000.00));
    }

    @Test
    void deposit_Success() throws Exception {
        // Given
        TransactionRequest request = TransactionRequest.builder()
                .accountNumber("1234567890")
                .pin("1234")
                .amount(new BigDecimal("500.00"))
                .type(TransactionRequest.TransactionType.DEPOSIT)
                .build();

        AccountDto accountDto = AccountDto.builder()
                .accountNumber("1234567890")
                .balance(new BigDecimal("1500.00"))
                .build();

        when(accountService.deposit(any(TransactionRequest.class))).thenReturn(accountDto);

        // When & Then
        mockMvc.perform(post("/api/accounts/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1500.00));
    }

    @Test
    void withdraw_Success() throws Exception {
        // Given
        TransactionRequest request = TransactionRequest.builder()
                .accountNumber("1234567890")
                .pin("1234")
                .amount(new BigDecimal("300.00"))
                .type(TransactionRequest.TransactionType.WITHDRAWAL)
                .build();

        AccountDto accountDto = AccountDto.builder()
                .accountNumber("1234567890")
                .balance(new BigDecimal("700.00"))
                .build();

        when(accountService.withdraw(any(TransactionRequest.class))).thenReturn(accountDto);

        // When & Then
        mockMvc.perform(post("/api/accounts/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(700.00));
    }
}




