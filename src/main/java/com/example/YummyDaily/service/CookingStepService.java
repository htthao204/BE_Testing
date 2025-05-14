package com.example.YummyDaily.service;

import com.example.YummyDaily.dto.request.CookingStepCreationRequest;
import com.example.YummyDaily.dto.request.CookingStepUpdateRequest;
import com.example.YummyDaily.dto.response.CookingStepResponse;
import com.example.YummyDaily.dto.response.RecipeResponse;
import com.example.YummyDaily.entity.CookingStep;
import com.example.YummyDaily.entity.Recipe;
import com.example.YummyDaily.exception.AppException;
import com.example.YummyDaily.exception.ErrorCode;
import com.example.YummyDaily.mapper.CookingStepMapper;
import com.example.YummyDaily.mapper.RecipeMapper;
import com.example.YummyDaily.repository.CookingStepRepository;
import com.example.YummyDaily.repository.RecipeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CookingStepService {
    CookingStepRepository cookingStepRepository;
    RecipeRepository recipeRepository;
    CookingStepMapper cookingStepMapper;
    RecipeMapper recipeMapper;

    public List<CookingStepResponse> getStepsByRecipeId(Long recipeId) {
        List<CookingStep> steps = cookingStepRepository.findByRecipe_RecipeId(recipeId);

        if (steps.isEmpty()) {
            throw new AppException(ErrorCode.RECIPE_NOT_EXISTED);
        }

        // Chuyển đổi List<CookingStep> → List<CookingStepResponse>
        List<CookingStepResponse> cookingStepResponses = steps.stream()
                .map(cookingStepMapper::toCookingStepResponse)
                .collect(Collectors.toList());

        // Lấy Recipe từ bước đầu tiên (vì tất cả bước đều thuộc cùng 1 Recipe)
        Recipe recipe = steps.get(0).getRecipe();
        RecipeResponse recipeResponse = recipeMapper.toRecipeResponse(recipe);

        // Gán RecipeResponse vào từng CookingStepResponse
        for (CookingStepResponse stepResponse : cookingStepResponses) {
            stepResponse.setRecipeResponse(recipeResponse);
        }

        return cookingStepResponses;
    }



    @Transactional
    public CookingStepResponse createCookingStep(CookingStepCreationRequest request) {
        // Kiểm tra công thức có tồn tại không
        Recipe recipe = recipeRepository.findById(request.getRecipeId())
                .orElseThrow(() -> new AppException(ErrorCode.RECIPE_NOT_EXISTED));

        CookingStep cookingStep = cookingStepMapper.toCookingStep(request);
        cookingStep.setRecipe(recipe); // Set công thức cho bước nấu ăn

        cookingStep = cookingStepRepository.save(cookingStep);
        return cookingStepMapper.toCookingStepResponse(cookingStep);
    }

    @Transactional
    public CookingStepResponse updateCookingStep(Long stepId, CookingStepUpdateRequest request) {
        CookingStep step = cookingStepRepository.findById(stepId)
                .orElseThrow(() -> new AppException(ErrorCode.STEP_NOT_FOUND));

        step.setStepNumber(request.getStepNumber());
        step.setDescription(request.getDescription());
        step.setCookingImg(request.getCookingImg());

        cookingStepRepository.save(step);
        return cookingStepMapper.toCookingStepResponse(step);
    }

    @Transactional
    public void deleteCookingStep(Long stepId) {
        CookingStep step = cookingStepRepository.findById(stepId)
                .orElseThrow(() -> new AppException(ErrorCode.STEP_NOT_FOUND));
        cookingStepRepository.delete(step);
    }
}
