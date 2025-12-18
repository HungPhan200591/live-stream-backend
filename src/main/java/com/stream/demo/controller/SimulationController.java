package com.stream.demo.controller;

import com.stream.demo.common.ApiResponse;
import com.stream.demo.model.dto.WalletDTO;
import com.stream.demo.model.dto.request.SimulateDepositRequest;
import com.stream.demo.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dev/simulate")
@RequiredArgsConstructor
@Tag(name = "Development Simulation", description = "Simulate external events for testing")
public class SimulationController {

    private final WalletService walletService;

    /**
     * Simulate payment deposit.
     */
    @PostMapping("/payment/deposit")
    @Operation(summary = "Simulate payment deposit")
    public ApiResponse<WalletDTO> simulateDeposit(
            @RequestBody @Valid SimulateDepositRequest request) {
        WalletDTO wallet = walletService.deposit(
                request.getUserId(),
                request.getAmount(),
                "Simulated deposit"
        );
        return ApiResponse.success(wallet, "Deposit successful (simulated)");
    }
}
