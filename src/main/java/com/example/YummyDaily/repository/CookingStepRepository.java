package com.example.YummyDaily.repository;

import com.example.YummyDaily.entity.CookingStep;
import com.example.YummyDaily.entity.IngredientRecipe;
import com.example.YummyDaily.entity.Ingredients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List; // Cần import để tránh lỗi
import java.util.Optional;

@Repository
public interface CookingStepRepository extends JpaRepository<CookingStep, Long> {
    List<CookingStep> findByRecipe_RecipeId(Long recipeId);
    @Query("SELECT ir.ingredient FROM IngredientRecipe ir WHERE ir.inreId = :inreId")
    Optional<Ingredients> findIngredientByInreId(@Param("inreId") Long inreId);
    void deleteByRecipe_RecipeId(Long recipeId);
}
