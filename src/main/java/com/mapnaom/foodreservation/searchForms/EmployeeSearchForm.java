package com.mapnaom.foodreservation.searchForms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mapnaom.foodreservation.entities.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link Employee}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeSearchForm implements Serializable {
    private Long id;
    private String username;
    private Boolean active;
    private String firstName;
    private String lastName;
    private String employeeCode;
    private String branchName;
}
