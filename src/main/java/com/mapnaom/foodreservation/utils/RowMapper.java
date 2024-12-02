package com.mapnaom.foodreservation.utils;

import com.mapnaom.foodreservation.exceptions.ExcelDataImportException;
import org.apache.poi.ss.usermodel.Row;

import java.util.Map;

@FunctionalInterface
public interface RowMapper<T> {
    T mapRow(Row row, Map<String, Integer> headerMap) throws ExcelDataImportException;
}
