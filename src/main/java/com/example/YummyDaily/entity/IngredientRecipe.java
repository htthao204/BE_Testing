package com.example.YummyDaily.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "recipe_ingredients")
public class IngredientRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long inreId;

    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false) // Tạo khóa ngoại kết nối với Recipe
    Recipe recipe;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false) // Tạo khóa ngoại kết nối với Ingredients
    Ingredients ingredient;

    Double quantity; // Lượng nguyên liệu cần dùng
}
