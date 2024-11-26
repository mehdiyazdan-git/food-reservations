package com.mapnaom.foodreservation.mappers;

import com.mapnaom.foodreservation.dtos.FoodOptionDto;
import com.mapnaom.foodreservation.models.FoodOption;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface FoodOptionMapper {
    @Mapping(source = "menuId", target = "menu.id")
    FoodOption toEntity(FoodOptionDto foodOptionDto);

    @Mapping(source = "menu.id", target = "menuId")
    FoodOptionDto toDto(FoodOption foodOption);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "menuId", target = "menu.id")
    FoodOption partialUpdate(FoodOptionDto foodOptionDto, @MappingTarget FoodOption foodOption);
}