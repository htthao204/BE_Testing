package com.example.YummyDaily.service;

import com.example.YummyDaily.dto.request.UserFavoriteUpdateRequest;
import com.example.YummyDaily.dto.request.UserFavoriteCreationRequest;
import com.example.YummyDaily.dto.response.UserFavoriteResponse;
import com.example.YummyDaily.entity.Recipe;
import com.example.YummyDaily.entity.User;
import com.example.YummyDaily.entity.UserFavorite;
import com.example.YummyDaily.exception.AppException;
import com.example.YummyDaily.exception.ErrorCode;
import com.example.YummyDaily.mapper.UserFavoriteMapper;
import com.example.YummyDaily.repository.RecipeRepository;
import com.example.YummyDaily.repository.UserFavoriteRepository;
import com.example.YummyDaily.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//@Slf4j
//public class UserFavoriteService {
//    UserFavoriteRepository userFavoriteRepository;
//    UserRepository userRepository;
//    RecipeRepository recipeRepository;
//    UserFavoriteMapper userFavoriteMapper;
//
////    @Transactional
////    public UserFavoriteResponse create(UserFavoriteCreationRequest request) {
////        System.out.println("Recipe ID: " + request.getRecipeId());
////
////        User user = userRepository.findById(request.getUserId())
////                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
////
////        Recipe recipe = recipeRepository.findById(request.getRecipeId())
////                .orElseThrow(() -> new AppException(ErrorCode.RECIPE_NOT_EXISTED));
////
////        if (userFavoriteRepository.existsByUserAndRecipe(user, recipe)) {
////            throw new AppException(ErrorCode.RECIPE_EXISTED);
////        }
////
////        UserFavorite favorite = toEntity(user, recipe);
////        UserFavorite saved = userFavoriteRepository.save(favorite);
////
////        // Dùng Builder để tạo UserFavoriteResponse
////        return UserFavoriteResponse.builder()
////                .userFavoriteId(saved.getUserFavoriteId())
////                .userId(saved.getUser().getUserId())
////                .recipeId(saved.getRecipe().getRecipeId())
////                .isActive(saved.getIsActive())
////                .build();
////    }
//public UserFavoriteResponse create(UserFavoriteCreationRequest request) {
//    // Kiểm tra sự tồn tại của người dùng và công thức
//    User user = userRepository.findById(request.getUserId())
//            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
//
//    Recipe recipe = recipeRepository.findById(request.getRecipeId())
//            .orElseThrow(() -> new AppException(ErrorCode.RECIPE_NOT_EXISTED));
//
//    // Kiểm tra xem người dùng đã yêu thích công thức này chưa
//    if (userFavoriteRepository.existsByUserAndRecipe(user, recipe)) {
//        throw new AppException(ErrorCode.RECIPE_EXISTED);
//    }
//
//    // Tạo và lưu yêu thích mới
//    UserFavorite favorite = toEntity(user, recipe);
//    UserFavorite saved = userFavoriteRepository.save(favorite);
//
//    // Trả về phản hồi với thông tin yêu thích đã lưu
//    return UserFavoriteResponse.builder()
//            .userFavoriteId(saved.getUserFavoriteId())
//            .userId(saved.getUser().getUserId())
//            .recipeId(saved.getRecipe().getRecipeId())
//            .isActive(saved.getIsActive())
//            .build();
//}
//
//
//    private UserFavorite toEntity(User user, Recipe recipe) {
//        UserFavorite favorite = new UserFavorite();
//        favorite.setUser(user);
//        favorite.setRecipe(recipe);
//        favorite.setIsActive(true);
//        return favorite;
//    }
//
//
//    @Transactional
//    public UserFavoriteResponse update(Long id, UserFavoriteUpdateRequest request) {
//        UserFavorite favorite = userFavoriteRepository.findById(id)
//                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
//
//        User user = userRepository.findById(request.getUserId())
//                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
//
//        Recipe recipe = recipeRepository.findById(request.getRecipeId())
//                .orElseThrow(() -> new AppException(ErrorCode.RECIPE_NOT_EXISTED));
//
//        userFavoriteMapper.updateEntity(favorite, request, user, recipe);
//        return userFavoriteMapper.toResponse(userFavoriteRepository.save(favorite));
//    }
//    private UserFavoriteResponse toResponse(Long userId, Long recipeId) {
//        UserFavoriteResponse favoriteResponse = new UserFavoriteResponse();
//        favoriteResponse.setUserId(userId);
//        favoriteResponse.setRecipeId(recipeId);
//        favoriteResponse.setIsActive(true);  // Hoặc lấy từ UserFavorite
//        return favoriteResponse;
//    }
//
//    @Transactional(readOnly = true)
//    public List<UserFavoriteResponse> getAllByUser(Long userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
//
//        // Dùng phương thức toResponse thủ công
//        return userFavoriteRepository.findAllByUser(user)
//                .stream()
//                .map(userFavorite -> toResponse(userFavorite.getUser().getUserId(), userFavorite.getRecipe().getRecipeId()))
//                .collect(Collectors.toList());
//    }
//
//    @Transactional(readOnly = true)
//    public UserFavoriteResponse getById(Long userFavoriteId) {
//        // Tìm kiếm UserFavorite theo userFavoriteId
//        UserFavorite userFavorite = userFavoriteRepository.findById(userFavoriteId)
//                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
//
//        // Trả về UserFavoriteResponse với thông tin lấy từ UserFavorite
//        return UserFavoriteResponse.builder()
//                .userFavoriteId(userFavorite.getUserFavoriteId())
//                .userId(userFavorite.getUser().getUserId())  // Lấy userId từ đối tượng User liên kết
//                .recipeId(userFavorite.getRecipe().getRecipeId())  // Lấy recipeId từ đối tượng Recipe liên kết
//                .isActive(userFavorite.getIsActive())
//                .build();
//    }
//    @Transactional(readOnly = true)
//    public List<UserFavoriteResponse> getAll() {
//        // Tìm kiếm UserFavorite theo userFavoriteId
//        List<UserFavorite> userFavorite = userFavoriteRepository.findAll();
//
//        // Trả về UserFavoriteResponse với thông tin lấy từ UserFavorite
//        return userFavoriteMapper.toResponseList(userFavorite);
//    }
//
//
//    @Transactional
//    public void delete(Long id) {
//        if (!userFavoriteRepository.existsById(id)) {
//            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND);
//        }
//        userFavoriteRepository.deleteById(id);
//    }
//}
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserFavoriteService {
    UserFavoriteRepository userFavoriteRepository;
    UserRepository userRepository;
    RecipeRepository recipeRepository;
    UserFavoriteMapper userFavoriteMapper;

    public UserFavoriteResponse create(UserFavoriteCreationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Recipe recipe = recipeRepository.findById(request.getRecipeId())
                .orElseThrow(() -> new AppException(ErrorCode.RECIPE_NOT_EXISTED));

        if (userFavoriteRepository.existsByUserAndRecipe(user, recipe)) {
            throw new AppException(ErrorCode.RECIPE_EXISTED);
        }

        UserFavorite favorite = userFavoriteMapper.toEntity(request);
        favorite.setUser(user);
        favorite.setRecipe(recipe);
        UserFavorite saved = userFavoriteRepository.save(favorite);

        return userFavoriteMapper.toResponse(saved);
    }

//    private UserFavorite toEntity(User user, Recipe recipe) {
//        return UserFavorite.builder()
//                .user(user)
//                .recipe(recipe)
//                .isActive(true)
//                .build();
//    }

    @Transactional
    public UserFavoriteResponse update(Long id, UserFavoriteUpdateRequest request) {
        UserFavorite favorite = userFavoriteRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Recipe recipe = recipeRepository.findById(request.getRecipeId())
                .orElseThrow(() -> new AppException(ErrorCode.RECIPE_NOT_EXISTED));

        userFavoriteMapper.updateEntity(favorite, request, user, recipe);

        return userFavoriteMapper.toResponse(userFavoriteRepository.save(favorite));
    }

    @Transactional(readOnly = true)
    public List<UserFavoriteResponse> getAllByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userFavoriteRepository.findAllByUser(user)
                .stream()
                .map(userFavoriteMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserFavoriteResponse getById(Long userFavoriteId) {
        UserFavorite userFavorite = userFavoriteRepository.findById(userFavoriteId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        return userFavoriteMapper.toResponse(userFavorite);
    }

    @Transactional(readOnly = true)
    public List<UserFavoriteResponse> getAll() {
        return userFavoriteRepository.findAll()
                .stream()
                .map(userFavoriteMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        if (!userFavoriteRepository.existsById(id)) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        userFavoriteRepository.deleteById(id);
    }
}
