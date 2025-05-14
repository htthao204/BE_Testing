package com.example.YummyDaily.dto.request;

import com.example.YummyDaily.dto.response.IngredientRecipeResponse;
import com.example.YummyDaily.enums.Difficultylevel;
import com.example.YummyDaily.enums.StateRecipe;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeCreationRequest {

    @NotBlank(message = "RECIPE_NAME_REQUIRED")
    @Size(min = 3, max = 100, message = "RECIPE_NAME_INVALID")
    String recipeName;

    @NotBlank(message = "RECIPE_DESCRIPTION_REQUIRED")
    @Size(min = 10, message = "RECIPE_DESCRIPTION_INVALID")
    String description;

    @NotBlank(message = "RECIPE_IMAGE_REQUIRED")
    @Pattern(regexp = "^(http|https)://.*$", message = "RECIPE_IMAGE_INVALID")
    String recipeImage;

    @Min(value = 1, message = "RECIPE_COOKING_TIME_INVALID")
    int cookingTime;

    @NotNull(message = "RECIPE_DIFFICULTY_REQUIRED")
    @Enumerated(EnumType.STRING)
    Difficultylevel difficultyLevel;  // Sử dụng enum thay vì String

    @Min(value = 0, message = "RECIPE_VIEW_INVALID")
    int view;

    @NotNull(message = "RECIPE_USER_ID_REQUIRED")
    Long userId;

    @NotNull(message = "RECIPE_CATEGORY_ID_REQUIRED")
    Long categoryId;

    @Min(value = 1, message = "RECIPE_NUMBER_PEOPLE_INVALID")
    int numberOfPeople;

    @PastOrPresent(message = "RECIPE_GENERATED_DATE_INVALID")
    LocalDate generatedDate;

    @NotNull(message = "RECIPE_STATE_REQUIRED")
    @Enumerated(EnumType.STRING)
    StateRecipe state;  // Sử dụng enum thay vì String

    @NotBlank(message = "RECIPE_VIDEO_REQUIRED")
    @Pattern(regexp = "^(http|https)://.*$", message = "RECIPE_VIDEO_INVALID")
    String introductionVideo;

    List<CookingStepCreationRequest> cookingStepCreationRequests;
    List<IngredientsCreationRequest> ingredientsCreationRequests;

}