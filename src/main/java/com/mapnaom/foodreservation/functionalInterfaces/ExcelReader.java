package com.mapnaom.foodreservation.functionalInterfaces;

import com.mapnaom.foodreservation.dtos.ImportResponse;
import com.mapnaom.foodreservation.exceptions.ExcelDataImportException;
import com.mapnaom.foodreservation.utils.DateConvertor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * A generic ExcelReader that parses Excel files into DTOs using a functional programming approach.
 *
 * @param <F> The type of the file to be read, extending {@link File}.
 * @param <D> The type of the DTO to map the Excel rows to.
 */
@Slf4j
public class ExcelReader<F extends File, D> implements Function<F, ImportResponse<String, Map<String, String>, D>> {

    private final Class<D> dtoClass;
    private final Map<Integer, Function<Cell, Object>> cellParserMap;
    private final Map<Integer, String> columnIndexToHeaderMap;
    private final Map<String, Field> fieldMap;

    /**
     * Constructs an ExcelReader for the specified DTO class.
     *
     * @param dtoClass The class of the DTO to map the Excel rows to.
     */
    public ExcelReader(Class<D> dtoClass) {
        this.dtoClass = dtoClass;
        this.cellParserMap = initializeCellParsers();
        this.fieldMap = initializeFieldMap();
        this.columnIndexToHeaderMap = new HashMap<>();
    }

    /**
     * Initializes a map of column indices to header names based on the DTO fields.
     *
     * @return Map of column index to header name.
     */
    private Map<Integer, String> initializeColumnIndexToHeaderMap(List<String> headers) {
        Map<Integer, String> map = new HashMap<>();
        for (int i = 0; i < headers.size(); i++) {
            map.put(i, headers.get(i));
        }
        return map;
    }

    /**
     * Initializes a map of DTO field names to Field objects for quick access.
     *
     * @return Map of field name to Field object.
     */
    private Map<String, Field> initializeFieldMap() {
        return Arrays.stream(dtoClass.getDeclaredFields())
                .collect(Collectors.toMap(Field::getName, field -> {
                    field.setAccessible(true);
                    return field;
                }));
    }

    /**
     * Initializes cell parsers for each column index based on the DTO field types.
     *
     * @return Map of column index to cell parser function.
     */
    private Map<Integer, Function<Cell, Object>> initializeCellParsers() {
        Map<Integer, Function<Cell, Object>> parsers = new HashMap<>();
        Field[] fields = dtoClass.getDeclaredFields();

        List<Field> importFields = Arrays.stream(fields).filter(field -> !field.getName().equals("id")).toList();

        for (int i = 0; i < importFields.size(); i++) {
            Class<?> fieldType = fields[i].getType();
            parsers.put(i, createCellParser(fieldType));
        }

        return parsers;
    }

