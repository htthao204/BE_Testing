package com.example.YummyDaily.dto.response;

import com.example.YummyDaily.entity.Recipe;
import com.example.YummyDaily.entity.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserFavoriteResponse {
    Long userFavoriteId;
    Long userId;
    Long recipeId;
    Boolean isActive;
}
