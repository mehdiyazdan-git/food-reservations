package com.mapnaom.foodreservation.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO برای پاسخ واردسازی پرسنل‌ها از طریق فایل اکسل
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonnelImportResponse {
    private int successCount;
    private List<String> errorMessages;
}
