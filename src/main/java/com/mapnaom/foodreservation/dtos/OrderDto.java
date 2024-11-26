package com.mapnaom.foodreservation.dtos;

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
public class OrderDto implements Serializable {
    private Long id;
    private Long employeeId;
    private FoodOptionDto foodOption;
    private LocalDate date;
    private String status;
}