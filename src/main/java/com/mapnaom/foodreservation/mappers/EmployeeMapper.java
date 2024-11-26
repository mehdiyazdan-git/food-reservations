package com.mapnaom.foodreservation.mappers;

import com.mapnaom.foodreservation.dtos.EmployeeDto;
import com.mapnaom.foodreservation.exceptions.ResourceNotFoundException;
import com.mapnaom.foodreservation.models.Branch;
import com.mapnaom.foodreservation.models.Employee;
import com.mapnaom.foodreservation.repositories.BranchRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class EmployeeMapper {

    @Autowired
    private  BranchRepository branchRepository;

    @Mapping(source = "branchId", target = "branch")
    public abstract Employee toEntity(EmployeeDto employeeDto);

    @Mapping(source = "branch", target = "branchId")
    public abstract EmployeeDto toDto(Employee employee);

    @Mapping(source = "branchId", target = "branch")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract Employee partialUpdate(EmployeeDto employeeDto, @MappingTarget Employee employee);

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