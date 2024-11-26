package com.mapnaom.foodreservation.utils;


import com.github.eloyzone.jalalicalendar.DateConverter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ExcelRowParser {

    public static <T> T parseRowToDto(Row row, Class<T> dtoClass, int rowNum) {
        try {
            T dtoInstance = dtoClass.getDeclaredConstructor().newInstance();
            Field[] fields = dtoClass.getDeclaredFields();
            Map<Integer, String> columnNames = getColumnNames(fields);

            for (int colNum = 0; colNum < fields.length; colNum++) {
                Field field = fields[colNum];
                field.setAccessible(true);

                Cell cell = row.getCell(colNum);
                String columnName = columnNames.getOrDefault(colNum, "ستون ناشناخته");

                try {
                    Object value = getCellValue(cell, field.getType());
                    field.set(dtoInstance, value);
                } catch (Exception e) {
                    String errorMsg = "خطا در ردیف " + rowNum + "، ستون " + columnName + ": " + e.getMessage();
                    throw new RuntimeException(errorMsg, e);
                }
            }
            return dtoInstance;
        } catch (Exception e) {
            throw new RuntimeException("خطا در ایجاد نمونه DTO: " + e.getMessage(), e);
        }
    }

    private static Map<Integer, String> getColumnNames(Field[] fields) {
        Map<Integer, String> columnNames = new HashMap<>();
        for (int i = 0; i < fields.length; i++) {
            columnNames.put(i, fields[i].getName());
        }
        return columnNames;
    }

    private static Object getCellValue(Cell cell, Class<?> targetType) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }

       return switch (targetType.getCanonicalName()) {
            case "java.lang.String" -> convertCellToString(cell);
            case "java.lang.Long" -> convertCellToLong(cell);
            case "java.lang.Integer" -> convertCellToInteger(cell);
            case "java.lang.Double" -> convertCellToDouble(cell);
            case "java.lang.Float" -> convertCellToFloat(cell);
            case "java.math.BigDecimal" -> convertCellToBigDecimal(cell);
            case "java.lang.Boolean" -> convertCellToBoolean(cell);
            case "java.time.LocalDate" -> convertCellToDate(cell);
            default -> throw new RuntimeException("Unsupported type: " + targetType.getCanonicalName());
        };
    }


    private static String convertCellToString(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> cell.toString();
        };
    }

    private static Long convertCellToLong(Cell cell) {
        Double numericValue = getNumericCellValue(cell);
        return numericValue != null ? numericValue.longValue() : null;
    }

    private static Integer convertCellToInteger(Cell cell) {
        Double numericValue = getNumericCellValue(cell);
        return numericValue != null ? numericValue.intValue() : null;
    }

    private static Double convertCellToDouble(Cell cell) {
        return getNumericCellValue(cell);
    }

    private static Float convertCellToFloat(Cell cell) {
        Double numericValue = getNumericCellValue(cell);
        return numericValue != null ? numericValue.floatValue() : null;
    }

    private static BigDecimal convertCellToBigDecimal(Cell cell) {
        Double numericValue = getNumericCellValue(cell);
        return numericValue != null ? BigDecimal.valueOf(numericValue) : null;
    }

    private static Double getNumericCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                String cellValue = cell.getStringCellValue().trim();
                if (StringUtils.hasText(cellValue)) {
                    try {
                        return Double.parseDouble(cellValue);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("نمی‌توان مقدار عددی را تجزیه کرد: " + cellValue);
                    }
                }
                return null;
            default:
                throw new IllegalArgumentException("سلول حاوی داده‌های غیر عددی است");
        }
    }

    private static Boolean convertCellToBoolean(Cell cell) {
        switch (cell.getCellType()) {
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case STRING:
                String cellValue = cell.getStringCellValue().trim();
                if (StringUtils.hasText(cellValue)) {
                    return Boolean.parseBoolean(cellValue);
                }
                return null;
            default:
                throw new IllegalArgumentException("سلول حاوی داده‌های غیر منطقی است");
        }
    }

    private static LocalDate convertCellToDate(Cell cell) {
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        } else if (cell.getCellType() == CellType.STRING) {
            String cellValue = cell.getStringCellValue().trim();
            String[] parts = cellValue.split("-");
            if (parts.length == 3) {
                try {
                    int year = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]);
                    int day = Integer.parseInt(parts[2]);
                    DateConverter dateConverter = new DateConverter();
                    LocalDate jalaliDate = dateConverter.jalaliToGregorian(year, month, day);
                    if (jalaliDate != null) {
                        return LocalDate.of(jalaliDate.getYear(), jalaliDate.getMonthValue(), jalaliDate.getDayOfMonth());
                    } else {
                        throw new IllegalArgumentException("تاریخ نامعتبر است: " + cellValue);
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("فرمت تاریخ نامعتبر است: " + cellValue);
                }
            } else {
                throw new IllegalArgumentException("فرمت تاریخ نامعتبر است: " + cellValue);
            }
        } else {
            throw new IllegalArgumentException("سلول حاوی داده‌های تاریخ نامعتبر است");
        }
    }


}
