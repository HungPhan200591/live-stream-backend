package com.stream.demo.service;

import com.stream.demo.model.dto.WalletDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
public class WalletService {

    public WalletDTO deposit(Long userId, BigDecimal amount, String description) {
        log.info("Depositing {} to userId: {}. Description: {}", amount, userId, description);
        return WalletDTO.builder()
                .userId(userId)
                .balance(amount) // In simulation, we just return the deposited amount as balance
                .currency("COINS")
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
