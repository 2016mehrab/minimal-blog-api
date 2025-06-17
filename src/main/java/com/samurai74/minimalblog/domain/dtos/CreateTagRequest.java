package com.samurai74.minimalblog.domain.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTagRequest {
    @NotEmpty(message = "At least one tag name is required")
    @Size(max=10, message = "Maximum {max} tags allowed")
   private Set<
            @Size(min=2, max=30, message="Tag name must be between {min} and {max} characters")
            @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_\\s-+\\.]*$", message = "Tag name can only contain letters, numbers, spaces and hyphens")
                    String> tags;
}
