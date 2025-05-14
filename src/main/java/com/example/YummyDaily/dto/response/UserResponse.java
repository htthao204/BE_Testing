package com.example.YummyDaily.dto.response;

import com.example.YummyDaily.entity.Notification;
import com.example.YummyDaily.entity.UserFavorite;
import com.example.YummyDaily.enums.Role;
import com.example.YummyDaily.enums.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL) // Bỏ qua giá trị null
public class UserResponse {
    Long userId;
    String username;
    String fullName;
    String avatar;
    Set<Role> roles;
    Status status;
    List<NotificationResponse> notifications;
    List<UserFavoriteResponse> userFavoritesResponses;
    LocalDate date;
}
