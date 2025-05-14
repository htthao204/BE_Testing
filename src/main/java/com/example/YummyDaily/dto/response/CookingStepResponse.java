package com.example.YummyDaily.dto.response;

import com.example.YummyDaily.entity.Recipe;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CookingStepResponse {
    Long cookingId;
    Integer stepNumber;
    String description;
    String cookingImg;
    @JsonIgnore
    RecipeResponse recipeResponse;
}
