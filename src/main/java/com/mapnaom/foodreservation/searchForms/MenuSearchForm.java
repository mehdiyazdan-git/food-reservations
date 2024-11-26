package com.mapnaom.foodreservation.searchForms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.mapnaom.foodreservation.models.Menu}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MenuSearchForm implements Serializable {
    private Long id;
    private LocalDate date;
    private LocalDate startDate;
    private LocalDate endDate;
    private String branchName;
    private String contractorName;
    private String contractorBranchName;
}