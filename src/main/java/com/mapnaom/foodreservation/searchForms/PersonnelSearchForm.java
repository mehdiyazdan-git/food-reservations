package com.mapnaom.foodreservation.searchForms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonnelSearchForm implements Serializable {
    private Long id;
    private String firstname;
    private String lastname;
    private String code;
    private String workLocationName;
}
