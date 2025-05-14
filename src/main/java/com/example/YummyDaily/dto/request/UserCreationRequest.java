package com.example.YummyDaily.dto.request;

import com.example.YummyDaily.entity.Notification;
import com.example.YummyDaily.enums.NotificationType;
import com.example.YummyDaily.enums.Role;
import com.example.YummyDaily.enums.Status;
import com.example.YummyDaily.exception.ErrorCode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreationRequest {
    @NotBlank(message = "INVALID_EMAIL")
    @Email(message = "INVALID_EMAIL")
    String username;

    @NotBlank(message = "PASSWORD_INVALID")
    @Size(min = 8, message = "PASSWORD_TOO_SHORT")
    String password;

    @NotBlank(message = "FULLNAME_INVALID")
    String fullName;

    String avatar;
    Set<Role> roles;
    Status status;
}
