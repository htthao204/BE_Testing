package com.example.YummyDaily.mapper;

import com.example.YummyDaily.dto.request.RatingCreationRequest;
import com.example.YummyDaily.dto.request.RatingUpdateRequest;
import com.example.YummyDaily.dto.response.RatingResponse;
import com.example.YummyDaily.entity.Rating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RatingMapper {

    Rating toRating(RatingCreationRequest request);
    @Mapping(source = "recipe.recipeId", target = "recipeId")
    @Mapping(source = "date", target = "date")
    @Mapping(source = "user", target = "user")
    RatingResponse toRatingResponse(Rating rating);
    void updateRating(@MappingTarget Rating rating, RatingUpdateRequest request);
}
