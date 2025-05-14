package com.example.YummyDaily.controller;

import com.example.YummyDaily.dto.request.CookingStepCreationRequest;
import com.example.YummyDaily.dto.request.CookingStepUpdateRequest;
import com.example.YummyDaily.dto.response.ApiResponse;
import com.example.YummyDaily.dto.response.CookingStepResponse;
import com.example.YummyDaily.service.CookingStepService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cookingsteps")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CookingStepController {
    CookingStepService cookingStepService;

    @GetMapping("/recipe/{recipeId}")
    public ApiResponse<List<CookingStepResponse>> getStepsByRecipeId(@PathVariable Long recipeId) {
        log.info("Fetching cooking steps for recipeId: {}", recipeId);
        var result = cookingStepService.getStepsByRecipeId(recipeId);
        return ApiResponse.<List<CookingStepResponse>>builder()
                .code(200)
                .message("Lấy danh sách bước nấu ăn thành công")
                .result(result)
                .build();
    }

    @PostMapping
    public ApiResponse<CookingStepResponse> createCookingStep(@RequestBody @Valid CookingStepCreationRequest request) {
        var result = cookingStepService.createCookingStep(request);
        return ApiResponse.<CookingStepResponse>builder()
                .code(201)
                .message("Thêm bước nấu ăn thành công")
                .result(result)
                .build();
    }

    @PutMapping("/{stepId}")
    public ApiResponse<CookingStepResponse> updateCookingStep(
            @PathVariable Long stepId,
            @RequestBody @Valid CookingStepUpdateRequest request) {
        log.info("Updating cooking step with id: {}", stepId);
        var result = cookingStepService.updateCookingStep(stepId, request);
        return ApiResponse.<CookingStepResponse>builder()
                .code(200)
                .message("Cập nhật bước nấu ăn thành công")
                .result(result)
                .build();
    }

    @DeleteMapping("/{stepId}")
    public ApiResponse<Void> deleteCookingStep(@PathVariable Long stepId) {
        log.info("Deleting cooking step with id: {}", stepId);
        cookingStepService.deleteCookingStep(stepId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Xóa bước nấu ăn thành công")
                .build();
    }
}
