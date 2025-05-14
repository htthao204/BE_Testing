package com.example.YummyDaily.dto.request;

import com.example.YummyDaily.entity.Notification;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserFavoriteCreationRequest {

    @NotNull(message = "RECIPE_USER_ID_REQUIRED")
    Long userId;

    @NotNull(message = "RECIPE_ID_REQUIRED")
    Long recipeId;

    @NotNull(message = "RECIPE_FAVORITE_REQUIRED")
    Boolean isActive;

}
