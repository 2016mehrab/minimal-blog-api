package com.samurai74.minimalblog.domain.dtos;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordResetConfirmRequest {
    @NotBlank(message = "Missing token")
    private String token;
    @NotBlank(message = "Password is required")
    @Size(min=6, max = 100, message = "Password must be between {min} and {max} characters")
    private String newPassword;
}
