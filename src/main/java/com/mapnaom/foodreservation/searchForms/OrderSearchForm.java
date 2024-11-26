package com.mapnaom.foodreservation.searchForms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.mapnaom.foodreservation.models.Order}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderSearchForm implements Serializable {
    private Long id;
    private String employeeFirstName;
    private String employeeLastName;
    private String employeeBranchName;
    private String foodOptionName;
    private LocalDate date;
    private String status;
}