package com.stream.demo.controller;

import com.stream.demo.common.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/dev")
public class SimulationController {

    // Mock Database (In-Memory for Simulation Phase)
    // TODO: Phase 4 will use Real DB Service

    @PostMapping("/stream/start")
    public ApiResponse<Map<String, Object>> simulateStartStream(@RequestParam String streamKey) {
        // Logic:
        // 1. Validate streamKey
        // 2. Update Stream Status to LIVE
        // 3. Notify Followers (Async)

        System.out.println(">>> SIMULATION: Stream " + streamKey + " STARTED.");

        return ApiResponse.success(Map.of(
                "streamKey", streamKey,
                "status", "LIVE",
                "viewers", 0), "Stream started successfully (Simulated)");
    }

    @PostMapping("/stream/end")
    public ApiResponse<String> simulateEndStream(@RequestParam String streamKey) {
        System.out.println(">>> SIMULATION: Stream " + streamKey + " ENDED.");
        return ApiResponse.success("Stream ended", "Stream stopped successfully (Simulated)");
    }

    @PostMapping("/payment/deposit")
    public ApiResponse<Map<String, Object>> simulateDeposit(
            @RequestParam Long userId,
            @RequestParam BigDecimal amount) {

        // Logic:
        // 1. Get Wallet by userId
        // 2. Wallet.balance += amount
        // 3. Save Transaction

        System.out.println(">>> SIMULATION: Deposit " + amount + " to User " + userId);

        return ApiResponse.success(Map.of(
                "userId", userId,
                "amount", amount,
                "newBalance", "1000.00 (Mock)"), "Deposit successful (Simulated)");
    }
}
