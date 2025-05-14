package com.example.YummyDaily.service;

import com.example.YummyDaily.dto.request.NotificationCreationRequest;
import com.example.YummyDaily.dto.request.UserCreationRequest;
import com.example.YummyDaily.dto.request.UserUpdateRequest;
import com.example.YummyDaily.dto.response.UserFavoriteResponse;
import com.example.YummyDaily.dto.response.UserResponse;
import com.example.YummyDaily.entity.User;
import com.example.YummyDaily.enums.NotificationType;
import com.example.YummyDaily.enums.Role;
import com.example.YummyDaily.enums.Status;
import com.example.YummyDaily.exception.AppException;
import com.example.YummyDaily.exception.ErrorCode;
import com.example.YummyDaily.mapper.UserMapper;
import com.example.YummyDaily.repository.NotificationRepository;
import com.example.YummyDaily.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    NotificationRepository notificationRepository;
    NotificationService notificationService;
    UserFavoriteService userFavoriteService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users");
        return userMapper.toUserResponseList(userRepository.findAll());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or #userId == authentication.principal.id")
    public UserResponse getUserById(Long userId) {
        log.info("Fetching user by ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<UserFavoriteResponse> userFavoriteResponses = userFavoriteService.getAllByUser(user.getUserId());
        UserResponse userResponse = userMapper.toUserResponse(user);
        userResponse.setUserFavoritesResponses(userFavoriteResponses);
        userResponse.setStatus(user.getStatus());
        return userResponse;
    }

    public Optional<UserResponse> getMyInfo() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        List<UserFavoriteResponse> userFavoriteResponses = userFavoriteService.getAllByUser(user.getUserId());
        UserResponse userResponse = userMapper.toUserResponse(user);
        userResponse.setUserFavoritesResponses(userFavoriteResponses);
        return Optional.of(userResponse);
    }

    public User createUser(UserCreationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(Status.ACTIVE);
        Set<Role> roles = request.getRoles() == null || request.getRoles().isEmpty()
                ? Set.of(Role.USER)
                : new HashSet<>(request.getRoles());
        user.setRoles(roles);
        user.setDate(LocalDate.now());
        User savedUser = userRepository.save(user);

        // Thông báo chào mừng người dùng
        NotificationCreationRequest welcomeNotification = NotificationCreationRequest.builder()
                .recipientId(savedUser.getUserId())
                .recipientRole(savedUser.getRoles())
                .title("Chào mừng đến với Yummy Daily!")
                .message("Cảm ơn bạn đã đăng ký. Hãy bắt đầu chia sẻ công thức nấu ăn cùng cộng đồng!")
                .notificationType(NotificationType.WELCOME_MESSAGE)
                .createdAt(LocalDateTime.now())
                .build();
        notificationService.createNotification(welcomeNotification);

        // Thông báo cho admin
        List<User> admins = userRepository.findByRolesContaining(Role.ADMIN);
        for (User admin : admins) {
            NotificationCreationRequest adminNotification = NotificationCreationRequest.builder()
                    .recipientId(admin.getUserId())
                    .recipientRole(admin.getRoles())
                    .title("Người dùng mới đăng ký")
                    .message("Người dùng '" + savedUser.getUsername() + "' vừa đăng ký vào hệ thống.")
                    .notificationType(NotificationType.NEW_USER_REGISTERED)
                    .createdAt(LocalDateTime.now())
                    .build();
            notificationService.createNotification(adminNotification);
        }

        return savedUser;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or #userId == authentication.principal.id")
    @Transactional
    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        userMapper.updateUser(user, request);
        User updatedUser = userRepository.save(user);

        // Thông báo cho người dùng về cập nhật thông tin
        NotificationCreationRequest userNotification = NotificationCreationRequest.builder()
                .recipientId(user.getUserId())
                .recipientRole(user.getRoles())
                .title("Thông tin cá nhân được cập nhật")
                .message("Thông tin cá nhân của bạn đã được cập nhật thành công.")
                .notificationType(NotificationType.GENERAL_USER)
                .createdAt(LocalDateTime.now())
                .build();
        notificationService.createNotification(userNotification);

        return userMapper.toUserResponse(updatedUser);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or #userId == authentication.principal.id")
    @Transactional
    public UserResponse patchUser(Long userId, UserUpdateRequest request) {
        log.info("Patching user with ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Update fields if provided
        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName());
        }
        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            if (!user.getUsername().equals(request.getUsername()) &&
                    userRepository.existsByUsername(request.getUsername())) {
                throw new AppException(ErrorCode.EMAIL_EXISTED);
            }
            user.setUsername(request.getUsername());
        }
        if (request.getAvatar() != null && !request.getAvatar().isBlank()) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            user.setRoles(new HashSet<>(request.getRoles()));
        }

        User updatedUser = userRepository.save(user);

        // Notify user about profile update
        NotificationCreationRequest userNotification = NotificationCreationRequest.builder()
                .recipientId(user.getUserId())
                .recipientRole(user.getRoles())
                .title("Thông tin cá nhân được cập nhật")
                .message("Một số thông tin cá nhân của bạn đã được cập nhật thành công.")
                .notificationType(NotificationType.GENERAL_USER)
                .createdAt(LocalDateTime.now())
                .build();
        notificationService.createNotification(userNotification);

        return userMapper.toUserResponse(updatedUser);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Thông báo cho admin (nếu admin khác xóa)
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!currentUser.getUserId().equals(userId)) { // Không gửi thông báo nếu tự xóa
            List<User> admins = userRepository.findByRolesContaining(Role.ADMIN);
            for (User admin : admins) {
                if (!admin.getUserId().equals(currentUser.getUserId())) { // Không gửi cho chính admin đang xóa
                    NotificationCreationRequest adminNotification = NotificationCreationRequest.builder()
                            .recipientId(admin.getUserId())
                            .recipientRole(admin.getRoles())
                            .title("Người dùng bị xóa")
                            .message("Người dùng '" + user.getUsername() + "' đã bị xóa bởi admin " + currentUser.getUsername() + ".")
                            .notificationType(NotificationType.GENERAL_ADMIN)
                            .createdAt(LocalDateTime.now())
                            .build();
                    notificationService.createNotification(adminNotification);
                }
            }
        }

        userRepository.delete(user);
    }

    public Optional<UserResponse> searchByUsername(String username) {
        log.info("Searching user by username: {}", username);
        return userRepository.findByUsername(username)
                .map(userMapper::toUserResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> searchUsers(String keyword) {
        log.info("Searching users with keyword: {}", keyword);

        List<User> usersByUsername = userRepository.findByUsername(keyword)
                .map(List::of)
                .orElse(List.of());
        List<User> usersByFullName = userRepository.findByFullNameContainingIgnoreCase(keyword);
        List<User> usersByRole = new ArrayList<>();
        try {
            Role role = Role.valueOf(keyword.toUpperCase());
            usersByRole = userRepository.findByRolesContaining(role);
        } catch (IllegalArgumentException e) {
            log.warn("Keyword '{}' không khớp với Role enum", keyword);
        }

        Set<User> uniqueUsers = new HashSet<>();
        uniqueUsers.addAll(usersByUsername);
        uniqueUsers.addAll(usersByFullName);
        uniqueUsers.addAll(usersByRole);

        return userMapper.toUserResponseList(new ArrayList<>(uniqueUsers));
    }

    public void changePassword(Long userId, String currentPassword, String newPassword) {
        log.info("Attempting to change password for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        NotificationCreationRequest notification = NotificationCreationRequest.builder()
                .recipientId(user.getUserId())
                .recipientRole(user.getRoles())
                .title("Đổi mật khẩu thành công")
                .message("Mật khẩu của bạn đã được cập nhật thành công.")
                .notificationType(NotificationType.PASSWORD_CHANGED)
                .createdAt(LocalDateTime.now())
                .build();

        notificationService.createNotification(notification);

        log.info("Password changed successfully for user ID: {}", userId);
    }

    public void createNewPassword(String userName, String newPassword) {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedNewPassword);
        userRepository.save(user);

        NotificationCreationRequest notification = NotificationCreationRequest.builder()
                .recipientId(user.getUserId())
                .recipientRole(user.getRoles())
                .title("Mật khẩu mới được đặt lại")
                .message("Mật khẩu của bạn đã được đặt lại thành công. Vui lòng đăng nhập với mật khẩu mới.")
                .notificationType(NotificationType.PASSWORD_CHANGED)
                .createdAt(LocalDateTime.now())
                .build();
        notificationService.createNotification(notification);
    }
}