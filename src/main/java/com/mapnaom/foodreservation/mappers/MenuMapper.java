package com.mapnaom.foodreservation.mappers;

import com.mapnaom.foodreservation.dtos.MenuDto;
import com.mapnaom.foodreservation.exceptions.ResourceNotFoundException;
import com.mapnaom.foodreservation.entities.Branch;
import com.mapnaom.foodreservation.entities.Contractor;
import com.mapnaom.foodreservation.entities.Menu;
import com.mapnaom.foodreservation.repositories.BranchRepository;
import com.mapnaom.foodreservation.repositories.ContractorRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {FoodOptionMapper.class})
public abstract class MenuMapper {

    @Autowired
    private BranchRepository branchRepository;
    private ContractorRepository contractorRepository;

    @Mapping(source = "contractorId", target = "contractor.id")
    @Mapping(source = "branchId", target = "branch.id")
    public abstract Menu toEntity(MenuDto menuDto);

    @AfterMapping
    void linkFoodOptions(@MappingTarget Menu menu) {
        menu.getFoodOptions().forEach(foodOption -> foodOption.setMenu(menu));
    }

    @InheritInverseConfiguration(name = "toEntity")
    public abstract MenuDto toDto(Menu menu);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract Menu partialUpdate(MenuDto menuDto, @MappingTarget Menu menu);

    protected Branch mapBranch(Long branchId){
        return branchRepository.findById(branchId).orElseThrow(() -> new ResourceNotFoundException("branch not found by id: " + branchId));
    }
    protected Long mapBranch(Branch branch){
        if (branch == null){
            return null;
        }
        return branch.getId();
    }
    protected Contractor mapContractor(Long contractorId){
        return contractorRepository.findById(contractorId).orElseThrow(() -> new ResourceNotFoundException("contractor not found by id: " + contractorId));
    }
    protected Long mapContractor(Contractor contractor){
        if (contractor == null){
            return null;
        }
        return contractor.getId();
    }
}
