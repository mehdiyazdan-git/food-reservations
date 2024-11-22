package com.mapnaom.foodreservation.searchForms;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
@Data
public class FoodPlanSearchForm implements Serializable {
    private Long id;
    private String foodName;
    private LocalDate localDate;
}
