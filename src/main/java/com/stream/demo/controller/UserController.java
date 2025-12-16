package com.stream.demo.controller;

import com.stream.demo.model.dto.UserDTO;
import com.stream.demo.model.dto.request.UpdateUserRequest;
import com.stream.demo.common.ApiResponse;
import com.stream.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for user profile management")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get user profile", description = "Get public user profile by ID")
    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated()") // Anyone authenticated can view public profiles
    public ResponseEntity<ApiResponse<UserDTO>> getUserProfile(@PathVariable Long userId) {
        UserDTO userDTO = userService.getUserProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(userDTO, "Get user profile successfully"));
    }

    @Operation(summary = "Update user profile", description = "Update user profile information. Only allowed for the user themselves or ADMIN.")
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<ApiResponse<UserDTO>> updateUserProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequest request) {
        UserDTO updatedUser = userService.updateUser(userId, request);
        return ResponseEntity.ok(ApiResponse.success(updatedUser, "Update user profile successfully"));
    }
}
