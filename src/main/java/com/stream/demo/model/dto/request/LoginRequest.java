package com.stream.demo.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @Schema(description = "Username for login", example = "user001")
    @NotBlank(message = "Username is required")
    private String username;

    @Schema(description = "Password for login", example = "Password123!")
    @NotBlank(message = "Password is required")
    private String password;
}
