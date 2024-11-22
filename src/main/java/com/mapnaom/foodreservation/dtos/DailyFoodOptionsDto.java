package com.mapnaom.foodreservation.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mapnaom.foodreservation.entities.DailyFoodOptions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link DailyFoodOptions}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DailyFoodOptionsDto implements Serializable {
    private Long id;
    private List<FoodDto> foodList;
    private LocalDate localDate;
}