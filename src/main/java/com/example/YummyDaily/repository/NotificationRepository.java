package com.example.YummyDaily.repository;

import com.example.YummyDaily.entity.Ingredients;
import com.example.YummyDaily.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipient_userId(Long userId);
    List<Notification> findByRecipe_RecipeId(Long recipeId);
}
