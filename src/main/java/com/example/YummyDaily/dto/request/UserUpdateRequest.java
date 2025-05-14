package com.example.YummyDaily.dto.request;

import com.example.YummyDaily.entity.Notification;
import com.example.YummyDaily.enums.Role;
import com.example.YummyDaily.enums.Status;
import com.example.YummyDaily.exception.ErrorCode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    @Email(message = "INVALID_EMAIL")
    @Size(max = 255, message = "EMAIL_TOO_LONG")
    String username; // Renamed from username to match method logic

    @Size(max = 100, message = "FULL_NAME_TOO_LONG")
    String fullName;

    @Size(max = 500, message = "AVATAR_URL_TOO_LONG")
    String avatar;

    Set<Role> roles;

    Status status;
}
