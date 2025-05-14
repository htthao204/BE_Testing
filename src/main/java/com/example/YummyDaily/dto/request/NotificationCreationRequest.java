package com.example.YummyDaily.dto.request;

import com.example.YummyDaily.enums.NotificationType;
import com.example.YummyDaily.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationCreationRequest {
    @NotNull(message = "RECIPIENT_ID_REQUIRED")  // Đảm bảo recipientId không null
    Long recipientId;

    @NotNull(message = "ROLE_REQUIRED")  // Đảm bảo recipientRole không null
    Set<Role> recipientRole;

    Long recipeId; // Có thể null nếu thông báo không liên quan đến recipe

    Long ratingId; // Có thể null nếu thông báo không liên quan đến rating

    @NotBlank(message = "TITLE_REQUIRED")  // Đảm bảo title không rỗng
    String title;

    @NotBlank(message = "MESSAGE_REQUIRED")  // Đảm bảo message không rỗng
    String message;

    @NotNull(message = "TYPE_REQUIRED")  // Đảm bảo type không null
    NotificationType notificationType;

    LocalDateTime createdAt;
}
