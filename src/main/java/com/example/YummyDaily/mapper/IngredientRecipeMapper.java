package com.example.YummyDaily.mapper;

import com.example.YummyDaily.dto.request.IngredientRecipeCreationRequest;
import com.example.YummyDaily.dto.request.IngredientRecipeUpdateRequest;
import com.example.YummyDaily.dto.response.IngredientRecipeResponse;
import com.example.YummyDaily.entity.IngredientRecipe;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface IngredientRecipeMapper {

    IngredientRecipe toIngredientRecipe(IngredientRecipeCreationRequest request);

    @Mapping(source = "ingredient", target = "ingredientsResponse")
    IngredientRecipeResponse toIngredientRecipeResponse(IngredientRecipe ingredientRecipe);

    void updateIngredientRecipe(@MappingTarget IngredientRecipe ingredientRecipe, IngredientRecipeUpdateRequest request);
}
