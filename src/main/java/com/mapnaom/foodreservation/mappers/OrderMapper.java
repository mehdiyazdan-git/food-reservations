package com.mapnaom.foodreservation.mappers;

import com.mapnaom.foodreservation.dtos.OrderDto;
import com.mapnaom.foodreservation.exceptions.ResourceNotFoundException;
import com.mapnaom.foodreservation.entities.Employee;
import com.mapnaom.foodreservation.entities.Order;
import com.mapnaom.foodreservation.repositories.EmployeeRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {FoodOptionMapper.class})
public abstract class OrderMapper {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Mapping(source = "employeeId", target = "employee.id")
    public abstract Order toEntity(OrderDto orderDto);

    @Mapping(source = "employee.id", target = "employeeId")
    public abstract OrderDto toDto(Order order);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "employeeId", target = "employee.id")
    public abstract Order partialUpdate(OrderDto orderDto, @MappingTarget Order order);

    protected Employee map(Long employeeId) {
        return employeeRepository.findById(employeeId).orElseThrow(() -> new ResourceNotFoundException("Employee not found by id: " + employeeId));
    }
    protected Long map(Employee employee){
        if (employee == null) {
            return null;
        }
        return employee.getId();
    }
}