package com.mapnaom.foodreservation.mappers;

import com.mapnaom.foodreservation.dtos.ReservationDto;
import com.mapnaom.foodreservation.entities.DailyFoodOptions;
import com.mapnaom.foodreservation.entities.Personnel;
import com.mapnaom.foodreservation.entities.Reservation;
import com.mapnaom.foodreservation.exceptions.ResourceNotFoundException;
import com.mapnaom.foodreservation.repositories.v1.DailyFoodOptionsRepository;
import com.mapnaom.foodreservation.repositories.v1.PersonnelRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class ReservationMapper {

    @Autowired
    protected PersonnelRepository personnelRepository;

    @Autowired
    protected DailyFoodOptionsRepository dailyFoodOptionsRepository;

    @Mapping(source = "personnelId", target = "personnel")
    @Mapping(source = "foodPlanId", target = "dailyFoodOptions")
    public abstract Reservation toEntity(ReservationDto reservationDto);

    @Mapping(source = "personnel.id", target = "personnelId")
    @Mapping(source = "dailyFoodOptions.id", target = "foodPlanId")
    public abstract ReservationDto toDto(Reservation reservation);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "personnelId", target = "personnel")
    @Mapping(source = "foodPlanId", target = "dailyFoodOptions")
    public abstract void partialUpdate(ReservationDto reservationDto, @MappingTarget Reservation reservation);

    // متد کمک برای تبدیل personnelId به Personnel
    protected Personnel mapPersonnel(Long personnelId) {
        if (personnelId == null) {
            return null;
        }
        return personnelRepository.findById(personnelId)
                .orElseThrow(() -> new ResourceNotFoundException("Personnel not found with id: " + personnelId));
    }

    // متد کمک برای تبدیل foodPlanId به DailyFoodOptions
    protected DailyFoodOptions mapFoodPlan(Long foodPlanId) {
        if (foodPlanId == null) {
            return null;
        }
        return dailyFoodOptionsRepository.findById(foodPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("DailyFoodOptions not found with id: " + foodPlanId));
    }

    // متد کمک برای تبدیل Personnel به personnelId
    protected Long map(Personnel personnel) {
        if (personnel == null) {
            return null;
        }
        return personnel.getId();
    }

    // متد کمک برای تبدیل DailyFoodOptions به foodPlanId
    protected Long map(DailyFoodOptions dailyFoodOptions) {
        if (dailyFoodOptions == null) {
            return null;
        }
        return dailyFoodOptions.getId();
    }
}
