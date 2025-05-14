package com.example.YummyDaily.service;

import com.example.YummyDaily.dto.request.NotificationCreationRequest;
import com.example.YummyDaily.dto.request.NotificationUpdateRequest;
import com.example.YummyDaily.dto.response.NotificationResponse;
import com.example.YummyDaily.entity.Notification;
import com.example.YummyDaily.entity.PendingNotification;
import com.example.YummyDaily.entity.Recipe;
import com.example.YummyDaily.entity.Rating;
import com.example.YummyDaily.entity.User;
import com.example.YummyDaily.enums.NotificationType;
import com.example.YummyDaily.enums.Role;
import com.example.YummyDaily.exception.AppException;
import com.example.YummyDaily.exception.ErrorCode;
import com.example.YummyDaily.mapper.NotificationMapper;
import com.example.YummyDaily.repository.NotificationRepository;
import com.example.YummyDaily.repository.PendingNotificationRepository;
import com.example.YummyDaily.repository.RecipeRepository;
import com.example.YummyDaily.repository.RatingRepository;
import com.example.YummyDaily.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class NotificationService {
    NotificationRepository notificationRepository;
    NotificationMapper notificationMapper;
    UserRepository userRepository;
    RecipeRepository recipeRepository;
    RatingRepository ratingRepository;
    PendingNotificationRepository pendingNotificationRepository;
    SimpMessagingTemplate messagingTemplate;
    private static final int MAX_RETRIES = 3;

    // Tạo mới một notification
    @Transactional
    public NotificationResponse createNotification(NotificationCreationRequest request) {
        User recipient = userRepository.findById(request.getRecipientId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Notification notification = notificationMapper.toNotification(request);
        notification.setRecipient(recipient);
        notification.setCreatedAt(LocalDateTime.now());

        // Lưu notification vào database
        Notification savedNotification = notificationRepository.save(notification);
        if (savedNotification == null) {
            throw new AppException(ErrorCode.NOTIFICATION_SAVE_FAILED);
        }

        sendNotificationToRecipient(savedNotification, request);
        return notificationMapper.toNotificationResponse(savedNotification);
    }

    // Gửi thông báo qua WebSocket
//    private void sendNotificationToRecipient(Notification notification, NotificationCreationRequest request) {
//        String destination = "/topic/notifications/" + notification.getRecipient().getUserId();
//        try {
//            messagingTemplate.convertAndSend(destination, notification);
//        } catch (Exception e) {
//            log.error("Không thể gửi thông báo WebSocket cho người dùng {}: {}", notification.getRecipient().getUserId(), e.getMessage());
//            storePendingNotification(request);
//        }
//    }
    private void sendNotificationToRecipient(Notification notification, NotificationCreationRequest request) {
        String destination = "/topic/notifications/" + notification.getRecipient().getUserId();
        try {
            NotificationResponse response = notificationMapper.toNotificationResponse(notification);
            messagingTemplate.convertAndSend(destination, response);
        } catch (Exception e) {
            log.error("Không thể gửi thông báo WebSocket cho người dùng {}: {}", notification.getRecipient().getUserId(), e.getMessage());
            storePendingNotification(request);
        }
    }


    // Lưu thông báo chưa gửi được
    @Transactional
    public void storePendingNotification(NotificationCreationRequest request) {
        String rolesAsString = request.getRecipientRole().stream()
                .map(Role::name)
                .collect(Collectors.joining(","));

        PendingNotification pending = PendingNotification.builder()
                .recipientId(request.getRecipientId())
                .recipientRole(rolesAsString)
                .recipeId(request.getRecipeId())
                .ratingId(request.getRatingId())
                .title(request.getTitle())
                .message(request.getMessage())
                .notificationType(request.getNotificationType().name())
                .createdAt(request.getCreatedAt())
                .retryCount(0)
                .status("PENDING")
                .build();
        pendingNotificationRepository.save(pending);
    }

    // Job định kỳ để thử gửi lại thông báo
//    @Scheduled(fixedRate = 60000)
//    @Transactional
//    public void retryPendingNotifications() {
//        List<PendingNotification> pendings = pendingNotificationRepository
//                .findByStatusAndRetryCountLessThan("PENDING", MAX_RETRIES);
//        for (PendingNotification pending : pendings) {
//            try {
//                User recipient = userRepository.findById(pending.getRecipientId())
//                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
//
//                // Chuyển recipientRole từ String sang Set<Role>
//                Set<Role> roles = Arrays.stream(pending.getRecipientRole().split(","))
//                        .map(Role::valueOf)
//                        .collect(Collectors.toSet());
//
//                // Lấy Recipe nếu có recipeId
//                Recipe recipe = null;
//                if (pending.getRecipeId() != null) {
//                    recipe = recipeRepository.findById(pending.getRecipeId())
//                            .orElseThrow(() -> new AppException(ErrorCode.RECIPE_NOT_EXISTED));
//                }
//
//                // Lấy Rating nếu có ratingId
//                Rating rating = null;
//                if (pending.getRatingId() != null) {
//                    rating = ratingRepository.findById(pending.getRatingId())
//                            .orElseThrow(() -> new AppException(ErrorCode.RATING_NOT_FOUND));
//                }
//
//                Notification notification = Notification.builder()
//                        .recipient(recipient)
//                        .recipientRole(roles)
//                        .recipe(recipe)
//                        .rating(rating)
//                        .title(pending.getTitle())
//                        .message(pending.getMessage())
//                        .notificationType(NotificationType.valueOf(pending.getNotificationType()))
//                        .createdAt(pending.getCreatedAt())
//                        .build();
//
//                String destination = "/topic/notifications/" + pending.getRecipientId();
//                messagingTemplate.convertAndSend(destination, notification);
//                pending.setStatus("SENT");
//            } catch (Exception e) {
//                pending.setRetryCount(pending.getRetryCount() + 1);
//                if (pending.getRetryCount() >= MAX_RETRIES) {
//                    pending.setStatus("FAILED");
//                    log.warn("Thông báo cho người dùng {} thất bại sau {} lần thử", pending.getRecipientId(), MAX_RETRIES);
//                }
//                log.error("Thử gửi lại thông báo thất bại: {}", e.getMessage());
//            }
//            pendingNotificationRepository.save(pending);
//        }
//    }
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void retryPendingNotifications() {
        List<PendingNotification> pendings = pendingNotificationRepository
                .findByStatusAndRetryCountLessThan("PENDING", MAX_RETRIES);
        for (PendingNotification pending : pendings) {
            try {
                User recipient = userRepository.findById(pending.getRecipientId())
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

                Set<Role> roles = Arrays.stream(pending.getRecipientRole().split(","))
                        .map(Role::valueOf)
                        .collect(Collectors.toSet());

                Recipe recipe = null;
                if (pending.getRecipeId() != null) {
                    recipe = recipeRepository.findById(pending.getRecipeId())
                            .orElseThrow(() -> new AppException(ErrorCode.RECIPE_NOT_EXISTED));
                }

                Rating rating = null;
                if (pending.getRatingId() != null) {
                    rating = ratingRepository.findById(pending.getRatingId())
                            .orElseThrow(() -> new AppException(ErrorCode.RATING_NOT_FOUND));
                }

                Notification notification = Notification.builder()
                        .recipient(recipient)
                        .recipientRole(roles)
                        .recipe(recipe)
                        .rating(rating)
                        .title(pending.getTitle())
                        .message(pending.getMessage())
                        .notificationType(NotificationType.valueOf(pending.getNotificationType()))
                        .createdAt(pending.getCreatedAt())
                        .build();

                String destination = "/topic/notifications/" + pending.getRecipientId();
                NotificationResponse response = notificationMapper.toNotificationResponse(notification);
                messagingTemplate.convertAndSend(destination, response);

                pending.setStatus("SENT");
            } catch (Exception e) {
                pending.setRetryCount(pending.getRetryCount() + 1);
                if (pending.getRetryCount() >= MAX_RETRIES) {
                    pending.setStatus("FAILED");
                    log.warn("Thông báo cho người dùng {} thất bại sau {} lần thử", pending.getRecipientId(), MAX_RETRIES);
                }
                log.error("Thử gửi lại thông báo thất bại: {}", e.getMessage());
            }
            pendingNotificationRepository.save(pending);
        }
    }

    // Lấy danh sách thông báo theo recipeId
    public List<Notification> getNotificationsEntityByRecipeId(Long recipeId) {
        return notificationRepository.findByRecipe_RecipeId(recipeId);
    }

    // Lấy tất cả thông báo
    public List<NotificationResponse> getAllNotifications() {
        return notificationMapper.toNotificationResponseList(notificationRepository.findAll());
    }

    // Cập nhật thông báo
    @Transactional
    public NotificationResponse updateNotification(Long notificationId, NotificationUpdateRequest request) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));

        notificationMapper.updateNotification(notification, request);
        Notification updatedNotification = notificationRepository.save(notification);

        return notificationMapper.toNotificationResponse(updatedNotification);
    }

    // Lấy danh sách thông báo của người nhận
    public List<NotificationResponse> getNotificationsByRecipientId(Long recipientId) {
        List<Notification> notifications = notificationRepository.findByRecipient_userId(recipientId);
        return notificationMapper.toNotificationResponseList(notifications);
    }

    public List<Notification> getNotificationsEntityByRecipientId(Long recipientId) {
        return notificationRepository.findByRecipient_userId(recipientId);
    }

    // Lấy thông báo theo ID
    public NotificationResponse getNotificationById(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));

        return notificationMapper.toNotificationResponse(notification);
    }

    // Xóa thông báo
    @Transactional
    public void deleteNotification(Long notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            throw new AppException(ErrorCode.NOTIFICATION_NOT_FOUND);
        }
        notificationRepository.deleteById(notificationId);
    }

    // Lấy danh sách thông báo theo recipeId
    public List<NotificationResponse> getNotificationsByRecipeId(Long recipeId) {
        List<Notification> notifications = notificationRepository.findByRecipe_RecipeId(recipeId);
        return notificationMapper.toNotificationResponseList(notifications);
    }

    // Cập nhật trạng thái đã đọc
    @Transactional
    public NotificationResponse markAsRead(Long notificationId, boolean isRead) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));

        notification.setRead(isRead);
        notificationRepository.save(notification);

        NotificationResponse response = notificationMapper.toNotificationResponse(notification);
        response.setRead(isRead);
        return response;
    }

    // Tạo thông báo khi người dùng đăng ký
    @Transactional
    public NotificationResponse createUserRegistrationNotification(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Tạo thông báo cho người dùng
        Notification userNotification = Notification.builder()
                .recipient(user)
                .recipientRole(user.getRoles())
                .title("Chào mừng bạn!")
                .message("Chào mừng bạn đến với YummyDaily!")
                .notificationType(NotificationType.WELCOME_MESSAGE)
                .createdAt(LocalDateTime.now())
                .build();
        Notification savedUserNotification = notificationRepository.save(userNotification);

        // Gửi thông báo cho người dùng qua WebSocket
        NotificationCreationRequest userRequest = NotificationCreationRequest.builder()
                .recipientId(user.getUserId())
                .recipientRole(user.getRoles())
                .title("Chào mừng bạn!")
                .message("Chào mừng bạn đến với YummyDaily!")
                .notificationType(NotificationType.WELCOME_MESSAGE)
                .createdAt(LocalDateTime.now())
                .build();
        sendNotificationToRecipient(savedUserNotification, userRequest);

        // Tạo thông báo cho Admin
        List<User> admins = userRepository.findAllByRolesContaining(Role.ADMIN);
        for (User admin : admins) {
            Notification adminNotification = Notification.builder()
                    .recipient(admin)
                    .recipientRole(admin.getRoles())
                    .title("Người dùng mới đăng ký")
                    .message("Có người dùng mới đăng ký: " + user.getUsername())
                    .notificationType(NotificationType.NEW_USER_REGISTERED)
                    .createdAt(LocalDateTime.now())
                    .build();
            Notification savedAdminNotification = notificationRepository.save(adminNotification);

            NotificationCreationRequest adminRequest = NotificationCreationRequest.builder()
                    .recipientId(admin.getUserId())
                    .recipientRole(admin.getRoles())
                    .title("Người dùng mới đăng ký")
                    .message("Có người dùng mới đăng ký: " + user.getUsername())
                    .notificationType(NotificationType.NEW_USER_REGISTERED)
                    .createdAt(LocalDateTime.now())
                    .build();
            sendNotificationToRecipient(savedAdminNotification, adminRequest);
        }

        return notificationMapper.toNotificationResponse(savedUserNotification);
    }
}