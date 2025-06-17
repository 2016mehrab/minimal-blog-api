package com.samurai74.minimalblog.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTagName {
    @NotBlank(message="Tag name is required")
    @Size(min=2, max=30, message="Tag name must be between {min} and {max} characters")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_\\s-+\\.]*$", message = "Tag name can only contain letters, numbers, spaces and hyphens")
    private String name;
}
