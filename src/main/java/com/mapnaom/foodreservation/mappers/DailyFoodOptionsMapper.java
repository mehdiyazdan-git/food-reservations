package com.mapnaom.foodreservation.mappers;

import com.mapnaom.foodreservation.dtos.DailyFoodOptionsDto;
import com.mapnaom.foodreservation.entities.DailyFoodOptions;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring",
        uses = {FoodMapper.class} // استفاده از FoodMapper برای مپینگ لیست‌ها
)
public abstract class DailyFoodOptionsMapper {

    @Autowired
    protected FoodMapper foodMapper;

    @Mapping(target = "foodList", source = "foodList")
    public abstract DailyFoodOptions toEntity(DailyFoodOptionsDto dailyFoodOptionsDto);

    @Mapping(target = "foodList", source = "foodList")
    public abstract DailyFoodOptionsDto toDto(DailyFoodOptions dailyFoodOptions);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void partialUpdate(DailyFoodOptionsDto dailyFoodOptionsDto, @MappingTarget DailyFoodOptions dailyFoodOptions);
}
