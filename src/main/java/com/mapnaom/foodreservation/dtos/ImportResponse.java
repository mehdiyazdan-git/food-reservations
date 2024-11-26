package com.mapnaom.foodreservation.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportResponse {
    private int successCount;
    private List<String> errorMessages;
}
