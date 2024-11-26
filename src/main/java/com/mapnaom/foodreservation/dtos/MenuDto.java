package com.mapnaom.foodreservation.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * DTO for {@link com.mapnaom.foodreservation.models.Menu}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MenuDto implements Serializable {
    private Long id;
    private LocalDate date;
    private Long branchId;
    private Long contractorId;
    private Set<FoodOptionDto> foodOptions = new LinkedHashSet<>();
}