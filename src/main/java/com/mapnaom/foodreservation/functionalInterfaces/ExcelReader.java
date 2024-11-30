package com.mapnaom.foodreservation.functionalInterfaces;


import com.mapnaom.foodreservation.dtos.ImportResponse;
import com.mapnaom.foodreservation.utils.ExcelCellError;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface ExcelReader<DTO> {
    /**
     * Reads the Excel file and returns a list of reservations
     * @param filePath the path of the Excel file
     * @return a list of reservations
     */
    ImportResponse<DTO> readExcel(MultipartFile filePath) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;

}
