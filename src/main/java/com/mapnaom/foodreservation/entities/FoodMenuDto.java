package com.mapnaom.foodreservation.entities;

import com.mapnaom.foodreservation.dtos.FoodMenuItemDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for {@link FoodMenu}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoodMenuDto implements Serializable {
    private Long id;
    private LocalDate localDate;
    private List<FoodMenuItemDto> foodMenuItems = new ArrayList<>();
}