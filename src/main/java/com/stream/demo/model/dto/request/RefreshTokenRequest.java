package com.stream.demo.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for refreshing access token
 * DTO yêu cầu để làm mới access token bằng refresh token
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenRequest {

    @Schema(description = "JWT Refresh Token obtained from login/register", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMDAxIiwic2Vzc2lvbl9pZCI6IjEyMzQ1Njc4LTkwYWItY2RlZi0xMjM0LTU2Nzg5MGFiY2RlZiIsImRldmljZV9pZCI6ImRldmljZS0xMjMiLCJpYXQiOjE3MDMwMDAwMDAsImV4cCI6MTcwMzYwNDgwMH0.example_signature")
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}
