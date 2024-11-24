package com.mapnaom.foodreservation.mappers;

import com.mapnaom.foodreservation.dtos.ReservationDto;
import com.mapnaom.foodreservation.entities.FoodMenuItem;
import com.mapnaom.foodreservation.entities.Personnel;
import com.mapnaom.foodreservation.entities.Reservation;
import com.mapnaom.foodreservation.repositories.FoodMenuItemRepository;
import com.mapnaom.foodreservation.repositories.PersonnelRepository;
import jakarta.persistence.EntityNotFoundException;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Collection;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class ReservationMapper {

    @Autowired
    protected PersonnelRepository personnelRepository;

    @Autowired
    protected FoodMenuItemRepository foodMenuItemRepository;

    // Mapping from DTO to Entity with ID resolution
    @Mapping(source = "personnelId", target = "personnel", qualifiedByName = "mapToPersonnel")
    @Mapping(source = "foodMenuItemId", target = "foodMenuItem", qualifiedByName = "mapToFoodMenuItem")
    @Mapping(target = "id", ignore = true)
    public abstract Reservation toEntity(ReservationDto reservationDto);

    // Mapping from Entity to DTO
    @Mapping(source = "personnel.id", target = "personnelId")
    @Mapping(source = "foodMenuItem.id", target = "foodMenuItemId")
    public abstract ReservationDto toDto(Reservation reservation);

    // Partial Update Mapping
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "personnelId", target = "personnel", qualifiedByName = "mapToPersonnel")
    @Mapping(source = "foodMenuItemId", target = "foodMenuItem", qualifiedByName = "mapToFoodMenuItem")
    @Mapping(target = "id", ignore = true)
    public abstract void partialUpdate(ReservationDto reservationDto, @MappingTarget Reservation reservation);

    // List mappings
    public abstract List<ReservationDto> toDtoList(Collection<Reservation> reservations);

    public abstract List<Reservation> toEntityList(Collection<ReservationDto> reservationDtos);

    // MapStruct Custom Mapping Logic for Personnel
    @Named("mapToPersonnel")
    protected Personnel mapToPersonnel(Long personnelId) {
        if (personnelId == null) {
            return null;
        }
        return personnelRepository.findById(personnelId)
                .orElseThrow(() -> new EntityNotFoundException("Personnel not found with ID: " + personnelId));
    }

    // MapStruct Custom Mapping Logic for FoodMenuItem
    @Named("mapToFoodMenuItem")
    protected FoodMenuItem mapToFoodMenuItem(Integer foodMenuItemId) {
        if (foodMenuItemId == null) {
            return null;
        }
        return foodMenuItemRepository.findById(foodMenuItemId)
                .orElseThrow(() -> new EntityNotFoundException("FoodMenuItem not found with ID: " + foodMenuItemId));
    }

    // Utility for mapping back
    protected Long mapPersonnelToId(Personnel personnel) {
        return personnel != null ? personnel.getId() : null;
    }

    protected Integer mapFoodMenuItemToId(FoodMenuItem foodMenuItem) {
        return foodMenuItem != null ? foodMenuItem.getId() : null;
    }
}
