package com.example.YummyDaily.controller;

import com.example.YummyDaily.dto.request.ChangePasswordRequest;
import com.example.YummyDaily.dto.request.UserCreationRequest;
import com.example.YummyDaily.dto.request.UserUpdateRequest;
import com.example.YummyDaily.dto.response.ApiResponse;
import com.example.YummyDaily.dto.response.NotificationResponse;
import com.example.YummyDaily.dto.response.UserResponse;
import com.example.YummyDaily.entity.Notification;
import com.example.YummyDaily.entity.User;
import com.example.YummyDaily.exception.AppException;
import com.example.YummyDaily.exception.ErrorCode;
import com.example.YummyDaily.mapper.NotificationMapper;
import com.example.YummyDaily.mapper.UserMapper;
import com.example.YummyDaily.repository.NotificationRepository;
import com.example.YummyDaily.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {

    UserService userService;
    UserMapper userMapper;
    NotificationRepository notificationRepository;
    NotificationMapper notificationMapper;

    // Tạo người dùng - POST trả về 201 Created
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody @Valid UserCreationRequest request) {
        User createdUser = userService.createUser(request);

        // Lấy danh sách thông báo cho user vừa tạo
        List<Notification> notifications = notificationRepository.findByRecipient_userId(createdUser.getUserId());

        // Chuyển Notification → NotificationResponse
        List<NotificationResponse> notificationResponses = notifications.stream()
                .map(notificationMapper::toNotificationResponse)
                .toList();

        // Tạo UserResponse bao gồm cả notifications
        UserResponse userResponse = userMapper.toUserResponse(createdUser, notificationResponses);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(201, "Tạo người dùng thành công", userResponse));
    }

    // Lấy danh sách người dùng - GET trả về 200 OK
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("username: {}", authentication.getName());
        authentication.getAuthorities().forEach(auth -> log.info(auth.getAuthority()));

        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách người dùng thành công", users));
    }

    // Lấy thông tin cá nhân - GET trả về 200 OK
    @GetMapping("/myinfor")
    public ResponseEntity<ApiResponse<UserResponse>> getMyInfo() {
        UserResponse userInfo = userService.getMyInfo()
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy thông tin người dùng thành công", userInfo));
    }

    // Lấy người dùng theo ID - GET trả về 200 OK
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(new ApiResponse<>(200, "Tìm thấy người dùng", user));
    }

    // Cập nhật toàn bộ người dùng - PUT trả về 200 OK
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id, @RequestBody @Valid UserUpdateRequest request) {
        UserResponse updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Cập nhật người dùng thành công", updatedUser));
    }

    // Cập nhật từng phần người dùng - PATCH trả về 200 OK
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> patchUser(
            @PathVariable Long id, @RequestBody @Valid UserUpdateRequest request) {
        UserResponse updatedUser = userService.patchUser(id, request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Cập nhật người dùng thành công", updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);

        ApiResponse<String> response = new ApiResponse<>();
        response.setMessage("Xóa người dùng thành công");
        response.setResult("Success");

        return ResponseEntity.ok(response); // 200 OK với body
    }

    // Tìm kiếm người dùng theo từ khóa - GET trả về 200 OK
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserResponse>>> searchUsers(@RequestParam("keyword") String keyword) {
        log.info("Searching users with keyword: {}", keyword);
        List<UserResponse> users = userService.searchUsers(keyword);
        return ResponseEntity.ok(new ApiResponse<>(200, "Tìm kiếm người dùng thành công", users));
    }

    // Tìm kiếm người dùng theo email - GET trả về 200 OK
    @GetMapping("/search/email")
    public ResponseEntity<ApiResponse<UserResponse>> searchUserByEmail(@RequestParam("email") String email) {
        log.info("Searching user with email: {}", email);
        UserResponse user = userService.searchByUsername(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return ResponseEntity.ok(new ApiResponse<>(200, "Tìm kiếm người dùng thành công", user));
    }

    // Đổi mật khẩu - PUT trả về 200 OK
    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        log.info("Received change password request for user");

        // Lấy userId từ SecurityContext
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = userService.searchByUsername(username)
                .map(UserResponse::getUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Gọi service để đổi mật khẩu
        userService.changePassword(userId, request.getCurrentPassword(), request.getNewPassword());

        return ResponseEntity.ok(new ApiResponse<>(200, "Đổi mật khẩu thành công", null));
    }

    // Đặt mật khẩu mới - PUT trả về 200 OK
    @PutMapping("/create-password/{newPassword}")
    public ResponseEntity<ApiResponse<Void>> createNewPassword(
            @PathVariable String newPassword,
            @RequestParam String userName
    ) {
        userService.createNewPassword(userName, newPassword);
        return ResponseEntity.ok(new ApiResponse<>(200, "Đặt mật khẩu mới thành công", null));
    }
}