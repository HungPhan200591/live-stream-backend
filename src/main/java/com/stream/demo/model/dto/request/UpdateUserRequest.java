package com.stream.demo.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {

    @Schema(description = "Display name for user profile", example = "John Doe")
    @Size(min = 3, max = 50, message = "Display name must be between 3 and 50 characters")
    private String displayName;

    @Schema(description = "User biography/description", example = "Passionate streamer and gamer. Love to share my gaming experiences!")
    @Size(max = 500, message = "Bio cannot exceed 500 characters")
    private String bio;

    @Schema(description = "URL to user avatar image", example = "https://example.com/avatars/user001.jpg")
    private String avatarUrl;

    @Schema(description = "Email address", example = "newemail@example.com")
    @Email(message = "Invalid email format")
    private String email;
}
