package com.example.YummyDaily.repository;

import com.example.YummyDaily.entity.Recipe;
import com.example.YummyDaily.enums.Difficultylevel;
import com.example.YummyDaily.enums.StateRecipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    boolean existsByRecipeName(String recipeName);
    List<Recipe> findByCategory_CategoryId(Long categoryId);
    List<Recipe> findByUser_UserId(Long userId);
    List<Recipe> findByRecipeNameContainingIgnoreCase(String recipeName);
    List<Recipe> findByCategory_CategoryNameContainingIgnoreCase(String categoryName);
    List<Recipe> findByUser_UsernameContainingIgnoreCase(String username);
    List<Recipe> findByGeneratedDateBetween(LocalDate startDate, LocalDate endDate);
    List<Recipe> findTop5ByOrderByViewDesc();
    List<Recipe> findByState(StateRecipe state);

    // Tìm kiếm đa tiêu chí nâng cao
    @Query("SELECT r FROM Recipe r WHERE " +
            "(:keyword IS NULL OR " +
            "LOWER(r.recipeName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.category.categoryName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:state IS NULL OR r.state = :state) AND " +
            "(:userId IS NULL OR r.user.userId = :userId) AND " +
            "(:categoryId IS NULL OR r.category.categoryId = :categoryId) AND " +
            "(:difficultyLevel IS NULL OR r.difficultyLevel = :difficultyLevel) AND " +
            "(:minCookingTime IS NULL OR r.cookingTime >= :minCookingTime) AND " +
            "(:maxCookingTime IS NULL OR r.cookingTime <= :maxCookingTime)")
    List<Recipe> advancedSearch(
            @Param("keyword") String keyword,
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("difficultyLevel") Difficultylevel difficultyLevel,
            @Param("minCookingTime") Integer minCookingTime,
            @Param("maxCookingTime") Integer maxCookingTime,
            @Param("state") StateRecipe state);

    @Query("SELECT DISTINCT r FROM Recipe r " +
            "JOIN r.ingredientRecipes ir " +
            "JOIN ir.ingredient i " +
            "WHERE LOWER(i.ingredientName) LIKE LOWER(CONCAT('%', :ingredientName, '%'))")
    List<Recipe> findByIngredientNameContainingIgnoreCase(@Param("ingredientName") String ingredientName);

    @EntityGraph(attributePaths = {"category", "notifications"})
    Page<Recipe> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"category", "notifications"})
    Page<Recipe> findByState(StateRecipe state, Pageable pageable);

    @EntityGraph(attributePaths = {"category", "notifications"})
    Page<Recipe> findByUser_UserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"category", "notifications"})
    Page<Recipe> findByCategory_CategoryId(Long categoryId, Pageable pageable);
}