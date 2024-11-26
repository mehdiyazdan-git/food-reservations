package com.mapnaom.foodreservation.searchForms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mapnaom.foodreservation.entities.FoodOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for {@link FoodOption}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FoodOptionSearchForm implements Serializable {
    private Long id;
    private String name;
    private BigDecimal price;
    private LocalDate menuDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String menuBranchName;
}