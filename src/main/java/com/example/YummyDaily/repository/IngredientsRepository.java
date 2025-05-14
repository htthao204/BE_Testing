package com.example.YummyDaily.repository;

import com.example.YummyDaily.entity.Ingredients;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IngredientsRepository extends JpaRepository<Ingredients, Long> {
    Optional<Ingredients> findByIngredientName(String ingredientName);

}
