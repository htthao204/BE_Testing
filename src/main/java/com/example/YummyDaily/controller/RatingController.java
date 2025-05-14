//package com.example.YummyDaily.controller;
//
//import com.example.YummyDaily.dto.request.RatingCreationRequest;
//import com.example.YummyDaily.dto.request.RatingUpdateRequest;
//import com.example.YummyDaily.dto.response.ApiResponse;
//import com.example.YummyDaily.dto.response.RatingResponse;
//import com.example.YummyDaily.service.RatingService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/ratings")
//@RequiredArgsConstructor
//public class RatingController {
//    private final RatingService ratingService;
//
//    // Lấy tất cả đánh giá - GET trả về 200 OK
//    @GetMapping
//    public ResponseEntity<ApiResponse<List<RatingResponse>>> getAllRatings() {
//        List<RatingResponse> responses = ratingService.getAllRating();
//        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy tất cả đánh giá thành công", responses));
//    }
//
//    // Tạo đánh giá mới - POST trả về 201 Created
//    @PostMapping
//    public ResponseEntity<ApiResponse<RatingResponse>> createRating(@RequestBody @Valid RatingCreationRequest request) {
//        RatingResponse response = ratingService.createRating(request);
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(new ApiResponse<>(201, "Đánh giá đã được tạo thành công", response));
//    }
//
//    // Lấy danh sách đánh giá theo Recipe ID - GET trả về 200 OK
//    @GetMapping("/recipe/{recipeId}")
//    public ResponseEntity<ApiResponse<List<RatingResponse>>> getRatingsByRecipeId(@PathVariable Long recipeId) {
//        List<RatingResponse> responses = ratingService.getRatingsByRecipeId(recipeId);
//        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách đánh giá theo Recipe ID thành công", responses));
//    }
//
//    // Lấy danh sách đánh giá theo User ID - GET trả về 200 OK
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<ApiResponse<List<RatingResponse>>> getRatingsByUserId(@PathVariable Long userId) {
//        List<RatingResponse> responses = ratingService.getRatingsByUserId(userId);
//        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách đánh giá theo User ID thành công", responses));
//    }
//
//    // Cập nhật toàn bộ đánh giá - PUT trả về 200 OK
//    @PutMapping("/{ratingId}/user/{userId}")
//    public ResponseEntity<ApiResponse<RatingResponse>> updateRating(
//            @PathVariable Long ratingId,
//            @Valid @RequestBody RatingUpdateRequest request,
//            @PathVariable Long userId) {
//        RatingResponse response = ratingService.updateRating(ratingId, request, userId);
//        return ResponseEntity.ok(new ApiResponse<>(200, "Cập nhật đánh giá thành công", response));
//    }
//
//    // Cập nhật từng phần đánh giá - PATCH trả về 200 OK
//    @PatchMapping("/{ratingId}/user/{userId}")
//    public ResponseEntity<ApiResponse<RatingResponse>> patchRating(
//            @PathVariable Long ratingId,
//            @Valid @RequestBody RatingUpdateRequest request,
//            @PathVariable Long userId) {
//        RatingResponse response = ratingService.patchRating(ratingId, request, userId);
//        return ResponseEntity.ok(new ApiResponse<>(200, "Cập nhật đánh giá thành công", response));
//    }
//
//    // Xóa đánh giá - DELETE trả về 204 No Content
//    @DeleteMapping("/{ratingId}/user/{userId}")
//    public ResponseEntity<ApiResponse<String>> deleteRating(@PathVariable Long ratingId, @PathVariable Long userId) {
//        ratingService.deleteRating(ratingId, userId);
//
//        ApiResponse<String> response = new ApiResponse<>();
//        response.setMessage("Xóa đánh giá thành công");
//        response.setResult("Success");
//
//        return ResponseEntity.ok(response); // 200 OK với body
//    }
//    // Lấy chi tiết đánh giá theo Rating ID - GET trả về 200 OK
//    @GetMapping("/{ratingId}")
//    public ResponseEntity<ApiResponse<RatingResponse>> getRatingById(@PathVariable Long ratingId) {
//        RatingResponse response = ratingService.getRatingById(ratingId);
//        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy đánh giá theo ID thành công", response));
//    }
//
//}
package com.example.YummyDaily.controller;

import com.example.YummyDaily.dto.request.RatingCreationRequest;
import com.example.YummyDaily.dto.request.RatingUpdateRequest;
import com.example.YummyDaily.dto.response.ApiResponse;
import com.example.YummyDaily.dto.response.RatingResponse;
import com.example.YummyDaily.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ratings")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    // Lấy tất cả đánh giá (không phân trang) - GET trả về 200 OK
    @GetMapping
    public ResponseEntity<ApiResponse<List<RatingResponse>>> getAllRatings() {
        List<RatingResponse> responses = ratingService.getAllRating();
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy tất cả đánh giá thành công", responses));
    }

    // Lấy tất cả đánh giá (phân trang) - GET trả về 200 OK
    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<Page<RatingResponse>>> getAllRatingsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<RatingResponse> responses = ratingService.getAllRatingPage(page, size);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách đánh giá phân trang thành công", responses));
    }

    // Tạo đánh giá mới - POST trả về 201 Created
    @PostMapping
    public ResponseEntity<ApiResponse<RatingResponse>> createRating(@RequestBody @Valid RatingCreationRequest request) {
        RatingResponse response = ratingService.createRating(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(201, "Đánh giá đã được tạo thành công", response));
    }

    // Lấy chi tiết đánh giá theo Rating ID - GET trả về 200 OK
    @GetMapping("/{ratingId}")
    public ResponseEntity<ApiResponse<RatingResponse>> getRatingById(@PathVariable Long ratingId) {
        RatingResponse response = ratingService.getRatingById(ratingId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy đánh giá theo ID thành công", response));
    }

    // Lấy danh sách đánh giá theo Recipe ID (không phân trang) - GET trả về 200 OK
    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<ApiResponse<List<RatingResponse>>> getRatingsByRecipeId(@PathVariable Long recipeId) {
        List<RatingResponse> responses = ratingService.getRatingsByRecipeId(recipeId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách đánh giá theo Recipe ID thành công", responses));
    }

    // Lấy danh sách đánh giá theo Recipe ID (phân trang) - GET trả về 200 OK
    @GetMapping("/recipe/{recipeId}/paged")
    public ResponseEntity<ApiResponse<Page<RatingResponse>>> getRatingsByRecipePaged(
            @PathVariable Long recipeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<RatingResponse> responses = ratingService.getRatingsByRecipePaged(recipeId, page, size);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách đánh giá theo Recipe ID phân trang thành công", responses));
    }

    // Lấy danh sách đánh giá theo User ID (không phân trang) - GET trả về 200 OK
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<RatingResponse>>> getRatingsByUserId(@PathVariable Long userId) {
        List<RatingResponse> responses = ratingService.getRatingsByUserId(userId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách đánh giá theo User ID thành công", responses));
    }

    // Lấy danh sách đánh giá theo User ID (phân trang) - GET trả về 200 OK
    @GetMapping("/user/{userId}/paged")
    public ResponseEntity<ApiResponse<Page<RatingResponse>>> getRatingsByUserPaged(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<RatingResponse> responses = ratingService.getRatingsByUserPaged(userId, page, size);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách đánh giá theo User ID phân trang thành công", responses));
    }

    // Cập nhật toàn bộ đánh giá - PUT trả về 200 OK
    @PutMapping("/{ratingId}/user/{userId}")
    public ResponseEntity<ApiResponse<RatingResponse>> updateRating(
            @PathVariable Long ratingId,
            @Valid @RequestBody RatingUpdateRequest request,
            @PathVariable Long userId) {
        RatingResponse response = ratingService.updateRating(ratingId, request, userId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Cập nhật đánh giá thành công", response));
    }

    // Cập nhật từng phần đánh giá - PATCH trả về 200 OK
    @PatchMapping("/{ratingId}/user/{userId}")
    public ResponseEntity<ApiResponse<RatingResponse>> patchRating(
            @PathVariable Long ratingId,
            @Valid @RequestBody RatingUpdateRequest request,
            @PathVariable Long userId) {
        RatingResponse response = ratingService.patchRating(ratingId, request, userId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Cập nhật đánh giá thành công", response));
    }

    // Xóa đánh giá - DELETE trả về 200 OK
    @DeleteMapping("/{ratingId}/user/{userId}")
    public ResponseEntity<ApiResponse<String>> deleteRating(@PathVariable Long ratingId, @PathVariable Long userId) {
        ratingService.deleteRating(ratingId, userId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Xóa đánh giá thành công", "Success"));
    }
}