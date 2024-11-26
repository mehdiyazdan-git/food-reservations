package com.mapnaom.foodreservation.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mapnaom.foodreservation.enums.RoleName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link com.mapnaom.foodreservation.models.Role}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoleDto implements Serializable {
    private Long id;
    private RoleName name;
}