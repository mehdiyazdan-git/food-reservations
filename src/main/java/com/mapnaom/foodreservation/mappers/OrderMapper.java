package com.mapnaom.foodreservation.mappers;

import com.mapnaom.foodreservation.dtos.OrderDto;
import com.mapnaom.foodreservation.entities.Employee;
import com.mapnaom.foodreservation.entities.FoodOption;
import com.mapnaom.foodreservation.entities.Order;
import com.mapnaom.foodreservation.exceptions.ResourceNotFoundException;
import com.mapnaom.foodreservation.repositories.EmployeeRepository;
import com.mapnaom.foodreservation.repositories.FoodOptionRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public abstract class OrderMapper {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private FoodOptionRepository foodOptionRepository;

    @Mapping(source = "employeeId", target = "employee", qualifiedByName = "employeeIdToEmployee")
    @Mapping(source = "foodOptionId", target = "foodOption", qualifiedByName = "foodOptionIdToFoodOption")
    public abstract Order toEntity(OrderDto orderDto);

    @Mapping(source = "employee", target = "employeeId", qualifiedByName = "employeeToEmployeeId")
    @Mapping(source = "foodOption", target = "foodOptionId", qualifiedByName = "foodOptionToFoodOptionId")
    public abstract OrderDto toDto(Order order);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "employeeId", target = "employee", qualifiedByName = "employeeIdToEmployee")
    @Mapping(source = "foodOptionId", target = "foodOption", qualifiedByName = "foodOptionIdToFoodOption")
    public abstract void partialUpdate(OrderDto orderDto, @MappingTarget Order order);

    @Named("employeeIdToEmployee")
    protected Employee employeeIdToEmployee(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found by id: " + employeeId));
    }

    @Named("employeeToEmployeeId")
    protected Long employeeToEmployeeId(Employee employee) {
        return employee != null ? employee.getId() : null;
    }

    @Named("foodOptionIdToFoodOption")
    protected FoodOption foodOptionIdToFoodOption(Long foodOptionId) {
        return foodOptionRepository.findById(foodOptionId)
                .orElseThrow(() -> new ResourceNotFoundException("FoodOption not found by id: " + foodOptionId));
    }

    @Named("foodOptionToFoodOptionId")
    protected Long foodOptionToFoodOptionId(FoodOption foodOption) {
        return foodOption != null ? foodOption.getId() : null;
    }
}
