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
public class IngredientsUpdateRequest {
    @NotBlank(message = "INGREDIENT_NAME_REQUIRED")
    @Size(min = 2, max = 50, message = "INGREDIENT_NAME_INVALID")
    String ingredientName; // Chuẩn hóa tên biến

    @NotBlank(message = "INGREDIENT_UNIT_REQUIRED")
    @Size(min = 1, max = 10, message = "INGREDIENT_UNIT_INVALID") // Điều chỉnh hợp lý hơn
    String unit;

    @NotBlank(message = "INGREDIENT_IMAGE_REQUIRED")
    @Pattern(
            regexp = "^(http|https)://.*\\.(?i)(jpg|jpeg|png|gif|bmp|webp)$", // (?i) để không phân biệt hoa thường
            message = "INGREDIENT_IMAGE_INVALID"
    )
    String ingredientImage;
    Double amount;
}
