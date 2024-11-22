package com.mapnaom.foodreservation.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO برای پاسخ واردسازی غذاها از طریق فایل اکسل
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoodImportResponse {
    private int successCount;
    private List<String> errorMessages;
}
