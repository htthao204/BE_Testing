package com.example.YummyDaily.dto.response;

import com.example.YummyDaily.entity.Notification;
import com.example.YummyDaily.entity.Recipe;
import com.example.YummyDaily.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RatingResponse {
    Long ratingId;
    float ratingScore;
    String ratingImage;
    String description;
    Long recipeId;
    User user;
    List<Notification> notifications;
    LocalDate date;
}
