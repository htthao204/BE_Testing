package com.example.YummyDaily.dto.request;

import com.example.YummyDaily.enums.Difficultylevel;
import com.example.YummyDaily.enums.StateRecipe;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class SearchRecipeRequest {
     String keyword;
     StateRecipe state; // Chỉ dùng cho admin
     Long userId;
     Long categoryId;
     Difficultylevel difficultyLevel;
     Integer minCookingTime;
     Integer maxCookingTime;
     Boolean favorite;
}
