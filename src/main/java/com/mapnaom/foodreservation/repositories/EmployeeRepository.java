package com.mapnaom.foodreservation.repositories;

import com.mapnaom.foodreservation.entities.Employee;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

// Repository for Employee
@Repository
public interface EmployeeRepository extends UserRepository<Employee>, JpaSpecificationExecutor<Employee> {
}
