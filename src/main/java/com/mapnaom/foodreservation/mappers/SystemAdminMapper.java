package com.mapnaom.foodreservation.mappers;

import com.mapnaom.foodreservation.dtos.SystemAdminDto;
import com.mapnaom.foodreservation.models.SystemAdmin;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface SystemAdminMapper {
    SystemAdmin toEntity(SystemAdminDto systemAdminDto);

    SystemAdminDto toDto(SystemAdmin systemAdmin);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    SystemAdmin partialUpdate(SystemAdminDto systemAdminDto, @MappingTarget SystemAdmin systemAdmin);
}