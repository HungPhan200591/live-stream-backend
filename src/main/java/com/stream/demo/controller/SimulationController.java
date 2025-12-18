package com.stream.demo.controller;

import com.stream.demo.common.ApiResponse;
import com.stream.demo.model.dto.StreamDTO;
import com.stream.demo.model.dto.WalletDTO;
import com.stream.demo.model.dto.request.SimulateDepositRequest;
import com.stream.demo.model.dto.request.SimulateStreamEndRequest;
import com.stream.demo.model.dto.request.SimulateStreamStartRequest;
import com.stream.demo.service.StreamService;
import com.stream.demo.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@Profile("dev")
@RestController
@RequestMapping("/api/dev/simulate")
@RequiredArgsConstructor
@Tag(name = "Development Simulation", description = "Simulate external events for testing")
public class SimulationController {

    private final StreamService streamService;
    private final WalletService walletService;

    /**
     * Giả lập OBS stream start
     */
    @PostMapping("/stream/start")
    @Operation(summary = "Simulate stream start event")
    public ApiResponse<StreamDTO> simulateStreamStart(
            @RequestBody @Valid SimulateStreamStartRequest request) {
        StreamDTO stream = streamService.startStream(request.getStreamKey());
        return ApiResponse.success(stream, "Stream started (simulated)");
    }

    /**
     * Giả lập OBS stream end
     */
    @PostMapping("/stream/end")
    @Operation(summary = "Simulate stream end event")
    public ApiResponse<StreamDTO> simulateStreamEnd(
            @RequestBody @Valid SimulateStreamEndRequest request) {
        StreamDTO stream = streamService.endStream(request.getStreamKey());
        return ApiResponse.success(stream, "Stream ended (simulated)");
    }

    /**
     * Giả lập payment deposit
     */
    @PostMapping("/payment/deposit")
    @Operation(summary = "Simulate payment deposit")
    public ApiResponse<WalletDTO> simulateDeposit(
            @RequestBody @Valid SimulateDepositRequest request) {
        WalletDTO wallet = walletService.deposit(
                request.getUserId(),
                request.getAmount(),
                "Simulated deposit");
        return ApiResponse.success(wallet, "Deposit successful (simulated)");
    }
}
