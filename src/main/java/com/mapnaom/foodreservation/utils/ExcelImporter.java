package com.mapnaom.foodreservation.utils;

import com.mapnaom.foodreservation.exceptions.ExcelDataImportException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public final class ExcelImporter {

    private ExcelImporter() {
        // Prevent instantiation
    }

    // Supported data types
    private static final Set<Class<?>> SUPPORTED_TYPES = Set.of(
            Boolean.class, Integer.class, String.class, Double.class, Float.class,
            Long.class, Short.class, Byte.class, Date.class, LocalDate.class,
            LocalTime.class, LocalDateTime.class
    );

    /**
     * Imports data from an Excel file into a list of objects of the specified class.
     *
     * @param file  the Excel file to import
     * @param clazz the class of the objects to create
     * @param <T>   the type parameter
     * @return a list of populated objects
     * @throws ExcelDataImportException if an error occurs during import
     */
    public static <T> List<T> importFromExcel(MultipartFile file, Class<T> clazz) throws ExcelDataImportException {
        Objects.requireNonNull(file, "File must not be null");
        Objects.requireNonNull(clazz, "Class type must not be null");

        List<T> resultList = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (!rowIterator.hasNext()) {
                throw new ExcelDataImportException("Excel sheet is empty");
            }

            // Process header row
            Row headerRow = rowIterator.next();
            Map<String, Integer> headerMap = getHeaderMap(headerRow);

            // Retrieve fields and filter unsupported types
            Field[] fields = clazz.getDeclaredFields();
            Map<String, Field> fieldMap = Arrays.stream(fields)
                    .filter(field -> !field.getName().equalsIgnoreCase("id"))
                    .filter(field -> SUPPORTED_TYPES.contains(field.getType()))
                    .collect(Collectors.toMap(Field::getName, field -> field));

            // Validate that all headers correspond to fields
            for (String header : headerMap.keySet()) {
                if (!fieldMap.containsKey(header)) {
                    log.warn("No matching field found for header: {}", header);
                }
            }

            // Iterate over data rows
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                T instance = clazz.getDeclaredConstructor().newInstance();

                for (Map.Entry<String, Integer> entry : headerMap.entrySet()) {
                    String headerName = entry.getKey();
                    int cellIndex = entry.getValue();

                    Field field = fieldMap.get(headerName);
                    if (field != null) {
                        field.setAccessible(true);
                        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        if (cell != null) {
                            try {
                                Object value = parseCellValue(cell, field.getType());
                                field.set(instance, value);
                            } catch (Exception e) {
                                log.error("Error setting value for field '{}' at row {}, column '{}': {}",
                                        field.getName(), row.getRowNum() + 1, headerName, e.getMessage());
                                throw new ExcelDataImportException(
                                        e, String.format("Error parsing cell at row %d, column '%s'",
                                                                                row.getRowNum() + 1, headerName));
                            }
                        }
                    }
                }
                resultList.add(instance);
            }

        } catch (IOException e) {
            log.error("IO Exception while reading Excel file: {}", e.getMessage());
            throw new ExcelDataImportException(e, "Failed to read Excel file");
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error("Reflection Exception: {}", e.getMessage());
            throw new ExcelDataImportException(e, "Failed to instantiate class: " + clazz.getName());
        }

        return resultList;
    }

    /**
     * Creates a map of header names to their corresponding column indices.
     *
     * @param headerRow the header row
     * @return a map where keys are header names and values are column indices
     */
    private static Map<String, Integer> getHeaderMap(Row headerRow) {
        Map<String, Integer> headerMap = new HashMap<>();
        for (Cell cell : headerRow) {
            String headerName = cell.getStringCellValue().trim();
            if (!headerName.isEmpty()) {
                headerMap.put(headerName, cell.getColumnIndex());
            }
        }
        return headerMap;
    }

    /**
     * Parses the cell value into the specified type.
     *
     * @param cell the cell to parse
     * @param type the target type
     * @return the parsed value
     * @throws ExcelDataImportException if the cell type is unsupported or parsing fails
     */
    private static Object parseCellValue(Cell cell, Class<?> type) throws ExcelDataImportException {
        if (type.equals(String.class)) {
            return getStringCellValue(cell);
        } else if (type.equals(Boolean.class)) {
            return getBooleanCellValue(cell);
        } else if (type.equals(Integer.class)) {
            return (int) getNumericCellValue(cell);
        } else if (type.equals(Double.class)) {
            return getNumericCellValue(cell);
        } else if (type.equals(Float.class)) {
            return (float) getNumericCellValue(cell);
        } else if (type.equals(Long.class)) {
            return (long) getNumericCellValue(cell);
        } else if (type.equals(Short.class)) {
            return (short) getNumericCellValue(cell);
        } else if (type.equals(Byte.class)) {
            return (byte) getNumericCellValue(cell);
        } else if (type.equals(Date.class)) {
            return getDateCellValue(cell);
        } else if (type.equals(LocalDate.class)) {
            return getLocalDateCellValue(cell);
        } else if (type.equals(LocalTime.class)) {
            return getLocalTimeCellValue(cell);
        } else if (type.equals(LocalDateTime.class)) {
            return getLocalDateTimeCellValue(cell);
        } else {
            throw new ExcelDataImportException("Unsupported field type: " + type.getName());
        }
    }


    private static String getStringCellValue(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> null;
        };
    }

    private static Boolean getBooleanCellValue(Cell cell) {
        return switch (cell.getCellType()) {
            case BOOLEAN -> cell.getBooleanCellValue();
            case STRING -> Boolean.parseBoolean(cell.getStringCellValue());
            case NUMERIC -> cell.getNumericCellValue() != 0;
            default -> null;
        };
    }

    private static double getNumericCellValue(Cell cell) {
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return Double.parseDouble(cell.getStringCellValue());
            } catch (NumberFormatException e) {
                throw new ExcelDataImportException(e, "Invalid numeric value: " + cell.getStringCellValue());
            }
        }
        throw new ExcelDataImportException("Cell type is not numeric or string for numeric field");
    }

    private static Date getDateCellValue(Cell cell) {
        if (DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue();
        }
        throw new ExcelDataImportException("Cell does not contain a valid date");
    }

    private static LocalDate getLocalDateCellValue(Cell cell) {
        Date date = getDateCellValue(cell);
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private static LocalTime getLocalTimeCellValue(Cell cell) {
        Date date = getDateCellValue(cell);
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
    }

    private static LocalDateTime getLocalDateTimeCellValue(Cell cell) {
        Date date = getDateCellValue(cell);
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
