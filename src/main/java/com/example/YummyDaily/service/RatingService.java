package com.example.YummyDaily.service;

import com.example.YummyDaily.dto.request.NotificationCreationRequest;
import com.example.YummyDaily.dto.request.RatingCreationRequest;
import com.example.YummyDaily.dto.request.RatingUpdateRequest;
import com.example.YummyDaily.dto.response.RatingResponse;
import com.example.YummyDaily.entity.Notification;
import com.example.YummyDaily.entity.Rating;
import com.example.YummyDaily.entity.Recipe;
import com.example.YummyDaily.entity.User;
import com.example.YummyDaily.enums.NotificationType;
import com.example.YummyDaily.enums.Role;
import com.example.YummyDaily.exception.AppException;
import com.example.YummyDaily.exception.ErrorCode;
import com.example.YummyDaily.mapper.RatingMapper;
import com.example.YummyDaily.repository.RatingRepository;
import com.example.YummyDaily.repository.RecipeRepository;
import com.example.YummyDaily.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RatingService {
    RatingRepository ratingRepository;
    UserRepository userRepository;
    RecipeRepository recipeRepository;
    RatingMapper ratingMapper;
    NotificationService notificationService;

    @Transactional
    public RatingResponse createRating(RatingCreationRequest request) {
        // 1. Lấy thông tin người dùng và công thức
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Recipe recipe = recipeRepository.findById(request.getRecipeId())
                .orElseThrow(() -> new AppException(ErrorCode.RECIPE_NOT_EXISTED));

        // 2. Tạo Rating
        Rating rating = ratingMapper.toRating(request);
        rating.setUser(user);
        rating.setRecipe(recipe);
        rating.setDate(LocalDate.now());
        Rating savedRating = ratingRepository.save(rating);

        // 3. Tạo thông báo cho người sở hữu công thức
        User creator = recipe.getUser();
        String title = "Công thức có đánh giá mới";
        String message = String.format(
                "Công thức '%s' của bạn vừa nhận được đánh giá %d sao từ người dùng %s",
                recipe.getRecipeName(), request.getRatingScore(), user.getUsername()
        );
        NotificationType notificationType = NotificationType.RATING_RECEIVED;

        NotificationCreationRequest notificationRequest = NotificationCreationRequest.builder()
                .recipientId(creator.getUserId())
                .recipientRole(creator.getRoles())
                .recipeId(recipe.getRecipeId())
                .ratingId(savedRating.getRatingId()) // Thêm ratingId
                .title(title)
                .message(message)
                .notificationType(notificationType)
                .createdAt(LocalDateTime.now())
                .build();

        notificationService.createNotification(notificationRequest);

        // 4. Trả về response
        RatingResponse response = ratingMapper.toRatingResponse(savedRating);
        response.setNotifications(notificationService.getNotificationsEntityByRecipientId(creator.getUserId()));
        return response;
    }
    @Transactional(readOnly = true)
    public RatingResponse getRatingById(Long ratingId) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new AppException(ErrorCode.RATING_NOT_FOUND));
        return ratingMapper.toRatingResponse(rating);
    }

    @Transactional
    public RatingResponse updateRating(Long ratingId, RatingUpdateRequest request, Long userId) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        checkPermission(user, rating);

        // Cập nhật thông tin đánh giá
        ratingMapper.updateRating(rating, request);
        Rating updatedRating = ratingRepository.save(rating);

        // Tạo thông báo cho người sở hữu công thức (tùy chọn)
        Recipe recipe = rating.getRecipe();
        User creator = recipe.getUser();
        String title = "Đánh giá công thức được cập nhật";
        String message = String.format(
                "Đánh giá cho công thức '%s' của bạn đã được cập nhật bởi người dùng %s",
                recipe.getRecipeName(), user.getUsername()
        );
        NotificationType notificationType = NotificationType.RATING_UPDATED;

        NotificationCreationRequest notificationRequest = NotificationCreationRequest.builder()
                .recipientId(creator.getUserId())
                .recipientRole(creator.getRoles())
                .recipeId(recipe.getRecipeId())
                .ratingId(ratingId)
                .title(title)
                .message(message)
                .notificationType(notificationType)
                .createdAt(LocalDateTime.now())
                .build();

        notificationService.createNotification(notificationRequest);

        return ratingMapper.toRatingResponse(updatedRating);
    }

    @Transactional
    public void deleteRating(Long ratingId, Long userId) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        checkPermission(user, rating);

        // Tạo thông báo cho người sở hữu công thức (tùy chọn)
        Recipe recipe = rating.getRecipe();
        User creator = recipe.getUser();
        String title = "Đánh giá công thức bị xóa";
        String message = String.format(
                "Đánh giá cho công thức '%s' của bạn đã bị xóa bởi %s",
                recipe.getRecipeName(),
                user.getRoles().contains(Role.ADMIN) ? "admin" : "người dùng " + user.getUsername()
        );
        NotificationType notificationType = NotificationType.RATING_DELETED;

        NotificationCreationRequest notificationRequest = NotificationCreationRequest.builder()
                .recipientId(creator.getUserId())
                .recipientRole(creator.getRoles())
                .recipeId(recipe.getRecipeId())
                .ratingId(ratingId)
                .title(title)
                .message(message)
                .notificationType(notificationType)
                .createdAt(LocalDateTime.now())
                .build();

        notificationService.createNotification(notificationRequest);

        ratingRepository.deleteById(ratingId);
    }

    @Transactional(readOnly = true)
    public List<RatingResponse> getRatingsByRecipeId(Long recipeId) {
        if (!recipeRepository.existsById(recipeId)) {
            throw new AppException(ErrorCode.RECIPE_NOT_EXISTED);
        }
        return ratingRepository.findByRecipe_RecipeId(recipeId)
                .stream()
                .map(ratingMapper::toRatingResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RatingResponse> getAllRating() {
        return ratingRepository.findAll()
                .stream()
                .map(ratingMapper::toRatingResponse)
                .collect(Collectors.toList());
    }
    @Transactional
    public RatingResponse patchRating(Long ratingId, RatingUpdateRequest request, Long userId) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new AppException(ErrorCode.RATING_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        checkPermission(user, rating);

        // Update only provided fields
        // Since ratingScore is a primitive float, check if it's non-zero or explicitly provided
        if (request.getRatingScore() > 0) { // Assuming 0 is not a valid score (since valid range is 1–5)
            if (request.getRatingScore() < 1 || request.getRatingScore() > 5) {
                throw new AppException(ErrorCode.RATING_SCORE_INVALID);
            }
            rating.setRatingScore(request.getRatingScore());
        }
        if (request.getRatingImage() != null && !request.getRatingImage().isBlank()) {
            rating.setRatingImage(request.getRatingImage());
        }
        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            rating.setDescription(request.getDescription());
        }

        Rating updatedRating = ratingRepository.save(rating);

        // Create notification for the recipe owner
        Recipe recipe = rating.getRecipe();
        User creator = recipe.getUser();
        String title = "Đánh giá công thức được cập nhật";
        String message = String.format(
                "Đánh giá cho công thức '%s' của bạn đã được cập nhật bởi người dùng %s",
                recipe.getRecipeName(), user.getUsername()
        );
        NotificationType notificationType = NotificationType.RATING_UPDATED;

        NotificationCreationRequest notificationRequest = NotificationCreationRequest.builder()
                .recipientId(creator.getUserId())
                .recipientRole(creator.getRoles())
                .recipeId(recipe.getRecipeId())
                .ratingId(ratingId)
                .title(title)
                .message(message)
                .notificationType(notificationType)
                .createdAt(LocalDateTime.now())
                .build();

        notificationService.createNotification(notificationRequest);

        return ratingMapper.toRatingResponse(updatedRating);
    }


    @Transactional(readOnly = true)
    public List<RatingResponse> getRatingsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        return ratingRepository.findByUser_UserId(userId)
                .stream()
                .map(ratingMapper::toRatingResponse)
                .collect(Collectors.toList());
    }

    private void checkPermission(User user, Rating rating) {
        if (user == null || rating == null ||
                (!user.getRoles().contains(Role.ADMIN) &&
                        !rating.getUser().getUserId().equals(user.getUserId()))) {
            throw new AppException(ErrorCode.PERMISSION_DENIED);
        }
    }

    @Transactional(readOnly = true)
    public Page<RatingResponse> getRatingsByRecipePaged(Long recipeId, int page, int size) {
        if (!recipeRepository.existsById(recipeId)) {
            throw new AppException(ErrorCode.RECIPE_NOT_EXISTED);
        }

        Pageable pageable = PageRequest.of(page, size);
        return ratingRepository.findByRecipe_RecipeId(recipeId, pageable)
                .map(ratingMapper::toRatingResponse);
    }

    @Transactional(readOnly = true)
    public Page<RatingResponse> getRatingsByUserPaged(Long userId, int page, int size) {
        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        Pageable pageable = PageRequest.of(page, size);
        return ratingRepository.findByUser_UserId(userId, pageable)
                .map(ratingMapper::toRatingResponse);
    }
    @Transactional(readOnly = true)
    public Page<RatingResponse> getAllRatingPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ratingRepository.findAll(pageable)
                .map(ratingMapper::toRatingResponse);
    }


}