package com.example.YummyDaily.service;

import com.example.YummyDaily.dto.request.CategoryCreationRequest;
import com.example.YummyDaily.dto.request.CategoryUpdateRequest;
import com.example.YummyDaily.dto.response.CategoryResponse;
import com.example.YummyDaily.entity.Category;
import com.example.YummyDaily.exception.AppException;
import com.example.YummyDaily.exception.ErrorCode;
import com.example.YummyDaily.mapper.CategoryMapper;
import com.example.YummyDaily.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse createCategory(CategoryCreationRequest request) {
        if (categoryRepository.existsByCategoryName(request.getCategoryName())) {
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }
        Category category = categoryMapper.toCategory(request);
        category = categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(category);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        // (Tùy chọn) Ghi log số lượng Recipe sẽ bị xóa
        long recipeCount = category.getRecipes().size(); // Yêu cầu tải dữ liệu vì fetch = LAZY
        if (recipeCount > 0) {
            log.info("Xóa danh mục ID {} sẽ xóa {} công thức liên quan", id, recipeCount);
        }

        categoryRepository.delete(category);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse updateCategory(Long id, CategoryUpdateRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        // Dùng Mapper thay vì cập nhật thủ công
        categoryMapper.updateCategory(category, request);

        category = categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(category);
    }

    public List<CategoryResponse> searchCategories(String keyword) {
        List<Category> categories;

        // Nếu là số → tìm theo ID
        if (keyword.matches("\\d+")) {
            Long id = Long.parseLong(keyword);
            Category category = categoryRepository.findById(id).orElse(null);
            categories = category != null ? List.of(category) : List.of();
        } else {
            // Không phải số → tìm theo tên
            categories = categoryRepository.findByCategoryNameContainingIgnoreCase(keyword);
        }

        return categoryMapper.toCategoryResponseList(categories);
    }

    public Page<CategoryResponse> getPagedCategories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        return categoryPage.map(categoryMapper::toCategoryResponse);
    }

    public Page<CategoryResponse> searchPagedCategories(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> result = categoryRepository.findByCategoryNameContainingIgnoreCase(keyword, pageable);
        return result.map(categoryMapper::toCategoryResponse);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse patchCategory(Long id, CategoryUpdateRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        // Cập nhật categoryName nếu được cung cấp
        if (request.getCategoryName() != null && !request.getCategoryName().isBlank()) {
            if (!category.getCategoryName().equals(request.getCategoryName()) &&
                    categoryRepository.existsByCategoryName(request.getCategoryName())) {
                throw new AppException(ErrorCode.CATEGORY_EXISTED);
            }
            category.setCategoryName(request.getCategoryName());
        }

        // Cập nhật categoryImage nếu được cung cấp
        if (request.getCategoryImage() != null && !request.getCategoryImage().isBlank()) {
            category.setCategoryImage(request.getCategoryImage());
        }

        category = categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(category);
    }
}