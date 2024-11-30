package com.mapnaom.foodreservation.functionalInterfaces;

import com.mapnaom.foodreservation.dtos.ImportResponse;
import com.mapnaom.foodreservation.utils.ExcelParserImpl;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ExcelReaderImpl<DTO> implements ExcelReader<DTO> {

    private final ExcelParserImpl<DTO> excelParserImpl;

    /**
     * Reads an Excel file and returns an ImportResponse containing imported records and errors.
     *
     * @param multipartFile the Excel file to read
     * @return ImportResponse containing imported records and errors
     */
    @Override
    public ImportResponse<DTO> readExcel(MultipartFile multipartFile) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        XSSFWorkbook workbook = new XSSFWorkbook(multipartFile.getInputStream());
        XSSFSheet sheet = workbook.getSheetAt(0);

        List<Row> rows = new ArrayList<>();

        int firstRowNum = sheet.getFirstRowNum();
        int lastRowNum = sheet.getLastRowNum();

        // Collect all rows from the sheet
        for (int i = firstRowNum; i <= lastRowNum; i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                rows.add(row);
            }
        }

        // Use parseExcelFile method to process all rows
        ImportResponse<DTO> importResponse = excelParserImpl.parseExcelFile(rows);

        workbook.close();

        return importResponse;
    }
}