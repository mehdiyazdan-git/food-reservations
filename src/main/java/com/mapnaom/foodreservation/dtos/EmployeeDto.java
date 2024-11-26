package com.mapnaom.foodreservation.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mapnaom.foodreservation.entities.Employee;
import com.mapnaom.foodreservation.enums.RoleName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link Employee}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeDto implements Serializable {
    private Long id;
    private String username;
    private String password;
    private Set<RoleName> roles;
    private boolean active;
    private String firstName;
    private String lastName;
    private String employeeCode;
    private Integer branchId;
    private String branchName;
}