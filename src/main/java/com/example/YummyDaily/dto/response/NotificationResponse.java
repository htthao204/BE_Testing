package com.example.YummyDaily.dto.response;

import com.example.YummyDaily.entity.Rating;
import com.example.YummyDaily.entity.Recipe;
import com.example.YummyDaily.entity.User;
import com.example.YummyDaily.enums.NotificationType;
import com.example.YummyDaily.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {
    Long notificationId;

    // Chỉ trả về thông tin cơ bản về người nhận
    UserResponse recipient;

    // Vai trò của người nhận
    Set<Role> recipientRole;

    // Chỉ trả về thông tin cơ bản về công thức
    Long recipeId;

    String title;
    String message;
    boolean read;
    LocalDateTime createdAt;
    NotificationType notificationType;

    // Nếu thông báo liên quan đến đánh giá, chỉ trả về thông tin cơ bản về Rating
    Long ratingId;

    // Lớp con UserResponse chỉ trả về các thông tin cơ bản về người dùng
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class UserResponse {
        Long userId;
        String username;
        String fullName;
    }

    // Lớp con RecipeResponse chỉ trả về các thông tin cơ bản về công thức
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class RecipeResponse {
        Long recipeId;
        String title;
        String description;
    }

    // Lớp con RatingResponse chỉ trả về các thông tin cơ bản về Rating
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class RatingResponse {
        Long ratingId;
        int score;
    }
}
