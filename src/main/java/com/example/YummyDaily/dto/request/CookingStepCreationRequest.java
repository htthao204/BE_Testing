package com.example.YummyDaily.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CookingStepCreationRequest {
    @NotNull(message = "RECIPE_ID_REQUIRED")
    @Min(value = 1, message = "RECIPE_ID_INVALID")
    Long recipeId;

    @NotNull(message = "STEP_NUMBER_REQUIRED")
    @Min(value = 1, message = "STEP_NUMBER_INVALID")
    Integer stepNumber;  // Sửa lại tên biến

    @NotBlank(message = "STEP_DESCRIPTION_REQUIRED")
    @Size(min = 5,  message = "STEP_DESCRIPTION_INVALID")
    String description;

    @NotBlank(message = "STEP_IMAGE_REQUIRED")
    @Pattern(regexp = "^(http|https):\\/\\/[^\\s$.?#].[^\\s]*$", message = "STEP_IMAGE_INVALID")
    @Size(max = 255, message = "STEP_IMAGE_TOO_LONG")
    String cookingImg;  // Sửa lại tên biến
}
