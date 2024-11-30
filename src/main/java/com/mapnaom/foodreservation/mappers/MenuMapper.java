package com.mapnaom.foodreservation.mappers;

import com.mapnaom.foodreservation.dtos.FoodOptionDto;
import com.mapnaom.foodreservation.dtos.MenuDto;
import com.mapnaom.foodreservation.entities.Branch;
import com.mapnaom.foodreservation.entities.Contractor;
import com.mapnaom.foodreservation.entities.FoodOption;
import com.mapnaom.foodreservation.entities.Menu;
import com.mapnaom.foodreservation.exceptions.ResourceNotFoundException;
import com.mapnaom.foodreservation.repositories.BranchRepository;
import com.mapnaom.foodreservation.repositories.ContractorRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {FoodOptionMapper.class}
)
public abstract class MenuMapper {

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private ContractorRepository contractorRepository;

    @Autowired
    private FoodOptionMapper foodOptionMapper;

    @Mapping(source = "contractorId", target = "contractor", qualifiedByName = "contractorIdToContractor")
    @Mapping(source = "branchId", target = "branch", qualifiedByName = "branchIdToBranch")
    @Mapping(target = "foodOptions", ignore = true)
    public abstract Menu toEntity(MenuDto menuDto);

    @AfterMapping
    protected void afterToEntity(MenuDto menuDto, @MappingTarget Menu menu) {
        if (menuDto.getFoodOptions() != null) {
            Set<FoodOption> foodOptions = menuDto.getFoodOptions().stream()
                    .map(foodOptionDto -> {
                        FoodOption foodOption = foodOptionMapper.toEntity(foodOptionDto);
                        foodOption.setMenu(menu);
                        return foodOption;
                    })
                    .collect(Collectors.toSet());
            menu.setFoodOptions(foodOptions);
        }
    }

    @Mapping(source = "contractor", target = "contractorId", qualifiedByName = "contractorToContractorId")
    @Mapping(source = "branch", target = "branchId", qualifiedByName = "branchToBranchId")
    public abstract MenuDto toDto(Menu menu);

    @AfterMapping
    protected void afterToDto(Menu menu, @MappingTarget MenuDto menuDto) {
        if (menu.getFoodOptions() != null) {
            Set<FoodOptionDto> foodOptionDtos = menu.getFoodOptions().stream()
                    .map(foodOptionMapper::toDto)
                    .collect(Collectors.toSet());
            menuDto.setFoodOptions(foodOptionDtos);
        }
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "contractorId", target = "contractor", qualifiedByName = "contractorIdToContractor")
    @Mapping(source = "branchId", target = "branch", qualifiedByName = "branchIdToBranch")
    @Mapping(target = "foodOptions", ignore = true)
    public abstract void partialUpdate(MenuDto menuDto, @MappingTarget Menu menu);

    @AfterMapping
    protected void afterPartialUpdate(MenuDto menuDto, @MappingTarget Menu menu) {
        if (menuDto.getFoodOptions() != null) {
            updateFoodOptions(menuDto.getFoodOptions(), menu);
        }
    }

    private void updateFoodOptions(Set<FoodOptionDto> foodOptionDtos, Menu menu) {
        Map<Long, FoodOption> existingFoodOptions = menu.getFoodOptions().stream()
                .collect(Collectors.toMap(FoodOption::getId, fo -> fo));

        Set<FoodOption> updatedFoodOptions = new HashSet<>();

        for (FoodOptionDto dto : foodOptionDtos) {
            FoodOption foodOption;
            if (dto.getId() != null && existingFoodOptions.containsKey(dto.getId())) {
                // Update existing FoodOption
                foodOption = existingFoodOptions.get(dto.getId());
                foodOptionMapper.partialUpdate(dto, foodOption);
            } else {
                // Add new FoodOption
                foodOption = foodOptionMapper.toEntity(dto);
                foodOption.setMenu(menu);
            }
            updatedFoodOptions.add(foodOption);
        }

        // Remove FoodOptions not present in the DTO
        Set<FoodOption> toRemove = new HashSet<>(menu.getFoodOptions());
        toRemove.removeAll(updatedFoodOptions);
        toRemove.forEach(fo -> fo.setMenu(null));

        menu.getFoodOptions().clear();
        menu.getFoodOptions().addAll(updatedFoodOptions);
    }

    @Named("branchIdToBranch")
    protected Branch branchIdToBranch(Long branchId) {
        return branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found by id: " + branchId));
    }

    @Named("branchToBranchId")
    protected Long branchToBranchId(Branch branch) {
        return branch != null ? branch.getId() : null;
    }

    @Named("contractorIdToContractor")
    protected Contractor contractorIdToContractor(Long contractorId) {
        return contractorRepository.findById(contractorId)
                .orElseThrow(() -> new ResourceNotFoundException("Contractor not found by id: " + contractorId));
    }

    @Named("contractorToContractorId")
    protected Long contractorToContractorId(Contractor contractor) {
        return contractor != null ? contractor.getId() : null;
    }

}
