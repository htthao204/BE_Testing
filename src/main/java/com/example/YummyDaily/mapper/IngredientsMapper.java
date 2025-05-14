package com.example.YummyDaily.mapper;

import com.example.YummyDaily.dto.request.IngredientsCreationRequest;
import com.example.YummyDaily.dto.request.IngredientsUpdateRequest;
import com.example.YummyDaily.dto.response.IngredientsResponse;
import com.example.YummyDaily.entity.Ingredients;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface IngredientsMapper {

    // Chuyển từ DTO -> Entity khi tạo mới
    @Mapping(target = "ingredientId", ignore = true) // Bỏ qua ID vì DB tự sinh
    Ingredients toIngredients(IngredientsCreationRequest request);

    // Chuyển từ Entity -> DTO để phản hồi
    IngredientsResponse toIngredientsResponse(Ingredients ingredients);

    // Cập nhật Entity từ DTO mà không ghi đè giá trị null
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateIngredients(@MappingTarget Ingredients ingredients, IngredientsUpdateRequest request);
}
