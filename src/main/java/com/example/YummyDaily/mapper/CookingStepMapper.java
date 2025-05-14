package com.example.YummyDaily.mapper;

import com.example.YummyDaily.dto.request.CookingStepCreationRequest;
import com.example.YummyDaily.dto.request.CookingStepUpdateRequest;
import com.example.YummyDaily.dto.response.CookingStepResponse;
import com.example.YummyDaily.entity.CookingStep;
import com.example.YummyDaily.entity.Recipe;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CookingStepMapper {

    CookingStep toCookingStep(CookingStepCreationRequest request);

    CookingStepResponse toCookingStepResponse(CookingStep cookingStep);
    void updateCookingStep(@MappingTarget CookingStep cookingStep, CookingStepUpdateRequest request);
}
