package com.example.YummyDaily.controller;

import com.example.YummyDaily.dto.request.CategoryCreationRequest;
import com.example.YummyDaily.dto.request.CategoryUpdateRequest;
import com.example.YummyDaily.dto.response.ApiResponse;
import com.example.YummyDaily.dto.response.CategoryResponse;
import com.example.YummyDaily.entity.Category;
import com.example.YummyDaily.mapper.CategoryMapper;
import com.example.YummyDaily.service.CategoryService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.data.domain.Page;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/categories")
public class CategoryController {
    CategoryService categoryService;
    CategoryMapper categoryMapper;

    // 1️⃣ Tạo danh mục mới - POST trả về 201 Created
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@RequestBody @Valid CategoryCreationRequest request) {
        CategoryResponse category = categoryService.createCategory(request);
        ApiResponse<CategoryResponse> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Tạo danh mục thành công");
        apiResponse.setResult(category);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED); // 201
    }

    // 2️⃣ Lấy danh sách danh mục - GET trả về 200 OK
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories() {
        List<Category> categories = categoryService.getCategories();
        List<CategoryResponse> categoryResponses = categories.stream()
                .map(categoryMapper::toCategoryResponse)
                .toList();
        ApiResponse<List<CategoryResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Lấy danh mục thành công");
        apiResponse.setResult(categoryResponses);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK); // 200
    }

    // 3️⃣ Lấy danh mục theo ID - GET trả về 200 OK
    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategory(@PathVariable Long categoryId) {
        Category category = categoryService.getCategory(categoryId);
        ApiResponse<CategoryResponse> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Lấy danh mục thành công");
        apiResponse.setResult(categoryMapper.toCategoryResponse(category));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK); // 200
    }

    // 4️⃣ Xóa danh mục theo ID - DELETE trả về 204 No Content
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<String>> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);

        ApiResponse<String> response = new ApiResponse<>();
        response.setMessage("Xóa danh mục thành công");
        response.setResult("Success");

        return new ResponseEntity<>(response, HttpStatus.OK); // Trả về 200 OK với body
    }


    // 5️⃣ Cập nhật danh mục theo ID - PUT trả về 200 OK
    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Long categoryId,
            @RequestBody @Valid CategoryUpdateRequest request
    ) {
        CategoryResponse updatedCategory = categoryService.updateCategory(categoryId, request);
        ApiResponse<CategoryResponse> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Cập nhật danh mục thành công");
        apiResponse.setResult(updatedCategory);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK); // 200
    }

    // 6️⃣ Cập nhật từng phần danh mục theo ID - PATCH trả về 200 OK
    @PatchMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> patchCategory(
            @PathVariable Long categoryId,
            @RequestBody @Valid CategoryUpdateRequest request
    ) {
        CategoryResponse updatedCategory = categoryService.patchCategory(categoryId, request);
        ApiResponse<CategoryResponse> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Cập nhật danh mục thành công");
        apiResponse.setResult(updatedCategory);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK); // 200
    }

    // 7️⃣ Tìm kiếm danh mục theo từ khóa - GET trả về 200 OK
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> searchCategories(@RequestParam String keyword) {
        List<CategoryResponse> categoryResponses = categoryService.searchCategories(keyword);
        ApiResponse<List<CategoryResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Tìm kiếm danh mục thành công");
        apiResponse.setResult(categoryResponses);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK); // 200
    }

    // 8️⃣ Lấy danh mục phân trang - GET trả về 200 OK
    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<Page<CategoryResponse>>> getPagedCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<CategoryResponse> result = categoryService.getPagedCategories(page, size);
        ApiResponse<Page<CategoryResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Lấy danh mục phân trang thành công");
        apiResponse.setResult(result);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK); // 200
    }

    // 9️⃣ Tìm kiếm danh mục phân trang - GET trả về 200 OK
    @GetMapping("/search/paged")
    public ResponseEntity<ApiResponse<Page<CategoryResponse>>> searchPagedCategories(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<CategoryResponse> result = categoryService.searchPagedCategories(keyword, page, size);
        ApiResponse<Page<CategoryResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Tìm kiếm danh mục phân trang thành công");
        apiResponse.setResult(result);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK); // 200
    }
}