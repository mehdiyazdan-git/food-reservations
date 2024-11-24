package com.mapnaom.foodreservation.mappers;

import com.mapnaom.foodreservation.dtos.FoodMenuItemDto;
import com.mapnaom.foodreservation.entities.FoodMenuItem;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface FoodMenuItemMapper {
    @Mapping(source = "foodMenuId", target = "foodMenu.id")
    @Mapping(source = "foodId", target = "food.id")
    FoodMenuItem toEntity(FoodMenuItemDto foodMenuItemDto);

    @InheritInverseConfiguration(name = "toEntity")
    FoodMenuItemDto toDto(FoodMenuItem foodMenuItem);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    FoodMenuItem partialUpdate(FoodMenuItemDto foodMenuItemDto, @MappingTarget FoodMenuItem foodMenuItem);
}