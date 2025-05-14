package com.example.YummyDaily.mapper;

import com.example.YummyDaily.dto.request.RecipeCreationRequest;
import com.example.YummyDaily.dto.response.RecipeResponse;
import com.example.YummyDaily.entity.Ingredients;
import com.example.YummyDaily.entity.Recipe;
import com.example.YummyDaily.repository.RecipeRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

//@Mapper(componentModel = "spring")
//public interface RecipeMapper {
//
//    @Mapping(source = "category.categoryId", target = "categoryId")
//    //@Mapping(source = "ingredientRecipes", target = "ingredientRecipeResponses")
//    @Mapping(source = "cookingSteps", target = "cookingStepResponses")
//    @Mapping(source = "notifications", target = "notifications")
//
//    RecipeResponse toRecipeResponse(Recipe recipe);
//
//    Recipe toRecipe(RecipeCreationRequest request);
//
//    void updateRecipe(@MappingTarget Recipe recipe, RecipeCreationRequest request);
//}
@Mapper(componentModel = "spring", uses = {IngredientRecipeMapper.class})
public interface RecipeMapper {

    @Mapping(source = "category.categoryId", target = "categoryId")
    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "ingredientRecipes", target = "ingredientRecipeResponses")
    @Mapping(source = "cookingSteps", target = "cookingStepResponses")
    @Mapping(source = "notifications", target = "notifications")
    @Mapping(source = "userFavorites", target = "userFavoriteResponses")
    RecipeResponse toRecipeResponse(Recipe recipe);

    Recipe toRecipe(RecipeCreationRequest request);

    void updateRecipe(@MappingTarget Recipe recipe, RecipeCreationRequest request);
}
