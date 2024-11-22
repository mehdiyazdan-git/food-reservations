package com.mapnaom.foodreservation.searchForms;

import lombok.Data;

import java.io.Serializable;

@Data
public class WorkLocationSearchForm implements Serializable {
    private Long id;
    private String workLocationName;
}
