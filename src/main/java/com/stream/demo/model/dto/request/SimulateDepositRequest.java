package com.stream.demo.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SimulateDepositRequest {
    @NotNull
    @Schema(description = "User ID", example = "1")
    private Long userId;

    @NotNull
    @Positive
    @Schema(description = "Amount to deposit", example = "1000.00")
    private BigDecimal amount;
}
