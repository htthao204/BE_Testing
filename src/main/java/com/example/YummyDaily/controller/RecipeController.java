package com.example.YummyDaily.controller;

import com.example.YummyDaily.dto.request.RecipeCreationRequest;
import com.example.YummyDaily.dto.request.RecipeUpdateRequest;
import com.example.YummyDaily.dto.request.SearchRecipeRequest;
import com.example.YummyDaily.dto.response.ApiResponse;
import com.example.YummyDaily.dto.response.RecipeResponse;
import com.example.YummyDaily.enums.StateRecipe;
import com.example.YummyDaily.exception.ErrorCode;
import com.example.YummyDaily.service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
@Slf4j
public class RecipeController {
    private final RecipeService recipeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RecipeResponse>>> getAllRecipes() {
        List<RecipeResponse> recipes = recipeService.getRecipes();
        ApiResponse<List<RecipeResponse>> response = ApiResponse.<List<RecipeResponse>>builder()
                .code(ErrorCode.SUCCESS.getCode()) // 1000
                .message("Lấy danh sách công thức thành công")
                .result(recipes)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response); // 200 OK
    }

    @GetMapping("/ingredient/search")
    public ResponseEntity<ApiResponse<List<RecipeResponse>>> searchRecipesByIngredientName(@RequestParam String keyword) {
        log.info("Tìm kiếm công thức với nguyên liệu: {}", keyword);
        List<RecipeResponse> result = recipeService.searchRecipesByIngredientName(keyword);
        ApiResponse<List<RecipeResponse>> response = ApiResponse.<List<RecipeResponse>>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("Tìm kiếm công thức theo nguyên liệu thành công")
                .result(result)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response); // 200 OK
    }

    @GetMapping("/{recipeId:\\d+}")
    public ResponseEntity<ApiResponse<RecipeResponse>> getRecipeById(@PathVariable Long recipeId) {
        RecipeResponse recipe = recipeService.getRecipe(recipeId);
        ApiResponse<RecipeResponse> response = ApiResponse.<RecipeResponse>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("Lấy công thức thành công")
                .result(recipe)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response); // 200 OK
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RecipeResponse>> createRecipe(@RequestBody @Valid RecipeCreationRequest request) {
        RecipeResponse createdRecipe = recipeService.createRecipe(request);
        ApiResponse<RecipeResponse> response = ApiResponse.<RecipeResponse>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("Thêm công thức mới thành công")
                .result(createdRecipe)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response); // 201 Created
    }

    @PutMapping("/{recipeId}")
    public ResponseEntity<ApiResponse<RecipeResponse>> updateRecipe(@PathVariable Long recipeId, @RequestBody @Valid RecipeCreationRequest request) {
        RecipeResponse updatedRecipe = recipeService.updateRecipe(recipeId, request);
        ApiResponse<RecipeResponse> response = ApiResponse.<RecipeResponse>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("Cập nhật công thức thành công")
                .result(updatedRecipe)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response); // 200 OK
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<RecipeResponse>>> getRecipesByCategory(@PathVariable Long categoryId) {
        List<RecipeResponse> recipes = recipeService.getRecipesByCategory(categoryId);
        ApiResponse<List<RecipeResponse>> response = ApiResponse.<List<RecipeResponse>>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("Lấy công thức theo danh mục thành công")
                .result(recipes)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response); // 200 OK
    }

    @DeleteMapping("/{recipeId}")
    public ResponseEntity<ApiResponse<Void>> deleteRecipe(@PathVariable Long recipeId) {
        recipeService.deleteRecipe(recipeId);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("Xóa công thức thành công")
                .build();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response); // 204 No Content
    }

    @PatchMapping("/{recipeId}/state")
    public ResponseEntity<ApiResponse<RecipeResponse>> updateRecipeState(@PathVariable Long recipeId, @RequestBody Map<String, String> request) {
        StateRecipe state = StateRecipe.valueOf(request.get("state"));
        RecipeResponse updatedRecipe = recipeService.patchState(recipeId, state);
        ApiResponse<RecipeResponse> response = ApiResponse.<RecipeResponse>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("Cập nhật trạng thái công thức thành công")
                .result(updatedRecipe)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response); // 200 OK
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<RecipeResponse>>> getRecipesByUserId(@PathVariable Long userId) {
        List<RecipeResponse> recipes = recipeService.getRecipesByUserId(userId);
        ApiResponse<List<RecipeResponse>> response = ApiResponse.<List<RecipeResponse>>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("Lấy danh sách công thức theo người dùng thành công")
                .result(recipes)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response); // 200 OK
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/search/admin")
    public ResponseEntity<ApiResponse<List<RecipeResponse>>> searchRecipesForAdmin(@RequestBody SearchRecipeRequest request) {
        List<RecipeResponse> result = recipeService.searchRecipesForAdmin(request);
        ApiResponse<List<RecipeResponse>> response = ApiResponse.<List<RecipeResponse>>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("Tìm kiếm công thức cho admin thành công")
                .result(result)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response); // 200 OK
    }

    @PostMapping("/search/user")
    public ResponseEntity<ApiResponse<List<RecipeResponse>>> searchRecipesForUser(@RequestBody SearchRecipeRequest request) {
        List<RecipeResponse> result = recipeService.searchRecipesForUser(request);
        ApiResponse<List<RecipeResponse>> response = ApiResponse.<List<RecipeResponse>>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("Tìm kiếm công thức cho user thành công")
                .result(result)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response); // 200 OK
    }

    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<Page<RecipeResponse>>> getPagedRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String categoryId,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        if (page < 0) {
            throw new IllegalArgumentException("Số trang phải lớn hơn hoặc bằng 0");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Kích thước trang phải từ 1 đến 100");
        }

        String[] sortParts = sort.split(",");
        String sortField = sortParts[0];
        Sort.Direction sortDirection = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Sort sortOrder = Sort.by(sortDirection, sortField);
        Pageable pageable = PageRequest.of(page, size, sortOrder);

        Long categoryIdLong = null;
        if (categoryId != null && !categoryId.equalsIgnoreCase("null")) {
            try {
                categoryIdLong = Long.parseLong(categoryId);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("categoryId không hợp lệ");
            }
        }

        Page<RecipeResponse> pagedRecipes = recipeService.getPagedRecipes(pageable, categoryIdLong);
        ApiResponse<Page<RecipeResponse>> response = ApiResponse.<Page<RecipeResponse>>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("Lấy công thức phân trang thành công")
                .result(pagedRecipes)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response); // 200 OK
    }

    @PatchMapping("/{recipeId}")
    public ResponseEntity<ApiResponse<RecipeResponse>> patchRecipe(@PathVariable Long recipeId, @RequestBody @Valid RecipeUpdateRequest request) {
        RecipeResponse patchedRecipe = recipeService.patchRecipe(recipeId, request);
        ApiResponse<RecipeResponse> response = ApiResponse.<RecipeResponse>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("Chỉnh sửa công thức thành công")
                .result(patchedRecipe)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response); // 200 OK
    }
    @GetMapping("/category/{categoryId}/paged")
    public ResponseEntity<ApiResponse<Page<RecipeResponse>>> getPagedRecipesByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "generatedDate,desc") String sort) {

        // Kiểm tra số trang và kích thước trang
        if (page < 0) {
            throw new IllegalArgumentException("Số trang phải lớn hơn hoặc bằng 0");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Kích thước trang phải từ 1 đến 100");
        }

        // Xử lý sắp xếp
        String[] sortParts = sort.split(",");
        String sortField = sortParts[0];
        Sort.Direction sortDirection = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Sort sortOrder = Sort.by(sortDirection, sortField);
        Pageable pageable = PageRequest.of(page, size, sortOrder);

        // Gọi service để lấy danh sách công thức theo categoryId
        Page<RecipeResponse> pagedRecipes = recipeService.getRecipesByCategoryId(categoryId, pageable);

        // Tạo phản hồi API
        ApiResponse<Page<RecipeResponse>> response = ApiResponse.<Page<RecipeResponse>>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("Lấy công thức phân trang theo danh mục thành công")
                .result(pagedRecipes)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response); // 200 OK
    }
}