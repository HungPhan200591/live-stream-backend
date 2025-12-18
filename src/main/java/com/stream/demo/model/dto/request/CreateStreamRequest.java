package com.stream.demo.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request DTO cho việc tạo stream mới
 */
@Data
@Schema(description = "Request body để tạo stream mới")
public class CreateStreamRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must be at most 100 characters")
    @Schema(description = "Tiêu đề của stream", example = "My Gaming Stream")
    private String title;

    @Size(max = 2000, message = "Description must be at most 2000 characters")
    @Schema(description = "Mô tả chi tiết về stream", example = "Playing Valorant ranked - Road to Diamond!")
    private String description;
}
