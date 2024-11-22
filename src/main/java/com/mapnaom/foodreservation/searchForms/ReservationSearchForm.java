package com.mapnaom.foodreservation.searchForms;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ReservationSearchForm implements Serializable {
    private final Long id;
    private final String personnelFirstName;
    private final String personnelLastName;
    private final String personnelCode;
    private final String reservedFood;
}
