package com.mapnaom.foodreservation.searchForms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link com.mapnaom.foodreservation.models.Contractor}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContractorSearchForm implements Serializable {
    private Long id;
    private String username;
    private Boolean active;
    private String name;
    private String branchName;
}