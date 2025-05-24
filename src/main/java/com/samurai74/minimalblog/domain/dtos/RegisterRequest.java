package com.samurai74.minimalblog.domain.dtos;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {
    @NotBlank(message="Name is required")
    @Size(min=2, max = 50, message = "Name must be between {min} and {max} characters")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Name can only contain letters and spaces")
    private String name;
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min=6, max = 100, message = "Password must be between {min} and {max} characters")
    private String password;
}
