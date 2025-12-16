package com.stream.demo.controller;

import com.stream.demo.common.ApiResponse;
import com.stream.demo.model.dto.AuthResponse;
import com.stream.demo.model.dto.LoginRequest;
import com.stream.demo.model.dto.RegisterRequest;
import com.stream.demo.model.dto.UserDTO;
import com.stream.demo.service.AuthService;
import com.stream.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and registration APIs")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Create a new user account with default USER role")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ApiResponse.success(response, "User registered successfully");
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate user and return JWT token")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ApiResponse.success(response, "Login successful");
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get authenticated user information")
    public ApiResponse<UserDTO> getCurrentUser() {
        UserDTO userDTO = userService.convertToDTO(userService.getCurrentUser());
        return ApiResponse.success(userDTO, null);
    }
}
