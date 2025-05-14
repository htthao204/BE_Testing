package com.example.YummyDaily.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IngredientRecipeCreationRequest {

    @NotNull(message = "RECIPE_ID_REQUIRED")
    Long recipeId; // ID của công thức nấu ăn

    @NotNull(message = "INGREDIENT_ID_REQUIRED")
    Long ingredientId; // ID của nguyên liệu

    @NotNull(message = "QUANTITY_REQUIRED")
    @Min(value = 0, message = "QUANTITY_MUST_BE_POSITIVE")
    Double quantity; // Số lượng nguyên liệu cần dùng
}
