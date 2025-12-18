package com.stream.demo.controller;

import com.stream.demo.common.ApiResponse;
import com.stream.demo.model.dto.response.AuthResponse;
import com.stream.demo.model.dto.request.LoginRequest;
import com.stream.demo.model.dto.request.RegisterRequest;
import com.stream.demo.model.dto.request.RefreshTokenRequest;
import com.stream.demo.model.dto.UserDTO;
import com.stream.demo.service.AuthService;
import com.stream.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 * <p>
 * Endpoints cho authentication với Session-backed JWT
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and registration APIs")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Create a new user account with default USER role")
    public ApiResponse<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            @RequestParam(required = false, defaultValue = "unknown") String deviceId,
            @RequestParam(required = false, defaultValue = "Unknown Device") String deviceName,
            HttpServletRequest httpRequest) {

        String ipAddress = httpRequest.getRemoteAddr();

        AuthResponse response = authService.register(request, deviceId, deviceName, ipAddress);
        return ApiResponse.success(response, "User registered successfully");
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate user and return JWT token")
    public ApiResponse<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            @RequestParam(required = false, defaultValue = "unknown") String deviceId,
            @RequestParam(required = false, defaultValue = "Unknown Device") String deviceName,
            HttpServletRequest httpRequest) {

        String ipAddress = httpRequest.getRemoteAddr();

        AuthResponse response = authService.login(request, deviceId, deviceName, ipAddress);
        return ApiResponse.success(response, "Login successful");
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get authenticated user information")
    public ApiResponse<UserDTO> getCurrentUser() {
        UserDTO userDTO = userService.convertToDTO(userService.getCurrentUser());
        return ApiResponse.success(userDTO, "Get current user info successfully");
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Get new access token using refresh token. Session will be validated from database.")
    public ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshAccessToken(request.getRefreshToken());
        return ApiResponse.success(response, "Token refreshed successfully");
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Revoke session from database. Provide refresh token in request body.")
    public ApiResponse<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ApiResponse.success(null, "Logged out successfully");
    }

    @PostMapping("/logout-all")
    @Operation(summary = "Logout from all devices", description = "Revoke all sessions of the current user. User will be logged out from all devices.")
    public ApiResponse<Void> logoutAll(@AuthenticationPrincipal UserDetails userDetails) {
        com.stream.demo.model.entity.User currentUser = userService.getUserByUsername(userDetails.getUsername());
        authService.logoutAll(currentUser.getId());
        return ApiResponse.success(null, "Logged out from all devices successfully");
    }
}
