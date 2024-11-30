package com.mapnaom.foodreservation.mappers;

import com.mapnaom.foodreservation.dtos.FoodOptionDto;
import com.mapnaom.foodreservation.entities.Food;
import com.mapnaom.foodreservation.entities.FoodOption;
import com.mapnaom.foodreservation.exceptions.ResourceNotFoundException;
import com.mapnaom.foodreservation.repositories.FoodRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public abstract class FoodOptionMapper {

    @Autowired
    private FoodRepository foodRepository;

    @Mapping(source = "foodId", target = "food", qualifiedByName = "foodIdToFood")
    public abstract FoodOption toEntity(FoodOptionDto foodOptionDto);

    @Mapping(source = "food", target = "foodId", qualifiedByName = "foodToFoodId")
    @Mapping(source = "food.name", target = "foodName")
    public abstract FoodOptionDto toDto(FoodOption foodOption);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "foodId", target = "food", qualifiedByName = "foodIdToFood")
    public abstract void partialUpdate(FoodOptionDto foodOptionDto, @MappingTarget FoodOption foodOption);

    @Named("foodIdToFood")
    protected Food foodIdToFood(Long foodId) {
        return foodRepository.findById(foodId)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found by id: " + foodId));
    }

    @Named("foodToFoodId")
    protected Long foodToFoodId(Food food) {
        return food != null ? food.getId() : null;
    }
}
