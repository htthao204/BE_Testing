package com.example.YummyDaily.service;

import com.example.YummyDaily.dto.request.IngredientRecipeCreationRequest;
import com.example.YummyDaily.dto.request.IngredientRecipeUpdateRequest;
import com.example.YummyDaily.dto.response.IngredientRecipeResponse;
import com.example.YummyDaily.dto.response.IngredientsResponse;
import com.example.YummyDaily.dto.response.RecipeResponse;
import com.example.YummyDaily.entity.IngredientRecipe;
import com.example.YummyDaily.mapper.IngredientRecipeMapper;
import com.example.YummyDaily.mapper.IngredientsMapper;
import com.example.YummyDaily.repository.IngredientRecipeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IngredientRecipeService {

    IngredientRecipeRepository ingredientRecipeRepository;
     IngredientRecipeMapper ingredientRecipeMapper;
     IngredientsService ingredientsService;
     IngredientsMapper ingredientsMapper;
    // 🟢 Thêm nguyên liệu vào công thức
    public IngredientRecipeResponse addIngredientToRecipe(IngredientRecipeCreationRequest request) {
        IngredientRecipe ingredientRecipe = ingredientRecipeMapper.toIngredientRecipe(request);
        IngredientRecipe savedIngredientRecipe = ingredientRecipeRepository.save(ingredientRecipe);
        return ingredientRecipeMapper.toIngredientRecipeResponse(savedIngredientRecipe);
    }

    // 🟢 Lấy danh sách nguyên liệu của một công thức
    public List<IngredientRecipeResponse> getIngredientsByRecipeId(Long recipeId) {
        // Lấy danh sách IngredientRecipe theo RecipeId
        List<IngredientRecipe> ingredientRecipes = ingredientRecipeRepository.findByRecipe_RecipeId(recipeId);

        // Ánh xạ từng IngredientRecipe sang IngredientRecipeResponse
        return ingredientRecipes.stream()
                .map(ingredientRecipe -> {
                    IngredientRecipeResponse response = ingredientRecipeMapper.toIngredientRecipeResponse(ingredientRecipe);

                    // Set ingredient trực tiếp vào response
                    response.setIngredientsResponse(ingredientsMapper.toIngredientsResponse(ingredientRecipe.getIngredient()));

                    return response;
                })
                .collect(Collectors.toList());
    }


    // 🟢 Cập nhật số lượng nguyên liệu
    public IngredientRecipeResponse updateIngredientRecipe(Long inreId, IngredientRecipeUpdateRequest request) {
        IngredientRecipe ingredientRecipe = ingredientRecipeRepository.findById(inreId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nguyên liệu trong công thức!"));

        ingredientRecipeMapper.updateIngredientRecipe(ingredientRecipe, request);
        IngredientRecipe updatedIngredientRecipe = ingredientRecipeRepository.save(ingredientRecipe);
        return ingredientRecipeMapper.toIngredientRecipeResponse(updatedIngredientRecipe);
    }

    // 🟢 Xóa nguyên liệu khỏi công thức
    public void deleteIngredientFromRecipe(Long inreId) {
        if (!ingredientRecipeRepository.existsById(inreId)) {
            throw new RuntimeException("Không tìm thấy nguyên liệu để xóa!");
        }
        ingredientRecipeRepository.deleteById(inreId);
    }
}
