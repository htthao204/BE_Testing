package com.example.YummyDaily.entity;

import com.example.YummyDaily.enums.NotificationType;
import com.example.YummyDaily.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long notificationId;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    User recipient;

    @Enumerated(EnumType.STRING)
    Set<Role> recipientRole;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    Recipe recipe;

    String title;
    String message;
    boolean isRead;

    LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    NotificationType notificationType;

    @ManyToOne
    @JoinColumn(name = "rating_id", nullable = true)
    Rating rating;
}
