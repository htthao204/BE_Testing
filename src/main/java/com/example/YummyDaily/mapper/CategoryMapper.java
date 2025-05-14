package com.example.YummyDaily.mapper;

import com.example.YummyDaily.dto.request.CategoryCreationRequest;
import com.example.YummyDaily.dto.request.CategoryUpdateRequest;
import com.example.YummyDaily.dto.response.CategoryResponse;
import com.example.YummyDaily.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toCategory(CategoryCreationRequest request);

    CategoryResponse toCategoryResponse(Category category);
    List<CategoryResponse> toCategoryResponseList(List<Category> categories);
    void updateCategory(@MappingTarget Category category, CategoryUpdateRequest request);
}
