package com.mapnaom.foodreservation.mappers;

import com.mapnaom.foodreservation.dtos.WorkLocationDto;
import com.mapnaom.foodreservation.entities.WorkLocation;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface WorkLocationMapper {
    WorkLocation toEntity(WorkLocationDto workLocationDto);

    WorkLocationDto toDto(WorkLocation workLocation);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    WorkLocation partialUpdate(WorkLocationDto workLocationDto, @MappingTarget WorkLocation workLocation);
}