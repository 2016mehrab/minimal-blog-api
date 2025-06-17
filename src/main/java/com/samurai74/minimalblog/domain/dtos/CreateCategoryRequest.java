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
public class CreateCategoryRequest {

    @NotBlank(message="Category name is required")
    @Size(min=2, max = 50, message = "Category name must be between {min} and {max} characters")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_\\s-]*$", message = "Category name can only contain letters, numbers and hyphens")
    private String name;
}
