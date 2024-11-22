package com.mapnaom.foodreservation.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link com.mapnaom.foodreservation.entities.Reservation}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReservationDto implements Serializable {
    private Long id;
    @NotNull(message = "Personnel ID cannot be null")
    private Long personnelId;
    @NotNull(message = "Food plan ID cannot be null")
    private Long foodPlanId;
}