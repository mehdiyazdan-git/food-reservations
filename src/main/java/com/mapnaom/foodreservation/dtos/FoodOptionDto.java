package com.mapnaom.foodreservation.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link com.mapnaom.foodreservation.models.FoodOption}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FoodOptionDto implements Serializable {
    private Long id;
    private String name;
    private BigDecimal price;
    private Long menuId;
}