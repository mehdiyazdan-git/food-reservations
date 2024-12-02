package com.mapnaom.foodreservation.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mapnaom.foodreservation.entities.Menu;
import com.mapnaom.foodreservation.utils.Excel;
import com.mapnaom.foodreservation.utils.ExcelStrategy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * DTO for {@link Menu}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Excel(name = "Menu", useTitleRow = true, strategy = ExcelStrategy.COMPOSITE)

public class MenuDto implements Serializable {
    private Long id;
    private LocalDate date;
    private Long branchId;
    private Long contractorId;
    private Set<FoodOptionDto> foodOptions = new LinkedHashSet<>();
}