package com.example.YummyDaily.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "pending_notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long recipientId;
    private String recipientRole; // Lưu dưới dạng chuỗi, ví dụ: "ADMIN,USER"
    private Long recipeId;
    private Long ratingId;
    private String title;
    private String message;
    private String notificationType;
    private LocalDateTime createdAt;
    private int retryCount;
    private String status; // PENDING, SENT, FAILED
}
