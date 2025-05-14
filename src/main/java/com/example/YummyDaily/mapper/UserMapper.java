package com.example.YummyDaily.mapper;

import com.example.YummyDaily.dto.request.UserCreationRequest;
import com.example.YummyDaily.dto.request.UserUpdateRequest;
import com.example.YummyDaily.dto.response.NotificationResponse;
import com.example.YummyDaily.dto.response.UserResponse;
import com.example.YummyDaily.entity.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "username", source = "username")
    User toUser(UserCreationRequest request);

    // Ánh xạ từ User thành UserResponse, bao gồm cả status và userFavorites
    @Mapping(source = "userFavorites", target = "userFavoritesResponses")
    UserResponse toUserResponse(User user);

    // Cập nhật thông tin người dùng từ UserUpdateRequest
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);

    // Phương thức để bao gồm thông báo vào UserResponse
    default UserResponse toUserResponse(User user, List<NotificationResponse> notifications) {
        UserResponse userResponse = toUserResponse(user);
        userResponse.setNotifications(notifications);
        return userResponse;
    }

    // Phương thức ánh xạ từ danh sách User thành danh sách UserResponse
    List<UserResponse> toUserResponseList(List<User> users);
}
