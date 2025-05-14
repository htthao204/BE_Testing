package com.example.YummyDaily.service;

import com.example.YummyDaily.dto.request.IngredientsCreationRequest;
import com.example.YummyDaily.dto.request.IngredientsUpdateRequest;
import com.example.YummyDaily.dto.response.IngredientsResponse;
import com.example.YummyDaily.entity.Ingredients;
import com.example.YummyDaily.exception.AppException;
import com.example.YummyDaily.exception.ErrorCode;
import com.example.YummyDaily.mapper.IngredientsMapper;
import com.example.YummyDaily.repository.IngredientsRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IngredientsService {
    private final IngredientsRepository ingredientsRepository;
    private final IngredientsMapper ingredientsMapper;

    // Lấy tất cả nguyên liệu
    public List<IngredientsResponse> getAllIngredients() {
        return ingredientsRepository.findAll().stream()
                .map(ingredientsMapper::toIngredientsResponse)
                .collect(Collectors.toList());
    }

    // Lấy nguyên liệu theo ID
    public IngredientsResponse getIngredientById(Long id) {
        Ingredients ingredient = ingredientsRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.INGREDIENT_NOT_EXISTED));

        return ingredientsMapper.toIngredientsResponse(ingredient);
    }

    // Phương thức phân trang cho nguyên liệu
    public Page<IngredientsResponse> getPagedIngredients(Pageable pageable) {
        // Lấy danh sách phân trang từ repository và chuyển đổi thành IngredientsResponse
        return ingredientsRepository.findAll(pageable)
                .map(ingredientsMapper::toIngredientsResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public IngredientsResponse createIngredient(IngredientsCreationRequest request) {
        if (ingredientsRepository.findByIngredientName(request.getIngredientName()).isPresent()) {
            throw new AppException(ErrorCode.INGREDIENT_EXISTED);
        }
        Ingredients ingredient = ingredientsMapper.toIngredients(request);
        ingredient = ingredientsRepository.save(ingredient);
        return ingredientsMapper.toIngredientsResponse(ingredient);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public IngredientsResponse updateIngredient(Long id, IngredientsUpdateRequest request) {
        Ingredients ingredient = ingredientsRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.INGREDIENT_NOT_EXISTED));

        ingredient.setIngredientName(request.getIngredientName());
        ingredient.setUnit(request.getUnit());
        ingredient.setIngredientImage(request.getIngredientImage());

        ingredient = ingredientsRepository.save(ingredient);
        return ingredientsMapper.toIngredientsResponse(ingredient);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteIngredient(Long id) {
        Ingredients ingredient = ingredientsRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.INGREDIENT_NOT_EXISTED));
        ingredientsRepository.delete(ingredient);
    }
}
