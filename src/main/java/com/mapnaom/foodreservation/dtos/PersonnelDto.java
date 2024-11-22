package com.mapnaom.foodreservation.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * DTO for {@link com.mapnaom.foodreservation.entities.Personnel}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonnelDto implements Serializable {
    private Long id;

    @NotBlank(message = "First name cannot be blank")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstname;

    @NotBlank(message = "Last name cannot be blank")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastname;

    @NotBlank(message = "Code cannot be blank")
    @Size(min = 4, max = 10, message = "Code must be between 4 and 10 characters")
    private String code;

    @NotNull(message = "Work Location ID cannot be null")
    private Long workLocationId;
}
