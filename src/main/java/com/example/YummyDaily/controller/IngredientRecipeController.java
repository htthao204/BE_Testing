package com.example.YummyDaily.controller;

import com.example.YummyDaily.dto.request.IngredientRecipeCreationRequest;
import com.example.YummyDaily.dto.request.IngredientRecipeUpdateRequest;
import com.example.YummyDaily.dto.response.IngredientRecipeResponse;
import com.example.YummyDaily.service.IngredientRecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ingredient-recipe")
@RequiredArgsConstructor
public class IngredientRecipeController {

    private final IngredientRecipeService ingredientRecipeService;

    // 🟢 Thêm nguyên liệu vào công thức
    @PostMapping()
    public ResponseEntity<IngredientRecipeResponse> addIngredientToRecipe(
            @RequestBody  @Valid IngredientRecipeCreationRequest request) {
        return ResponseEntity.ok(ingredientRecipeService.addIngredientToRecipe(request));
    }

    // 🟢 Lấy danh sách nguyên liệu của một công thức theo recipeId
    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<List<IngredientRecipeResponse>> getIngredientsByRecipeId(@PathVariable Long recipeId) {
        return ResponseEntity.ok(ingredientRecipeService.getIngredientsByRecipeId(recipeId));
    }

    // 🟢 Cập nhật số lượng nguyên liệu trong công thức
    @PutMapping("/{inreId}")
    public ResponseEntity<IngredientRecipeResponse> updateIngredientRecipe(
            @PathVariable Long inreId,
            @RequestBody  @Valid IngredientRecipeUpdateRequest request) {
        return ResponseEntity.ok(ingredientRecipeService.updateIngredientRecipe(inreId, request));
    }

    // 🟢 Xóa nguyên liệu khỏi công thức
    @DeleteMapping("/{inreId}")
    public ResponseEntity<String> deleteIngredientFromRecipe(@PathVariable Long inreId) {
        ingredientRecipeService.deleteIngredientFromRecipe(inreId);
        return ResponseEntity.ok("Đã xóa nguyên liệu khỏi công thức thành công!");
    }
}
