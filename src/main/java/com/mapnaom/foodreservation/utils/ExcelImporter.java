package com.mapnaom.foodreservation.utils;

import com.mapnaom.foodreservation.dtos.ImportResponse;
import com.mapnaom.foodreservation.exceptions.ExcelDataImportException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

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
    public static <T> ImportResponse<T> importFromExcel(MultipartFile file, Class<T> clazz) throws ExcelDataImportException {
        Objects.requireNonNull(file, "File must not be null");
        Objects.requireNonNull(clazz, "Class type must not be null");

        ImportResponse<T> response = new ImportResponse<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (!rowIterator.hasNext()) {
                throw new ExcelDataImportException("Excel sheet is empty");
            }

            // Process header row
            Row headerRow = rowIterator.next();
            Map<String, Integer> headerMap = getHeaderMap(headerRow);

            // Retrieve fields and filter unsupported types recursively
            Map<String, Field> fieldMap = FieldProcessor.getFilteredFields(clazz);

            // Iterate over data rows
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                int rowIndex = row.getRowNum() + 1; // For error reporting
                try {
                    T instance = clazz.getDeclaredConstructor().newInstance();

                    for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
                        String fieldPath = entry.getKey();
                        Field field = entry.getValue();

                        // Determine the expected type for the field
                        Class<?> fieldType = getFieldType(clazz, fieldPath);

                        // Extract the value from the row
                        Object value = getValueFromRow(row, fieldPath, headerMap, fieldType);
                        if (value != null) {
                            setFieldValue(instance, fieldPath, value);
                        }
                    }

                    response.incrementSuccess(instance);
                } catch (Exception e) {
                    log.error("Error processing row {}: {}", rowIndex, e.getMessage());
                    response.incrementFailed();
                    response.addError(rowIndex, new ExcelCellError(e.getMessage()));
                }
            }

        } catch (IOException e) {
            log.error("IO Exception while reading Excel file: {}", e.getMessage());
            throw new ExcelDataImportException(e, "Failed to read Excel file");
        }

        return response;
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
     * Retrieves the expected field type based on the field path.
     *
     * @param clazz     the root class
     * @param fieldPath the dot-notated field path
     * @return the Class type of the field
     * @throws NoSuchFieldException if a field in the path does not exist
     */
    private static Class<?> getFieldType(Class<?> clazz, String fieldPath) throws NoSuchFieldException {
        String[] parts = fieldPath.split("\\.");
        Class<?> currentClass = clazz;

        for (String part : parts) {
            Field field = currentClass.getDeclaredField(part);
            currentClass = field.getType();

            // If the field is a collection, get its generic type
            if (Collection.class.isAssignableFrom(currentClass)) {
                Type genericType = field.getGenericType();
                if (genericType instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) genericType;
                    Type[] actualTypeArguments = pt.getActualTypeArguments();
                    if (actualTypeArguments.length == 1 && actualTypeArguments[0] instanceof Class) {
                        currentClass = (Class<?>) actualTypeArguments[0];
                    }
                }
            }
        }

        return currentClass;
    }

    /**
     * Retrieves a value from the row based on the field path.
     *
     * @param row       the Excel row
     * @param fieldPath the dot-notated field path (e.g., "foodOptions.price")
     * @param headerMap the header-to-column index map
     * @param type      the expected Java type of the field
     * @return the extracted value
     * @throws ExcelDataImportException if extraction fails
     */
    private static Object getValueFromRow(Row row, String fieldPath, Map<String, Integer> headerMap, Class<?> type) throws ExcelDataImportException {
        String[] parts = fieldPath.split("\\.");
        Object currentObject = null;

        // Traverse the field path to reach the target field
        // For simplicity, only handle the leaf field
        // Complex nested object creation can be implemented as needed
        String leafField = parts[parts.length - 1];

        Integer cellIndex = headerMap.get(leafField);
        if (cellIndex == null) {
            throw new ExcelDataImportException("Missing header for field: " + leafField);
        }

        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) {
            return null;
        }

        return parseCellValue(cell, type);
    }

    /**
     * Sets a value on an object based on the field path.
     *
     * @param instance  the object instance
     * @param fieldPath the dot-notated field path
     * @param value     the value to set
     * @throws IllegalAccessException    if field access fails
     * @throws NoSuchFieldException      if the field doesn't exist
     * @throws ExcelDataImportException  if any error occurs during setting the value
     * @throws InstantiationException    if instantiation of nested objects fails
     */
    private static void setFieldValue(Object instance, String fieldPath, Object value) throws IllegalAccessException, NoSuchFieldException, ExcelDataImportException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        String[] parts = fieldPath.split("\\.");
        Object currentObject = instance;

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            Field field = currentObject.getClass().getDeclaredField(part);
            field.setAccessible(true);

            if (i == parts.length - 1) {
                // Last part, set the value
                if (Collection.class.isAssignableFrom(field.getType())) {
                    // Handle collections
                    Collection<Object> collection = (Collection<Object>) field.get(currentObject);
                    if (collection == null) {
                        collection = instantiateCollection(field.getType());
                        field.set(currentObject, collection);
                    }
                    collection.add(value);
                } else {
                    field.set(currentObject, value);
                }
            } else {
                // Intermediate part, navigate or create the nested object
                Object nestedObject = field.get(currentObject);
                if (nestedObject == null) {
                    nestedObject = field.getType().getDeclaredConstructor().newInstance();
                    field.set(currentObject, nestedObject);
                }
                currentObject = nestedObject;
            }
        }
    }

    /**
     * Instantiates a collection based on its type.
     *
     * @param collectionType the collection class
     * @return an instance of the collection
     * @throws ExcelDataImportException if instantiation fails
     */
    private static Collection<Object> instantiateCollection(Class<?> collectionType) throws ExcelDataImportException {
        if (collectionType.isInterface()) {
            if (List.class.isAssignableFrom(collectionType)) {
                return new ArrayList<>();
            } else if (Set.class.isAssignableFrom(collectionType)) {
                return new LinkedHashSet<>();
            } else {
                throw new ExcelDataImportException("Unsupported collection type: " + collectionType.getName());
            }
        } else {
            try {
                return (Collection<Object>) collectionType.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new ExcelDataImportException(e, "Failed to instantiate collection type: " + collectionType.getName());
            }
        }
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
        if (type == null) {
            // Infer type based on cell type
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue().trim();
                case BOOLEAN:
                    return cell.getBooleanCellValue();
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getDateCellValue();
                    } else {
                        return cell.getNumericCellValue();
                    }
                case FORMULA:
                    return cell.getCellFormula();
                case BLANK:
                default:
                    return null;
            }
        }

        // Parse based on the specified type
        try {
            if (type.equals(String.class)) {
                return cell.getStringCellValue().trim();
            } else if (type.equals(Integer.class) || type.equals(int.class)) {
                return (int) cell.getNumericCellValue();
            } else if (type.equals(Long.class) || type.equals(long.class)) {
                return (long) cell.getNumericCellValue();
            } else if (type.equals(Double.class) || type.equals(double.class)) {
                return cell.getNumericCellValue();
            } else if (type.equals(Float.class) || type.equals(float.class)) {
                return (float) cell.getNumericCellValue();
            } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
                return cell.getBooleanCellValue();
            } else if (type.equals(LocalDate.class)) {
                Date date = cell.getDateCellValue();
                return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            } else if (type.equals(Date.class)) {
                return cell.getDateCellValue();
            }
            // Add more type parsers as needed
        } catch (Exception e) {
            throw new ExcelDataImportException(e, "Error parsing cell value: " + e.getMessage());
        }

        return null;
    }
}
