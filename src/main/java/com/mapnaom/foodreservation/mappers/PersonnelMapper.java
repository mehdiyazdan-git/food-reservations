package com.mapnaom.foodreservation.mappers;

import com.mapnaom.foodreservation.dtos.PersonnelDto;
import com.mapnaom.foodreservation.entities.Personnel;
import com.mapnaom.foodreservation.entities.WorkLocation;
import com.mapnaom.foodreservation.exceptions.ResourceNotFoundException;
import com.mapnaom.foodreservation.repositories.v1.WorkLocationRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class PersonnelMapper {

    @Autowired
    protected WorkLocationRepository workLocationRepository;

    @Mapping(source = "workLocationId", target = "workLocation")
    public abstract Personnel toEntity(PersonnelDto personnelDto);

    @Mapping(source = "workLocation", target = "workLocationId")
    public abstract PersonnelDto toDto(Personnel personnel);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "workLocationId", target = "workLocation")
    public abstract void partialUpdate(PersonnelDto personnelDto, @MappingTarget Personnel personnel);

    // متد کمک برای تبدیل workLocationId به WorkLocation
    protected WorkLocation map(Long workLocationId) {
        if (workLocationId == null) {
            return null;
        }
        return workLocationRepository.findById(workLocationId)
                .orElseThrow(() -> new ResourceNotFoundException("WorkLocation not found with id: " + workLocationId));
    }

    // متد کمک برای تبدیل WorkLocation به workLocationId
    protected Long map(WorkLocation workLocation) {
        if (workLocation == null) {
            return null;
        }
        return workLocation.getId();
    }
}
