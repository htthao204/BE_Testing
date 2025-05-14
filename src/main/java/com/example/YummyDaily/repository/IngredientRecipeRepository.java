package com.example.YummyDaily.repository;

import com.example.YummyDaily.entity.IngredientRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientRecipeRepository extends JpaRepository<IngredientRecipe, Long> {

    // 🟢 Tìm danh sách nguyên liệu theo ID công thức
    List<IngredientRecipe> findByRecipe_RecipeId(Long recipeId);
    void deleteByRecipe_RecipeId(Long recipeId);
}
