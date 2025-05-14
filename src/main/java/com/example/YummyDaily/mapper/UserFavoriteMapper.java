package com.example.YummyDaily.mapper;

import com.example.YummyDaily.dto.request.UserFavoriteUpdateRequest;
import com.example.YummyDaily.dto.request.UserFavoriteCreationRequest;
import com.example.YummyDaily.dto.response.CategoryResponse;
import com.example.YummyDaily.dto.response.UserFavoriteResponse;
import com.example.YummyDaily.entity.Category;
import com.example.YummyDaily.entity.Recipe;
import com.example.YummyDaily.entity.User;
import com.example.YummyDaily.entity.UserFavorite;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserFavoriteMapper {

    UserFavorite toEntity(UserFavoriteCreationRequest request);

    void updateEntity(@MappingTarget UserFavorite entity, UserFavoriteUpdateRequest request, User user, Recipe recipe);
    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "recipe.recipeId", target = "recipeId")
    List<UserFavoriteResponse> toResponseList(List<UserFavorite> entities);
    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "recipe.recipeId", target = "recipeId")
    UserFavoriteResponse toResponse(UserFavorite userFavorite);

    // Ánh xạ danh sách UserFavorite sang danh sách UserFavoriteResponse
    List<UserFavoriteResponse> toUserFavoriteResponses(List<UserFavorite> userFavorites);
}
