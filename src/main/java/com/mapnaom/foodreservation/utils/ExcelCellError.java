package com.mapnaom.foodreservation.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@RequiredArgsConstructor
public class ExcelCellError extends Throwable implements Serializable {
    private String message;
    private Integer row;
    private String columnName;


    public ExcelCellError(String message, Integer row, String columnName) {
        this.message = message;
        this.row = row;
        this.columnName = columnName;
    }

    public ExcelCellError(String message) {
        this.message = message;
    }

}
