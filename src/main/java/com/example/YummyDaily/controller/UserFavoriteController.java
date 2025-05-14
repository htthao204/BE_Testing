package com.example.YummyDaily.controller;

import com.example.YummyDaily.dto.request.UserFavoriteCreationRequest;
import com.example.YummyDaily.dto.request.UserFavoriteUpdateRequest;
import com.example.YummyDaily.dto.response.ApiResponse;
import com.example.YummyDaily.dto.response.UserFavoriteResponse;
import com.example.YummyDaily.service.UserFavoriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-favorites")
@RequiredArgsConstructor
public class UserFavoriteController {
    private final UserFavoriteService userFavoriteService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserFavoriteResponse>> create(@RequestBody @Valid UserFavoriteCreationRequest request) {
        UserFavoriteResponse response = userFavoriteService.create(request);
        return ResponseEntity.ok(new ApiResponse<>(1000, "Yêu thích đã được tạo thành công", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserFavoriteResponse>> update(@PathVariable Long id,
                                                                    @RequestBody @Valid UserFavoriteUpdateRequest request) {
        UserFavoriteResponse response = userFavoriteService.update(id, request);
        return ResponseEntity.ok(new ApiResponse<>(1000, "Cập nhật yêu thích thành công", response));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<UserFavoriteResponse>>> getAllByUser(@PathVariable Long userId) {
        List<UserFavoriteResponse> responses = userFavoriteService.getAllByUser(userId);
        return ResponseEntity.ok(new ApiResponse<>(1000, "Lấy danh sách yêu thích thành công", responses));
    }
    @GetMapping()
    public ResponseEntity<ApiResponse<List<UserFavoriteResponse>>> getAll() {
        List<UserFavoriteResponse> responses = userFavoriteService.getAll();
        return ResponseEntity.ok(new ApiResponse<>(1000, "Lấy danh sách yêu thích thành công", responses));
    }

    // Thêm phương thức mới để lấy thông tin chi tiết của một UserFavorite theo userFavoriteId
    @GetMapping("/{userFavoriteId}")
    public ResponseEntity<ApiResponse<UserFavoriteResponse>> getById(@PathVariable Long userFavoriteId) {
        UserFavoriteResponse response = userFavoriteService.getById(userFavoriteId);
        return ResponseEntity.ok(new ApiResponse<>(1000, "Lấy thông tin yêu thích thành công", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        userFavoriteService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(1000, "Xóa yêu thích thành công", "Deleted"));
    }
}