    /**
     * Creates a cell parser function based on the field type.
     *
     * @param fieldType The type of the field.
     * @return A function that parses a Cell into an Object of the field type.
     */
    private Function<Cell, Object> createCellParser(Class<?> fieldType) {
        if (fieldType.equals(String.class)) {
            return this::parseStringCell;
        } else if (fieldType.equals(Long.class) || fieldType.equals(long.class)) {
            return this::parseLongCell;
        } else if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
            return this::parseIntegerCell;
        } else if (fieldType.equals(Double.class) || fieldType.equals(double.class)) {
            return this::parseDoubleCell;
        } else if (fieldType.equals(Float.class) || fieldType.equals(float.class)) {
            return this::parseFloatCell;
        } else if (fieldType.equals(BigDecimal.class)) {
            return this::parseBigDecimalCell;
        } else if (fieldType.equals(Boolean.class) || fieldType.equals(boolean.class)) {
            return this::parseBooleanCell;
        } else if (fieldType.equals(LocalDate.class)) {
            return this::parseDateCell;
        } else {
            log.warn("Unsupported field type: {}", fieldType.getName());
            return cell -> null;
        }
    }

    /**
     * Parses a cell into a String.
     *
     * @param cell The Excel cell.
     * @return The cell value as a String.
     */
    private String parseStringCell(Cell cell) {
        if (cell == null) {
            return null;
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            case BLANK, ERROR, _NONE -> null;
        };
    }

    /**
     * Parses a cell into a Long.
     *
     * @param cell The Excel cell.
     * @return The cell value as a Long.
     */
    private Long parseLongCell(Cell cell) {
        Double numericValue = getNumericCellValue(cell);
        return numericValue != null ? numericValue.longValue() : null;
    }

    /**
     * Parses a cell into an Integer.
     *
     * @param cell The Excel cell.
     * @return The cell value as an Integer.
     */
    private Integer parseIntegerCell(Cell cell) {
        Double numericValue = getNumericCellValue(cell);
        return numericValue != null ? numericValue.intValue() : null;
    }

    /**
     * Parses a cell into a Double.
     *
     * @param cell The Excel cell.
     * @return The cell value as a Double.
     */
    private Double parseDoubleCell(Cell cell) {
        return getNumericCellValue(cell);
    }

    /**
     * Parses a cell into a Float.
     *
     * @param cell The Excel cell.
     * @return The cell value as a Float.
     */
    private Float parseFloatCell(Cell cell) {
        Double numericValue = getNumericCellValue(cell);
        return numericValue != null ? numericValue.floatValue() : null;
    }

    /**
     * Parses a cell into a BigDecimal.
     *
     * @param cell The Excel cell.
     * @return The cell value as a BigDecimal.
     */
    private BigDecimal parseBigDecimalCell(Cell cell) {
        Double numericValue = getNumericCellValue(cell);
        return numericValue != null ? BigDecimal.valueOf(numericValue) : null;
    }

    /**
     * Parses a cell into a Boolean.
     *
     * @param cell The Excel cell.
     * @return The cell value as a Boolean.
     */
    private Boolean parseBooleanCell(Cell cell) {
        if (cell == null) {
            return null;
        }
        return switch (cell.getCellType()) {
            case BOOLEAN -> cell.getBooleanCellValue();
            case STRING -> {
                String val = cell.getStringCellValue().trim().toLowerCase();
                yield switch (val) {
                    case "true", "yes", "1" -> true;
                    case "false", "no", "0" -> false;
                    default -> null;
                };
            }
            case NUMERIC -> cell.getNumericCellValue() != 0;
            default -> null;
        };
    }

    /**
     * Parses a cell into a LocalDate.
     *
     * @param cell The Excel cell.
     * @return The cell value as a LocalDate.
     */
    private LocalDate parseDateCell(Cell cell) {
        if (cell == null) {
            return null;
        }

        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        } else if (cell.getCellType() == CellType.STRING) {
            String dateStr = cell.getStringCellValue().trim();
            if (isJalaliDate(dateStr)) {
                return DateConvertor.convertJalaliToGregorian(dateStr);
            } else {
                // Attempt to parse as Gregorian date in yyyy-MM-dd format
                try {
                    return LocalDate.parse(dateStr);
                } catch (Exception e) {
                    log.error("Invalid date format: {}", dateStr);
                    return null;
                }
            }
        } else {
            log.error("Unsupported date cell type: {}", cell.getCellType());
            return null;
        }
    }

    /**
     * Checks if a string is in Jalali date format (yyyy/MM/dd).
     *
     * @param dateStr The date string.
     * @return True if Jalali date format, false otherwise.
     */
    private boolean isJalaliDate(String dateStr) {
        return dateStr.matches("\\d{4}/\\d{2}/\\d{2}");
    }

    /**
     * Retrieves the numeric value of a cell, handling different cell types.
     *
     * @param cell The Excel cell.
     * @return The numeric value as a Double, or null if not applicable.
     */
    private Double getNumericCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                String val = cell.getStringCellValue().trim();
                try {
                    return Double.parseDouble(val);
                } catch (NumberFormatException e) {
                    log.error("Unable to parse numeric value from string: {}", val);
                    return null;
                }
            case FORMULA:
                try {
                    return cell.getNumericCellValue();
                } catch (Exception e) {
                    log.error("Unable to parse numeric value from formula: {}", cell.getCellFormula());
                    return null;
                }
            default:
                return null;
        }
    }

    /**
     * Parses a single row into a DTO instance.
     *
     * @param row                   The Excel row.
     * @param columnIndexToHeaderMap Map of column index to header name.
     * @return The populated DTO instance, or null if the row is empty.
     */
    private D parseRow(Row row, Map<Integer, String> columnIndexToHeaderMap) {
        try {
            D dto = dtoClass.getDeclaredConstructor().newInstance();
            boolean isRowEmpty = true;

            for (Map.Entry<Integer, String> entry : columnIndexToHeaderMap.entrySet()) {
                int colIndex = entry.getKey();
                String header = entry.getValue();

                Cell cell = row.getCell(colIndex);
                Object cellValue = cellParserMap.getOrDefault(colIndex, this::parseStringCell).apply(cell);

                if (cellValue != null) {
                    isRowEmpty = false;
                }

                Field field = fieldMap.get(header);
                if (field != null) {
                    field.set(dto, cellValue);
                } else {
                    log.warn("No matching field found for header: {}", header);
                }
            }

            return isRowEmpty ? null : dto;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            log.error("Error instantiating DTO class: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Parses the header row to extract column names.
     *
     * @param headerRow The header row.
     * @return List of header names.
     */
    private List<String> parseHeaders(Row headerRow) {
        List<String> headers = new ArrayList<>();
        int lastCell = headerRow.getLastCellNum();
        for (int i = 0; i < lastCell; i++) {
            Cell cell = headerRow.getCell(i);
            if (cell == null) {
                headers.add("column_" + i);
            } else {
                headers.add(cell.getStringCellValue().trim());
            }
        }
        return headers;
    }

    /**
     * Applies the Excel reading and parsing process to the provided file.
     *
     * @param file The Excel file to read.
     * @return An ImportResponse containing successfully parsed DTOs and any errors encountered.
     */
    @Override
    public ImportResponse<String, Map<String, String>, D> apply(F file) {
        ImportResponse<String, Map<String, String>, D> importResponse = new ImportResponse<>();

        try (Workbook workbook = new XSSFWorkbook(file)) {
            if (workbook.getNumberOfSheets() == 0) {
                throw new ExcelDataImportException("Excel file contains no sheets.");
            }

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new ExcelDataImportException("Excel sheet is empty.");
            }

            List<String> headers = parseHeaders(headerRow);
            Map<Integer, String> columnIndexToHeader = initializeColumnIndexToHeaderMap(headers);

            Iterable<Row> rows = () -> sheet.iterator();
            List<D> dtoList = StreamSupport.stream(rows.spliterator(), false)
                    .skip(1) // Skip header row
                    .map(row -> {
                        try {
                            D dto = parseRow(row, columnIndexToHeader);
                            if (dto != null) {
                                importResponse.addSuccessRecord(dto);
                            }
                            return dto;
                        } catch (Exception e) {
                            String recordIdentifier = "Row " + (row.getRowNum() + 1);
                            Map<String, String> errorDetail = Map.of("Parsing Error", e.getMessage());
                            importResponse.addFailedRecord(recordIdentifier, errorDetail);
                            log.error("Error parsing row {}: {}", row.getRowNum() + 1, e.getMessage(), e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();
            importResponse.setSuccessRecords(dtoList);

            // Success count is already managed by addSuccessRecord
            // Additional processing or validation can be done here if needed

        } catch (IOException e) {
            String errorMsg = "Error reading Excel file: " + e.getMessage();
            importResponse.addErrorMessage(errorMsg);
            log.error(errorMsg, e);
        } catch (ExcelDataImportException e) {
            String errorMsg = "Excel import error: " + e.getMessage();
            importResponse.addErrorMessage(errorMsg);
            log.error(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Unexpected error during Excel import: " + e.getMessage();
            importResponse.addErrorMessage(errorMsg);
            log.error(errorMsg, e);
        }

        return importResponse;
    }
}
