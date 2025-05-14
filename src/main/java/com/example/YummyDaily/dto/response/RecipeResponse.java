package com.example.YummyDaily.dto.response;

import com.example.YummyDaily.entity.Notification;
import com.example.YummyDaily.entity.UserFavorite;
import com.example.YummyDaily.enums.Difficultylevel;
import com.example.YummyDaily.enums.StateRecipe;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecipeResponse {
    Long recipeId;
    String recipeName;
    String description;
    String recipeImage;
    int cookingTime;
    Difficultylevel difficultyLevel;
    int view;
    int numberOfPeople;
    LocalDate generatedDate;
    StateRecipe state;
    String introductionVideo;
//    UserResponse userResponse;
    Long userId;
    CategoryResponse categoryResponse;
    Long categoryId;
    List<IngredientRecipeResponse> ingredientRecipeResponses;
    List<CookingStepResponse> cookingStepResponses;
    List<NotificationResponse> notifications;
    List<UserFavoriteResponse> userFavoriteResponses;
}
