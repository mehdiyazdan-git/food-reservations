package com.mapnaom.foodreservation.mappers;

import com.mapnaom.foodreservation.dtos.ContractorDto;
import com.mapnaom.foodreservation.exceptions.ResourceNotFoundException;
import com.mapnaom.foodreservation.models.Branch;
import com.mapnaom.foodreservation.models.Contractor;
import com.mapnaom.foodreservation.repositories.BranchRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class ContractorMapper {

    @Autowired
    private BranchRepository branchRepository;

    @Mapping(source = "branchId", target = "branch.id")
    public abstract Contractor toEntity(ContractorDto contractorDto);

    @Mapping(source = "branch.id", target = "branchId")
    public abstract ContractorDto toDto(Contractor contractor);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
            @Mapping(source = "branchId", target = "branch.id")
    public abstract Contractor partialUpdate(ContractorDto contractorDto, @MappingTarget Contractor contractor);

    protected Branch map(Long branchId){
        return branchRepository.findById(branchId).orElseThrow(() -> new ResourceNotFoundException("branch not found by id: " + branchId));
    }
    protected Long map(Branch branch){
        if (branch == null){
            return null;
        }
        return branch.getId();
    }
}