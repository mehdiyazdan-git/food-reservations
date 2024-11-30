package com.mapnaom.foodreservation.utils;

import com.mapnaom.foodreservation.dtos.ImportResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@RequiredArgsConstructor
public abstract class ExcelParser<DTO> {

    public static List<ExcelCellError> errors;


    /**
     * Parse a row to a DTO
     *
     * @param row
     * @return DTO
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
  public abstract  DTO parseRow(Row row) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException;

  protected void addError(ExcelCellError error) {
    errors.add(error);
  }

  public abstract ImportResponse<DTO> parseExcelFile(List<Row> rows) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException;



}
