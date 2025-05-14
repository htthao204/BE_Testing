package com.example.YummyDaily.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryCreationRequest {

    @NotBlank(message = "CATEGORY_NAME_REQUIRED")
    @Size(min = 2, max = 50, message = "CATEGORY_NAME_INVALID") // Cập nhật max = 50
    String categoryName;
    @NotBlank(message = "CATEGORY_IMAGE_REQUIRED")
    @Pattern(
            regexp = "^(http|https)://.*$",
            message = "CATEGORY_IMAGE_INVALID"
    )
    String categoryImage;
}
