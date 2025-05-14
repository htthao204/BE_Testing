package com.example.YummyDaily.dto.response;

import com.example.YummyDaily.entity.IngredientRecipe;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IngredientsResponse {
    Long ingredientId;
    String ingredientName;
    String unit;
    String ingredientImage;
}
