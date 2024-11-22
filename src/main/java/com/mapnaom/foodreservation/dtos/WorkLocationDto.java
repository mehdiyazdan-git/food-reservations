package com.mapnaom.foodreservation.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link com.mapnaom.foodreservation.entities.WorkLocation}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkLocationDto implements Serializable {
    private Long id;
    @NotBlank(message = "Work Location Name cannot be blank")
    @Size(min = 2, max = 100, message = "Work Location Name must be between 2 and 100 characters")
    private String workLocationName;
}