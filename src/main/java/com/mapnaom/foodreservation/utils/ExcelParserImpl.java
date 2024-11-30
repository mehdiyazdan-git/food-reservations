package com.mapnaom.foodreservation.utils;

import com.mapnaom.foodreservation.dtos.ImportResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ExcelParserImpl<DTO> extends ExcelParser<DTO> {

    private final Class<DTO> clazz;

    @Override
    public DTO parseRow(Row row) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        DTO dtoInstance = clazz.getDeclaredConstructor().newInstance();
        Field[] fields = clazz.getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            Cell cell = row.getCell(i);

            if (cell != null) {
                try {
                    Object value = getCellValue(cell, field.getType());
                    field.set(dtoInstance, value);
                } catch (Exception e) {
                    addError(new ExcelCellError(e.getMessage(), row.getRowNum(), field.getName()));
                }
            } else {
                addError(new ExcelCellError("Cell is null", row.getRowNum(), field.getName()));
            }
        }

        return dtoInstance;
    }

    private Object getCellValue(Cell cell, Class<?> fieldType) {
        switch (cell.getCellType()) {
            case STRING:
                if (fieldType == String.class) {
                    return cell.getStringCellValue();
                }
                break;
            case NUMERIC:
                if (fieldType == Integer.class || fieldType == int.class) {
                    return (int) cell.getNumericCellValue();
                } else if (fieldType == Double.class || fieldType == double.class) {
                    return cell.getNumericCellValue();
                } else if (fieldType == Long.class || fieldType == long.class) {
                    return (long) cell.getNumericCellValue();
                }
                break;
            case BOOLEAN:
                if (fieldType == Boolean.class || fieldType == boolean.class) {
                    return cell.getBooleanCellValue();
                }
                break;
            default:
                return null;
        }
        return null;
    }

    @Override
    public ImportResponse<DTO> parseExcelFile(List<Row> rows) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<DTO> importedRecords = new ArrayList<>();
        errors = new ArrayList<>();

        for (Row row : rows) {
            DTO dto = parseRow(row);
            importedRecords.add(dto);
        }

        ImportResponse<DTO> response = new ImportResponse<>(importedRecords, errors);

        // Update totalSuccess and totalFailed counts
        response.getTotalSuccess().set(importedRecords.size() - errors.size());
        response.getTotalFailed().set(errors.size());

        // Optionally, you can populate the summary map or errorMap as needed

        return response;
    }
}