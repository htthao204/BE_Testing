package com.example.YummyDaily.repository;

import com.example.YummyDaily.entity.Recipe;
import com.example.YummyDaily.entity.User;
import com.example.YummyDaily.entity.UserFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserFavoriteRepository extends JpaRepository<UserFavorite, Long> {
    Optional<UserFavorite> findByUserAndRecipe(User user, Recipe recipe);

    List<UserFavorite> findAllByUser(User user);
    List<UserFavorite> findByRecipe_RecipeId(Long recipeId);
    List<UserFavorite> findAllByRecipe(Recipe recipe);
    List<UserFavorite> findByRecipe_RecipeIdIn(List<Long> recipeIds);
    boolean existsByUserAndRecipe(User user, Recipe recipe);
}
