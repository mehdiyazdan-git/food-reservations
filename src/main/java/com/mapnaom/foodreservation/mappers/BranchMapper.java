package com.mapnaom.foodreservation.mappers;

import com.mapnaom.foodreservation.dtos.BranchDto;
import com.mapnaom.foodreservation.entities.Branch;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface BranchMapper {
    Branch toEntity(BranchDto branchDto);

    BranchDto toDto(Branch branch);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Branch partialUpdate(BranchDto branchDto, @MappingTarget Branch branch);
}