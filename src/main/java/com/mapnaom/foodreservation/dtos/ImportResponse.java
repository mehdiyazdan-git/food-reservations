package com.mapnaom.foodreservation.dtos;

import com.mapnaom.foodreservation.utils.ExcelCellError;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * DTO representing the response of an import operation specific to Excel parsing.
 *

 * @param <DTO>  The type of successfully imported records (DTOs).
 */
@Data
@RequiredArgsConstructor
public class ImportResponse<DTO> {
    private final List<DTO> importedRecords;
    private final List<ExcelCellError> errors;
    private final Map<String, AtomicInteger> summary = new ConcurrentHashMap<>();
    private final AtomicLong totalSuccess = new AtomicLong(0);
    private final AtomicLong totalFailed = new AtomicLong(0);
    private final Map<String,ExcelCellError> errorMap = new ConcurrentHashMap<>();



}


//package com.mapnaom.foodreservation.utils;
//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import lombok.RequiredArgsConstructor;
//
//import java.io.Serializable;
//
//@EqualsAndHashCode(callSuper = true)
//@Data
//@JsonIgnoreProperties(ignoreUnknown = true)
//@RequiredArgsConstructor
//public class ExcelCellError extends Throwable implements Serializable {
//    private String message;
//    private Integer row;
//    private String columnName;
//
//
//    public ExcelCellError(String message, Integer row, String columnName) {
//        this.message = message;
//        this.row = row;
//        this.columnName = columnName;
//    }
//
//    public ExcelCellError(String message) {
//        this.message = message;
//    }
//
//}
