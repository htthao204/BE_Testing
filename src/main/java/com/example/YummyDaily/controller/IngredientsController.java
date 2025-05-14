package com.example.YummyDaily.controller;

import com.example.YummyDaily.dto.request.IngredientsCreationRequest;
import com.example.YummyDaily.dto.request.IngredientsUpdateRequest;
import com.example.YummyDaily.dto.response.ApiResponse;
import com.example.YummyDaily.dto.response.IngredientsResponse;
import com.example.YummyDaily.service.IngredientsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ingredients")
public class IngredientsController {
    private final IngredientsService ingredientsService;

    @PostMapping
    public ApiResponse<IngredientsResponse> createIngredient(@RequestBody @Valid IngredientsCreationRequest request) {
        ApiResponse<IngredientsResponse> response = new ApiResponse<>();
        response.setMessage("Tạo nguyên liệu thành công");
        response.setResult(ingredientsService.createIngredient(request));
        return response;
    }

    @GetMapping
    public ApiResponse<List<IngredientsResponse>> getAllIngredients() {
        ApiResponse<List<IngredientsResponse>> response = new ApiResponse<>();
        response.setMessage("Lấy danh sách nguyên liệu thành công");
        response.setResult(ingredientsService.getAllIngredients());
        return response;
    }

    @GetMapping("/{id}")
    public ApiResponse<IngredientsResponse> getIngredient(@PathVariable Long id) {
        ApiResponse<IngredientsResponse> response = new ApiResponse<>();
        response.setMessage("Lấy nguyên liệu thành công");
        response.setResult(ingredientsService.getIngredientById(id));
        return response;
    }

    @PutMapping("/{id}")
    public ApiResponse<IngredientsResponse> updateIngredient(@PathVariable Long id, @RequestBody @Valid IngredientsUpdateRequest request) {
        ApiResponse<IngredientsResponse> response = new ApiResponse<>();
        response.setMessage("Cập nhật nguyên liệu thành công");
        response.setResult(ingredientsService.updateIngredient(id, request));
        return response;
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteIngredient(@PathVariable Long id) {
        ingredientsService.deleteIngredient(id);
        ApiResponse<String> response = new ApiResponse<>();
        response.setMessage("Xóa nguyên liệu thành công");
        response.setResult("Đã xóa nguyên liệu với ID: " + id);
        return response;
    }

    // Thêm phương thức phân trang
    @GetMapping("/paged")
    public ApiResponse<Page<IngredientsResponse>> getPagedIngredients(Pageable pageable) {
        ApiResponse<Page<IngredientsResponse>> response = new ApiResponse<>();
        Page<IngredientsResponse> result = ingredientsService.getPagedIngredients(pageable);
        response.setMessage("Lấy danh sách nguyên liệu phân trang thành công");
        response.setResult(result);
        return response;
    }
}
