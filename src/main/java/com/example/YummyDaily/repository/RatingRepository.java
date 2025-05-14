package com.example.YummyDaily.repository;

import com.example.YummyDaily.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {


    List<Rating> findByRecipe_RecipeId(Long recipeId);


    List<Rating> findByUser_UserId(Long userId);
    Page<Rating> findByRecipe_RecipeId(Long recipeId, Pageable pageable);
    Page<Rating> findByUser_UserId(Long userId, Pageable pageable);
}
