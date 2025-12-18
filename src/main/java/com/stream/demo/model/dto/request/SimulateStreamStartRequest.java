package com.stream.demo.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SimulateStreamStartRequest {
    @NotBlank
    @Schema(description = "Stream key", example = "abc123def456")
    private String streamKey;
}
