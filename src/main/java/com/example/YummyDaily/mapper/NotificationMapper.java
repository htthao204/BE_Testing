package com.example.YummyDaily.mapper;

import com.example.YummyDaily.dto.request.NotificationCreationRequest;
import com.example.YummyDaily.dto.request.NotificationUpdateRequest;
import com.example.YummyDaily.dto.response.NotificationResponse;
import com.example.YummyDaily.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(source = "rating.ratingId", target = "ratingId")
    @Mapping(source = "recipe.recipeId", target = "recipeId")
    NotificationResponse toNotificationResponse(Notification notification);

    Notification toNotification(NotificationCreationRequest request);

    void updateNotification(@MappingTarget Notification notification, NotificationUpdateRequest request);

    List<NotificationResponse> toNotificationResponseList(List<Notification> notifications);

    List<Notification> toNotificationList(List<NotificationResponse> notificationResponses);
}
