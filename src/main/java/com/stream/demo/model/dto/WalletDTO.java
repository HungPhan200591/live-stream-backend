package com.stream.demo.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class WalletDTO {
    private Long id;
    private Long userId;
    private BigDecimal balance;
    private String currency;
    private Long version;
    private LocalDateTime updatedAt;
}
