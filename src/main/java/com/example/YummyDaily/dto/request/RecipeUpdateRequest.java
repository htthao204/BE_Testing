package com.example.YummyDaily.dto.request;

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
public class RecipeUpdateRequest {

    @Size(min = 3, max = 100, message = "RECIPE_NAME_INVALID")
    String recipeName;

    @Size(min = 10, max = 500, message = "RECIPE_DESCRIPTION_INVALID")
    String description;

    @Pattern(regexp = "^(http|https)://.*$", message = "RECIPE_IMAGE_INVALID")
    String recipeImage;

    @Min(value = 1, message = "RECIPE_COOKING_TIME_INVALID")
    Integer cookingTime; // Changed to Integer to allow null

    @Enumerated(EnumType.STRING)
    Difficultylevel difficultyLevel;

    @Min(value = 0, message = "RECIPE_VIEW_INVALID")
    Integer view; // Changed to Integer to allow null

    Long userId;

    Long categoryId;

    @Min(value = 1, message = "RECIPE_NUMBER_PEOPLE_INVALID")
    Integer numberOfPeople; // Changed to Integer to allow null

    @PastOrPresent(message = "RECIPE_GENERATED_DATE_INVALID")
    LocalDate generatedDate;

    @Enumerated(EnumType.STRING)
    StateRecipe state;

    @Pattern(regexp = "^(http|https)://.*$", message = "RECIPE_VIDEO_INVALID")
    String introductionVideo;

    List<CookingStepCreationRequest> cookingStepCreationRequests;
    List<IngredientsCreationRequest> ingredientsCreationRequests;
}