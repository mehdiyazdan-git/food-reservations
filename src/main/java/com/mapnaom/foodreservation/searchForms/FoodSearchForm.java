package com.mapnaom.foodreservation.searchForms;

import lombok.Data;

import java.io.Serializable;

@Data
public class FoodSearchForm implements Serializable {
    private Long id;
    private String name;
}
