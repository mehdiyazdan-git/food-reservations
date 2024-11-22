package com.mapnaom.foodreservation.mappers;

import com.mapnaom.foodreservation.dtos.FoodDto;
import com.mapnaom.foodreservation.entities.Food;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface FoodMapper {
    Food toEntity(FoodDto foodDto);

    FoodDto toDto(Food food);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Food partialUpdate(FoodDto foodDto, @MappingTarget Food food);
}