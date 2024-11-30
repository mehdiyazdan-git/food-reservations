package com.mapnaom.foodreservation.utils;

import com.mapnaom.foodreservation.exceptions.ExcelDataImportException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelDataImporter {

    private static final Logger logger = LogManager.getLogger(ExcelDataImporter.class);



    public static <T> List<T> importData(MultipartFile file, Class<T> dtoClass) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("پرونده ارائه شده خالی است.");
        }

        List<T> dtos = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            if (workbook.getNumberOfSheets() == 0) {
                throw new ExcelDataImportException("فایل Excel هیچ شیتی ندارد.");
            }

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                int rowNum = row.getRowNum();

                // Skip the header row
                if (rowNum == 0) {
                    continue;
                }

                try {
                    T dto = ExcelRowParser.parseRowToDto(row, dtoClass, rowNum + 1);
                    dtos.add(dto);
                } catch (Exception e) {
                    String errorMsg = "خطا در ردیف " + (rowNum + 1) + ": " + e.getMessage();
                    logger.error(errorMsg, e);
                    throw new ExcelDataImportException(e, errorMsg);
                }
            }
        } catch (IOException e) {
            String errorMsg = "خطا در خواندن فایل Excel: " + e.getMessage();
            logger.error(errorMsg, e);
            throw new ExcelDataImportException(e, errorMsg);
        } catch (ExcelDataImportException e) {
            throw new RuntimeException(e);
        }

        return dtos;
    }

    public static Workbook createWorkbook(MultipartFile file) {
        try {
            return new XSSFWorkbook(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Sheet getFirstSheet(Workbook sheets) {
        return sheets.getSheetAt(0);
    }

    public static List<Row> getDataRows(Sheet sheet) {
        List<Row> rowList = new ArrayList<>();
        for (Row row : sheet) {
            if (row.getCell(0) != null) {
                rowList.add(row);
            }
        }
        return rowList;
    }
}
