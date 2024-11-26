package com.mapnaom.foodreservation.mappers;

import com.mapnaom.foodreservation.entities.FoodMenu;
import com.mapnaom.foodreservation.entities.FoodMenuDto;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {FoodMenuItemMapper.class})
public interface FoodMenuMapper {
    FoodMenu toEntity(FoodMenuDto foodMenuDto);

    @AfterMapping
    default void linkFoodMenuItems(@MappingTarget FoodMenu foodMenu) {
        foodMenu.getFoodMenuItems().forEach(foodMenuItem -> foodMenuItem.setFoodMenu(foodMenu));
    }

    FoodMenuDto toDto(FoodMenu foodMenu);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    FoodMenu partialUpdate(FoodMenuDto foodMenuDto, @MappingTarget FoodMenu foodMenu);
}