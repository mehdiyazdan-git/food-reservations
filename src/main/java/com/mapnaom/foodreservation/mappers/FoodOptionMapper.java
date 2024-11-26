package com.mapnaom.foodreservation.mappers;

import com.mapnaom.foodreservation.dtos.FoodOptionDto;
import com.mapnaom.foodreservation.entities.Food;
import com.mapnaom.foodreservation.entities.FoodOption;
import com.mapnaom.foodreservation.entities.Menu;
import com.mapnaom.foodreservation.repositories.FoodOptionRepository;
import com.mapnaom.foodreservation.repositories.FoodRepository;
import com.mapnaom.foodreservation.repositories.MenuRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
@RequiredArgsConstructor
public abstract class FoodOptionMapper {

    @Autowired
    private final FoodRepository foodRepository;

    @Autowired
    private MenuRepository menuRepository;


    @Mapping(target = "menu",expression = "java(mapLongToMenu(menuId))")
    @Mapping( target = "food",expression = "java(mapLongToFood(foodId))")
    public abstract FoodOption toEntity(FoodOptionDto foodOptionDto);

    @Mapping(expression = "java(mapMnuToLong(menu))", target = "menuId")
    @Mapping(expression = "java(mapFoodToLong(food))", target = "foodId")
    public abstract FoodOptionDto toDto(FoodOption foodOption);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "menu",expression = "java(mapLongToMenu(menuId))")
    @Mapping( target = "food",expression = "java(mapLongToFood(foodId))")
    public abstract FoodOption partialUpdate(FoodOptionDto foodOptionDto, @MappingTarget FoodOption foodOption);


    public Menu mapLongToMenu(Long menuId){
        return menuRepository.findById(menuId).orElse(null);
    }
    public Food mapLongToFood(Long foodId){
        return foodRepository.findById(foodId).orElse(null);
    }
    public Long mapMnuToLong(Menu menu){
        if (menu == null) return null;
        return menu.getId();
    }
    public Long mapFoodToLong(Food food){
        if (food == null) return null;
        return food.getId();
    }

}