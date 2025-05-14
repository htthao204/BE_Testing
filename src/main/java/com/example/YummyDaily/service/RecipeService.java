package com.example.YummyDaily.service;

import com.example.YummyDaily.dto.request.*;
import com.example.YummyDaily.dto.response.*;
import com.example.YummyDaily.entity.*;
import com.example.YummyDaily.enums.Difficultylevel;
import com.example.YummyDaily.enums.NotificationType;
import com.example.YummyDaily.enums.Role;
import com.example.YummyDaily.enums.StateRecipe;
import com.example.YummyDaily.exception.AppException;
import com.example.YummyDaily.exception.ErrorCode;
import com.example.YummyDaily.mapper.*;
import com.example.YummyDaily.repository.*;
import io.micrometer.common.util.StringUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RecipeService {
    IngredientRecipeRepository ingredientRecipeRepository;
    RecipeRepository recipeRepository;
    RecipeMapper recipeMapper;
    UserRepository userRepository;
    CategoryRepository categoryRepository;
    CookingStepRepository cookingStepRepository;
    CookingStepMapper cookingStepMapper;
    CookingStepService cookingStepService;
    IngredientRecipeService ingredientRecipeService;
    IngredientRecipeMapper ingredientRecipeMapper;
    IngredientsRepository ingredientsRepository;
    IngredientsMapper ingredientsMapper;
    IngredientsService ingredientsService;
    CategoryService categoryService;
    CategoryMapper categoryMapper;
    UserMapper userMapper;
    NotificationRepository notificationRepository;
    NotificationMapper notificationMapper;
    NotificationService notificationService;
    UserFavoriteService userFavoriteService;
    UserFavoriteMapper userFavoriteMapper;
    UserFavoriteRepository userFavoriteRepository;
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Transactional
    public RecipeResponse patchRecipe(Long recipeId, RecipeUpdateRequest request) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new AppException(ErrorCode.RECIPE_NOT_EXISTED));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Cho phép người tạo công thức hoặc admin cập nhật
        if (!recipe.getUser().getUserId().equals(currentUser.getUserId()) && !currentUser.getRoles().contains(Role.ADMIN)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Update only the fields provided in the request
        if (request.getRecipeName() != null && !request.getRecipeName().isBlank()) {
            if (!recipe.getRecipeName().equals(request.getRecipeName()) &&
                    recipeRepository.existsByRecipeName(request.getRecipeName())) {
                throw new AppException(ErrorCode.RECIPE_EXISTED);
            }
            recipe.setRecipeName(request.getRecipeName());
        }

        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            recipe.setDescription(request.getDescription());
        }

        if (request.getRecipeImage() != null && !request.getRecipeImage().isBlank()) {
            recipe.setRecipeImage(request.getRecipeImage());
        }

        if (request.getCookingTime() != null) {
            recipe.setCookingTime(request.getCookingTime());
        }

        if (request.getDifficultyLevel() != null) {
            recipe.setDifficultyLevel(request.getDifficultyLevel());
        }

        if (request.getView() != null) {
            recipe.setView(request.getView());
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
            recipe.setCategory(category);
        }

        if (request.getNumberOfPeople() != null) {
            recipe.setNumberOfPeople(request.getNumberOfPeople());
        }

        if (request.getGeneratedDate() != null) {
            recipe.setGeneratedDate(request.getGeneratedDate());
        }

        if (request.getIntroductionVideo() != null && !request.getIntroductionVideo().isBlank()) {
            recipe.setIntroductionVideo(request.getIntroductionVideo());
        }

        // Update cooking steps if provided
        if (request.getCookingStepCreationRequests() != null && !request.getCookingStepCreationRequests().isEmpty()) {
            cookingStepRepository.deleteByRecipe_RecipeId(recipeId);
            List<CookingStepResponse> cookingStepResponses = new ArrayList<>();
            for (CookingStepCreationRequest stepRequest : request.getCookingStepCreationRequests()) {
                CookingStep cookingStep = cookingStepMapper.toCookingStep(stepRequest);
                cookingStep.setRecipe(recipe);
                cookingStep = cookingStepRepository.save(cookingStep);
                cookingStepResponses.add(cookingStepMapper.toCookingStepResponse(cookingStep));
            }
        }

        // Update ingredients if provided
        if (request.getIngredientsCreationRequests() != null && !request.getIngredientsCreationRequests().isEmpty()) {
            ingredientRecipeRepository.deleteByRecipe_RecipeId(recipeId);
            List<IngredientRecipeResponse> ingredientRecipeResponses = new ArrayList<>();
            for (IngredientsCreationRequest ingredientRequest : request.getIngredientsCreationRequests()) {
                if (StringUtils.isBlank(ingredientRequest.getIngredientName())) {
                    throw new AppException(ErrorCode.INGREDIENT_NAME_REQUIRED);
                }

                Ingredients ingredients = ingredientsMapper.toIngredients(ingredientRequest);
                ingredients = ingredientsRepository.save(ingredients);

                IngredientRecipe ingredientRecipe = IngredientRecipe.builder()
                        .recipe(recipe)
                        .ingredient(ingredients)
                        .quantity(ingredientRequest.getAmount())
                        .build();

                ingredientRecipe = ingredientRecipeRepository.save(ingredientRecipe);
                ingredientRecipeResponses.add(ingredientRecipeMapper.toIngredientRecipeResponse(ingredientRecipe));
            }
        }

        // Set state to PENDING for review (trừ khi admin cập nhật)
        if (!currentUser.getRoles().contains(Role.ADMIN)) {
            recipe.setState(StateRecipe.PENDING);
        }

        Recipe updatedRecipe = recipeRepository.save(recipe);

        // Notify admins (nếu không phải admin cập nhật)
        if (!currentUser.getRoles().contains(Role.ADMIN)) {
            List<User> adminUsers = userRepository.findByRolesContaining(Role.ADMIN);
            for (User admin : adminUsers) {
                NotificationCreationRequest notificationRequest = NotificationCreationRequest.builder()
                        .recipientId(admin.getUserId())
                        .recipientRole(admin.getRoles())
                        .recipeId(recipeId)
                        .title("Công thức được cập nhật")
                        .message("Công thức '" + recipe.getRecipeName() + "' đã được cập nhật bởi " +
                                currentUser.getUsername() + " và đang chờ phê duyệt.")
                        .notificationType(NotificationType.RECIPE_UPDATED)
                        .createdAt(LocalDateTime.now())
                        .build();
                notificationService.createNotification(notificationRequest);
            }
        }

        return buildFullRecipeResponse(updatedRecipe);
    }
    public RecipeResponse getRecipe(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new AppException(ErrorCode.RECIPE_NOT_EXISTED));
        List<UserFavorite> userFavorites = userFavoriteRepository.findByRecipe_RecipeId(recipeId);
        recipe.setUserFavorites(userFavorites);
        RecipeResponse response = recipeMapper.toRecipeResponse(recipe);

        response.setCategoryResponse(getCategory(recipe.getCategory().getCategoryId()));
        response.setCookingStepResponses(cookingStepService.getStepsByRecipeId(recipe.getRecipeId()));
        response.setIngredientRecipeResponses(ingredientRecipeService.getIngredientsByRecipeId(recipe.getRecipeId()));
        response.setUserFavoriteResponses(userFavoriteMapper.toResponseList(userFavorites));
        List<NotificationResponse> notificationResponses = recipe.getNotifications().stream()
                .map(notificationMapper::toNotificationResponse)
                .collect(Collectors.toList());
        response.setNotifications(notificationResponses);

        return response;
    }

    private void notifyUser(User user, Recipe recipe, String title, String message, NotificationType type) {
        NotificationCreationRequest notificationRequest = NotificationCreationRequest.builder()
                .recipientId(user.getUserId())
                .recipientRole(user.getRoles())
                .recipeId(recipe != null ? recipe.getRecipeId() : null)
                .title(title)
                .message(message)
                .notificationType(type)
                .createdAt(LocalDateTime.now())
                .build();

        notificationService.createNotification(notificationRequest);
    }

    public CategoryResponse getCategory(Long categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("Category ID cannot be null");
        }

        return categoryMapper.toCategoryResponse(
                categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED)));
    }

    public List<RecipeResponse> getRecipes() {
        List<Recipe> recipes = recipeRepository.findAll();

        // Tối ưu hóa truy vấn userFavorites
        List<Long> recipeIds = recipes.stream()
                .map(Recipe::getRecipeId)
                .collect(Collectors.toList());
        List<UserFavorite> allFavorites = userFavoriteRepository.findByRecipe_RecipeIdIn(recipeIds);
        Map<Long, List<UserFavorite>> favoritesByRecipeId = allFavorites.stream()
                .collect(Collectors.groupingBy(f -> f.getRecipe().getRecipeId()));

        return recipes.stream().map(recipe -> {
            // Lấy userFavorites từ map
            List<UserFavorite> userFavorites = favoritesByRecipeId.getOrDefault(recipe.getRecipeId(), List.of());

            // Tạo RecipeResponse
            RecipeResponse response = recipeMapper.toRecipeResponse(recipe);

            // Thêm thông tin danh mục
            if (recipe.getCategory() != null) {
                response.setCategoryResponse(categoryMapper.toCategoryResponse(recipe.getCategory()));
            }

            // Thêm thông tin bước nấu ăn và nguyên liệu
            response.setCookingStepResponses(cookingStepService.getStepsByRecipeId(recipe.getRecipeId()));
            response.setIngredientRecipeResponses(ingredientRecipeService.getIngredientsByRecipeId(recipe.getRecipeId()));
            response.setUserFavoriteResponses(userFavoriteMapper.toResponseList(userFavorites));

            // Thêm thông báo (kiểm tra null)
            List<NotificationResponse> notificationResponses = recipe.getNotifications() != null
                    ? recipe.getNotifications().stream()
                    .map(notificationMapper::toNotificationResponse)
                    .collect(Collectors.toList())
                    : List.of();
            response.setNotifications(notificationResponses);

            return response;
        }).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public RecipeResponse createRecipe(RecipeCreationRequest request) {
        if (recipeRepository.existsByRecipeName(request.getRecipeName())) {
            throw new AppException(ErrorCode.RECIPE_EXISTED);
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Recipe recipe = recipeMapper.toRecipe(request);
        recipe.setView(0);
        recipe.setState(StateRecipe.PENDING);
        recipe.setGeneratedDate(LocalDate.now());
        recipe.setCategory(category);
        recipe.setUser(user);

        recipe = recipeRepository.save(recipe);

        RecipeResponse recipeResponse = recipeMapper.toRecipeResponse(recipe);
        recipeResponse.setUserId(user.getUserId());
        recipeResponse.setCategoryId(category.getCategoryId());

        List<CookingStepResponse> cookingStepResponses = new ArrayList<>();
        if (request.getCookingStepCreationRequests() != null && !request.getCookingStepCreationRequests().isEmpty()) {
            for (CookingStepCreationRequest stepRequest : request.getCookingStepCreationRequests()) {
                CookingStep cookingStep = cookingStepMapper.toCookingStep(stepRequest);
                cookingStep.setRecipe(recipe);
                cookingStep = cookingStepRepository.save(cookingStep);

                CookingStepResponse stepResponse = cookingStepMapper.toCookingStepResponse(cookingStep);
                stepResponse.setRecipeResponse(recipeResponse);
                cookingStepResponses.add(stepResponse);
            }
        } else {
            throw new AppException(ErrorCode.COOKINGSTEP_REQUIRED);
        }
        recipeResponse.setCookingStepResponses(cookingStepResponses);

        List<IngredientRecipeResponse> ingredientRecipeResponses = new ArrayList<>();
        for (IngredientsCreationRequest ingredientRequest : request.getIngredientsCreationRequests()) {
            if (StringUtils.isBlank(ingredientRequest.getIngredientName())) {
                throw new AppException(ErrorCode.INGREDIENT_NAME_REQUIRED);
            }

            Ingredients ingredients = ingredientsMapper.toIngredients(ingredientRequest);
            ingredients = ingredientsRepository.save(ingredients);

            IngredientRecipe ingredientRecipe = IngredientRecipe.builder()
                    .recipe(recipe)
                    .ingredient(ingredients)
                    .quantity(ingredientRequest.getAmount())
                    .build();

            ingredientRecipe = ingredientRecipeRepository.save(ingredientRecipe);
            ingredientRecipeResponses.add(ingredientRecipeMapper.toIngredientRecipeResponse(ingredientRecipe));
        }

        List<IngredientRecipeResponse> updatedIngredientRecipeResponses = ingredientRecipeService.getIngredientsByRecipeId(recipe.getRecipeId());
        recipeResponse.setIngredientRecipeResponses(updatedIngredientRecipeResponses);

        List<User> adminUsers = userRepository.findByRolesContaining(Role.ADMIN);
        List<NotificationResponse> notificationResponses = new ArrayList<>();

        for (User admin : adminUsers) {
            NotificationCreationRequest notificationRequest = NotificationCreationRequest.builder()
                    .recipientId(admin.getUserId())
                    .recipientRole(admin.getRoles())
                    .recipeId(recipe.getRecipeId())
                    .title("Công thức mới chờ phê duyệt")
                    .message("Công thức '" + recipe.getRecipeName() + "' đã được tạo bởi " + user.getUsername() + " và đang chờ phê duyệt.")
                    .notificationType(NotificationType.NEW_RECIPE_SUBMISSION)
                    .createdAt(LocalDateTime.now())
                    .build();

            NotificationResponse notificationResponse = notificationService.createNotification(notificationRequest);
            notificationResponses.add(notificationResponse);
        }

        recipeResponse.setNotifications(notificationResponses);
        return recipeResponse;
    }

    public List<RecipeResponse> getRecipesByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<Recipe> recipes = recipeRepository.findByUser_UserId(userId);

        return recipes.stream()
                .map(recipe -> {
                    RecipeResponse response = recipeMapper.toRecipeResponse(recipe);
                    response.setCategoryResponse(categoryMapper.toCategoryResponse(recipe.getCategory()));
                    response.setUserId(recipe.getUser().getUserId());
                    response.setCookingStepResponses(cookingStepService.getStepsByRecipeId(recipe.getRecipeId()));
                    response.setIngredientRecipeResponses(ingredientRecipeService.getIngredientsByRecipeId(recipe.getRecipeId()));
                    List<NotificationResponse> notifications = notificationService.getNotificationsByRecipeId(recipe.getRecipeId());
                    response.setNotifications(notifications);
                    return response;
                })
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Transactional
    public RecipeResponse updateRecipe(Long recipeId, RecipeCreationRequest request) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new AppException(ErrorCode.RECIPE_NOT_EXISTED));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!recipe.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Cập nhật công thức
        recipeMapper.updateRecipe(recipe, request);
        recipe.setState(StateRecipe.PENDING); // Đặt lại trạng thái PENDING để chờ duyệt
        Recipe updatedRecipe = recipeRepository.save(recipe);

        // Gửi thông báo cho admin
        List<User> adminUsers = userRepository.findByRolesContaining(Role.ADMIN);
        for (User admin : adminUsers) {
            NotificationCreationRequest notificationRequest = NotificationCreationRequest.builder()
                    .recipientId(admin.getUserId())
                    .recipientRole(admin.getRoles())
                    .recipeId(recipeId)
                    .title("Công thức được cập nhật")
                    .message("Công thức '" + recipe.getRecipeName() + "' đã được cập nhật bởi " + currentUser.getUsername() + " và đang chờ phê duyệt.")
                    .notificationType(NotificationType.RECIPE_UPDATED)
                    .createdAt(LocalDateTime.now())
                    .build();
            notificationService.createNotification(notificationRequest);
        }

        return recipeMapper.toRecipeResponse(updatedRecipe);
    }

    @Transactional
    public void deleteRecipe(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new AppException(ErrorCode.RECIPE_NOT_EXISTED));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!recipe.getUser().getUserId().equals(currentUser.getUserId()) && !currentUser.getRoles().contains(Role.ADMIN)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Gửi thông báo cho admin (nếu không phải admin xóa)
        if (!currentUser.getRoles().contains(Role.ADMIN)) {
            List<User> adminUsers = userRepository.findByRolesContaining(Role.ADMIN);
            for (User admin : adminUsers) {
                NotificationCreationRequest notificationRequest = NotificationCreationRequest.builder()
                        .recipientId(admin.getUserId())
                        .recipientRole(admin.getRoles())
                        .recipeId(recipeId)
                        .title("Công thức bị xóa")
                        .message("Công thức '" + recipe.getRecipeName() + "' đã bị xóa bởi " + currentUser.getUsername() + ".")
                        .notificationType(NotificationType.GENERAL_ADMIN)
                        .createdAt(LocalDateTime.now())
                        .build();
                notificationService.createNotification(notificationRequest);
            }
        }

        recipeRepository.delete(recipe);
    }

    public List<RecipeResponse> getRecipesByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        List<Recipe> recipes = recipeRepository.findByCategory_CategoryId(categoryId);
        return recipes.stream()
                .map(recipeMapper::toRecipeResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public RecipeResponse patchState(Long recipeId, StateRecipe newState) {
        Objects.requireNonNull(newState, "Trạng thái không được null");

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new AppException(ErrorCode.RECIPE_NOT_EXISTED));

        recipe.setState(newState);
        Recipe updatedRecipe = recipeRepository.save(recipe);

        evictRecipeCache(recipeId);

        if (newState == StateRecipe.APPROVED || newState == StateRecipe.REJECTED) {
            User creator = recipe.getUser();
            String title;
            String message;
            NotificationType notificationType;

            if (newState == StateRecipe.APPROVED) {
                title = "Công thức đã được phê duyệt";
                message = "Công thức '" + recipe.getRecipeName() + "' (ID: " + recipe.getRecipeId() + ") đã được phê duyệt.";
                notificationType = NotificationType.RECIPE_APPROVED;
            } else {
                title = "Công thức bị từ chối";
                message = "Công thức '" + recipe.getRecipeName() + "' (ID: " + recipe.getRecipeId() + ") đã bị từ chối.";
                notificationType = NotificationType.RECIPE_REJECTED;
            }

            NotificationCreationRequest notificationRequest = NotificationCreationRequest.builder()
                    .recipientId(creator.getUserId())
                    .recipientRole(creator.getRoles())
                    .recipeId(recipe.getRecipeId())
                    .title(title)
                    .message(message)
                    .notificationType(notificationType)
                    .createdAt(LocalDateTime.now())
                    .build();

            notificationService.createNotification(notificationRequest);
        }

        return buildFullRecipeResponse(updatedRecipe);
    }

    private void validateAdminPermission() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!currentUser.getRoles().contains(Role.ADMIN)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    private RecipeResponse buildFullRecipeResponse(Recipe recipe) {
        RecipeResponse response = recipeMapper.toRecipeResponse(recipe);
        response.setCategoryResponse(categoryMapper.toCategoryResponse(recipe.getCategory()));
        response.setUserId(recipe.getUser().getUserId());
        response.setCookingStepResponses(cookingStepService.getStepsByRecipeId(recipe.getRecipeId()));
        response.setIngredientRecipeResponses(ingredientRecipeService.getIngredientsByRecipeId(recipe.getRecipeId()));
        List<NotificationResponse> notificationResponses = recipe.getNotifications().stream()
                .map(notificationMapper::toNotificationResponse)
                .collect(Collectors.toList());
        response.setNotifications(notificationResponses);
        return response;
    }

    private void evictRecipeCache(Long recipeId) {
        // Implement logic xóa cache tại đây nếu cần
    }

    public List<RecipeResponse> searchRecipesForAdmin(SearchRecipeRequest request) {
        List<Recipe> recipes = recipeRepository.advancedSearch(
                isBlank(request.getKeyword()) ? null : request.getKeyword(),
                request.getUserId(),
                request.getCategoryId(),
                request.getDifficultyLevel(),
                request.getMinCookingTime(),
                request.getMaxCookingTime(),
                request.getState()
        );
        return recipes.stream().map(this::buildFullRecipeResponse).toList();
    }

    public List<RecipeResponse> searchRecipesForUser(SearchRecipeRequest request) {
        List<Recipe> recipes = recipeRepository.advancedSearch(
                isBlank(request.getKeyword()) ? null : request.getKeyword(),
                request.getUserId(),
                request.getCategoryId(),
                request.getDifficultyLevel(),
                request.getMinCookingTime(),
                request.getMaxCookingTime(),
                request.getState()
        );
        return recipes.stream()
                .map(this::buildFullRecipeResponse)
                .collect(Collectors.toList());
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public List<RecipeResponse> searchRecipesByIngredientName(String ingredientName) {
        List<Recipe> recipes = recipeRepository.findByIngredientNameContainingIgnoreCase(ingredientName);
        return recipes.stream()
                .map(this::buildFullRecipeResponse)
                .toList();
    }


public Page<RecipeResponse> getPagedRecipes(Pageable pageable, Long categoryId) {
    Page<Recipe> recipePage;
    if (categoryId != null) {
        // Lọc công thức theo categoryId
        recipePage = recipeRepository.findByCategory_CategoryId(categoryId, pageable);
    } else {
        // Lấy tất cả công thức
        recipePage = recipeRepository.findAll(pageable);
    }

    // Tối ưu hóa truy vấn userFavorites
    List<Long> recipeIds = recipePage.getContent().stream()
            .map(Recipe::getRecipeId)
            .collect(Collectors.toList());
    List<UserFavorite> allFavorites = userFavoriteRepository.findByRecipe_RecipeIdIn(recipeIds);
    Map<Long, List<UserFavorite>> favoritesByRecipeId = allFavorites.stream()
            .collect(Collectors.groupingBy(f -> f.getRecipe().getRecipeId()));

    return recipePage.map(recipe -> {
        // Lấy userFavorites từ map
        List<UserFavorite> userFavorites = favoritesByRecipeId.getOrDefault(recipe.getRecipeId(), List.of());

        // Tạo RecipeResponse
        RecipeResponse response = recipeMapper.toRecipeResponse(recipe);

        // Thêm thông tin danh mục
        if (recipe.getCategory() != null) {
            response.setCategoryResponse(categoryMapper.toCategoryResponse(recipe.getCategory()));
        }

        // Thêm thông tin bước nấu ăn và nguyên liệu
        response.setCookingStepResponses(cookingStepService.getStepsByRecipeId(recipe.getRecipeId()));
        response.setIngredientRecipeResponses(ingredientRecipeService.getIngredientsByRecipeId(recipe.getRecipeId()));
        response.setUserFavoriteResponses(userFavoriteMapper.toResponseList(userFavorites));

        // Thêm thông báo (kiểm tra null)
        List<NotificationResponse> notificationResponses = recipe.getNotifications() != null
                ? recipe.getNotifications().stream()
                .map(notificationMapper::toNotificationResponse)
                .collect(Collectors.toList())
                : List.of();
        response.setNotifications(notificationResponses);

        return response;
    });
}
    public Page<RecipeResponse> getRecipesByCategoryId(Long categoryId, Pageable pageable) {
        // Kiểm tra xem categoryId có hợp lệ không
        if (categoryId == null) {
            throw new AppException(ErrorCode.CATEGORY_NOT_EXISTED);
        }

        // Lấy danh mục từ categoryId
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        // Lấy danh sách công thức theo categoryId với phân trang
        Page<Recipe> recipePage = recipeRepository.findByCategory_CategoryId(categoryId, pageable);

        // Tối ưu hóa truy vấn userFavorites
        List<Long> recipeIds = recipePage.getContent().stream()
                .map(Recipe::getRecipeId)
                .collect(Collectors.toList());
        List<UserFavorite> allFavorites = userFavoriteRepository.findByRecipe_RecipeIdIn(recipeIds);
        Map<Long, List<UserFavorite>> favoritesByRecipeId = allFavorites.stream()
                .collect(Collectors.groupingBy(f -> f.getRecipe().getRecipeId()));

        // Chuyển đổi danh sách công thức thành RecipeResponse
        return recipePage.map(recipe -> {
            // Lấy userFavorites từ map
            List<UserFavorite> userFavorites = favoritesByRecipeId.getOrDefault(recipe.getRecipeId(), List.of());

            // Tạo RecipeResponse
            RecipeResponse response = recipeMapper.toRecipeResponse(recipe);

            // Thêm thông tin danh mục
            response.setCategoryResponse(categoryMapper.toCategoryResponse(recipe.getCategory()));

            // Thêm thông tin bước nấu ăn và nguyên liệu
            response.setCookingStepResponses(cookingStepService.getStepsByRecipeId(recipe.getRecipeId()));
            response.setIngredientRecipeResponses(ingredientRecipeService.getIngredientsByRecipeId(recipe.getRecipeId()));
            response.setUserFavoriteResponses(userFavoriteMapper.toResponseList(userFavorites));

            // Thêm thông báo (kiểm tra null)
            List<NotificationResponse> notificationResponses = recipe.getNotifications() != null
                    ? recipe.getNotifications().stream()
                    .map(notificationMapper::toNotificationResponse)
                    .collect(Collectors.toList())
                    : List.of();
            response.setNotifications(notificationResponses);

            return response;
        });
    }
}