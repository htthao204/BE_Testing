package com.example.YummyDaily.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RatingCreationRequest {
    @NotNull(message = "RECIPE_USER_ID_REQUIRED")
    Long userId;

    @NotNull(message = "RECIPE_ID_REQUIRED")
    Long recipeId;

    @Min(value = 1, message = "RATING_SCORE_INVALID")
    @Max(value = 5, message = "RATING_SCORE_INVALID")
    int ratingScore;

//    @Pattern(
//            regexp = "^(http|https)://.*$",
//            message = "RATING_IMAGE_INVALID"
//    )
    String ratingImage;

    @NotBlank(message = "RATING_DESCRIPTION_REQUIRED")
    @Size(min = 10, max = 500, message = "RATING_DESCRIPTION_INVALID")
    String description;


}
