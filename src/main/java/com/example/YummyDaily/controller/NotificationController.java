package com.example.YummyDaily.controller;

import com.example.YummyDaily.dto.request.NotificationCreationRequest;
import com.example.YummyDaily.dto.request.NotificationUpdateRequest;
import com.example.YummyDaily.dto.response.ApiResponse;
import com.example.YummyDaily.dto.response.NotificationResponse;
import com.example.YummyDaily.entity.Notification;
import com.example.YummyDaily.service.NotificationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class NotificationController {
    NotificationService notificationService;
    SimpMessagingTemplate messagingTemplate;  // WebSocket messaging template

    // 1️⃣ Tạo mới thông báo
    @PostMapping
    public ApiResponse<NotificationResponse> createNotification(@RequestBody @Valid NotificationCreationRequest request) {
        NotificationResponse response = notificationService.createNotification(request);

        ApiResponse<NotificationResponse> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Tạo thông báo thành công");
        apiResponse.setResult(response);

        return apiResponse;
    }




    // 2️⃣ Cập nhật thông báo
    @PutMapping("/{notificationId}")
    public ApiResponse<NotificationResponse> updateNotification(@PathVariable Long notificationId,
                                                                @RequestBody @Valid NotificationUpdateRequest request) {
        ApiResponse<NotificationResponse> apiResponse = new ApiResponse<>();
        try {
            NotificationResponse response = notificationService.updateNotification(notificationId, request);
            apiResponse.setMessage("Cập nhật thông báo thành công");
            apiResponse.setResult(response);
        } catch (RuntimeException e) {
            apiResponse.setCode(100);
            apiResponse.setMessage("Không tìm thấy thông báo");
        }
        return apiResponse;
    }
    // 1️⃣ Tạo mới thông báo
//    @PostMapping
//    public ApiResponse<NotificationResponse> createNotification(@RequestBody @Valid NotificationCreationRequest request) {
//        NotificationResponse response = notificationService.createNotification(request);
//
//        ApiResponse<NotificationResponse> apiResponse = new ApiResponse<>();
//        apiResponse.setMessage("Tạo thông báo thành công");
//        apiResponse.setResult(response);
//
//        return apiResponse;
//    }
//
//    // 2️⃣ Cập nhật thông báo
//    @PutMapping("/{notificationId}")
//    public ApiResponse<NotificationResponse> updateNotification(@PathVariable Long notificationId,
//                                                                @RequestBody @Valid NotificationUpdateRequest request) {
//        ApiResponse<NotificationResponse> apiResponse = new ApiResponse<>();
//        try {
//            NotificationResponse response = notificationService.updateNotification(notificationId, request);
//            apiResponse.setMessage("Cập nhật thông báo thành công");
//            apiResponse.setResult(response);
//        } catch (RuntimeException e) {
//            apiResponse.setCode(100);
//            apiResponse.setMessage("Không tìm thấy thông báo");
//        }
//        return apiResponse;
//    }

    // 3️⃣ Lấy danh sách thông báo theo người nhận
    @GetMapping("/recipient/{recipientId}")
    public ApiResponse<List<NotificationResponse>> getNotificationsByRecipientId(@PathVariable Long recipientId) {
        List<NotificationResponse> responseList = notificationService.getNotificationsByRecipientId(recipientId);

        ApiResponse<List<NotificationResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Lấy danh sách thông báo thành công");
        apiResponse.setResult(responseList);

        return apiResponse;
    }
    // 7️⃣ Lấy tất cả thông báo
    @GetMapping()
    public ApiResponse<List<NotificationResponse>> getAllNotifications() {
        ApiResponse<List<NotificationResponse>> apiResponse = new ApiResponse<>();
        try {
            List<NotificationResponse> responseList = notificationService.getAllNotifications();
            apiResponse.setMessage("Lấy tất cả thông báo thành công");
            apiResponse.setResult(responseList);
        } catch (RuntimeException e) {
            apiResponse.setCode(100);
            apiResponse.setMessage("Lỗi khi lấy danh sách thông báo");
        }
        return apiResponse;
    }
    // 4️⃣ Lấy thông báo theo ID
    @GetMapping("/{notificationId}")
    public ApiResponse<NotificationResponse> getNotificationById(@PathVariable Long notificationId) {
        ApiResponse<NotificationResponse> apiResponse = new ApiResponse<>();
        try {
            NotificationResponse response = notificationService.getNotificationById(notificationId);
            apiResponse.setMessage("Lấy thông báo thành công");
            apiResponse.setResult(response);
        } catch (RuntimeException e) {
            apiResponse.setCode(100);
            apiResponse.setMessage("Thông báo không tồn tại");
        }
        return apiResponse;
    }

    // 5️⃣ Xóa thông báo
    @DeleteMapping("/{notificationId}")
    public ApiResponse<Void> deleteNotification(@PathVariable Long notificationId) {
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        try {
            notificationService.deleteNotification(notificationId);
            apiResponse.setMessage("Xóa thông báo thành công");
        } catch (RuntimeException e) {
            apiResponse.setCode(100);
            apiResponse.setMessage("Không tìm thấy thông báo để xóa");
        }
        return apiResponse;
    }
    // 6️⃣ Cập nhật trạng thái đã đọc/chưa đọc của thông báo
    @PatchMapping("/{notificationId}/status")
    public ApiResponse<NotificationResponse> updateNotificationStatus(
            @PathVariable Long notificationId,
            @RequestParam boolean isRead) {
        ApiResponse<NotificationResponse> apiResponse = new ApiResponse<>();
        try {
            // Gọi service để cập nhật trạng thái read của thông báo
            NotificationResponse notificationResponse = notificationService.markAsRead(notificationId, isRead);

            // Cập nhật phản hồi API
            apiResponse.setMessage("Cập nhật trạng thái thông báo thành công");
            apiResponse.setResult(notificationResponse);  // Trả về thông báo đã cập nhật
        } catch (RuntimeException e) {
            apiResponse.setCode(100);
            apiResponse.setMessage("Không tìm thấy thông báo");
        }
        return apiResponse;
    }


}
