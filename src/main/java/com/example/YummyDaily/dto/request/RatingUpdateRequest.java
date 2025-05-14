package com.example.YummyDaily.dto.request;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RatingUpdateRequest {

    Long userId;

    Long recipeId;

    @Min(value = 1, message = "RATING_SCORE_INVALID")
    @Max(value = 5, message = "RATING_SCORE_INVALID")
    float ratingScore;  // Đổi tên biến theo chuẩn camelCase

    @Pattern(
            regexp = "^(http|https)://.*$",
            message = "RATING_IMAGE_INVALID"
    )
    String ratingImage;  // Đổi tên biến theo chuẩn camelCase

    @Size(min = 10, max = 500, message = "RATING_DESCRIPTION_INVALID")
    String description;
}
