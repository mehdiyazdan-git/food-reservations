package com.mapnaom.foodreservation.exceptions;

public class ExcelDataImportException extends RuntimeException{
    public ExcelDataImportException(Exception e,String message) {
        super(message, e);
    }
    public ExcelDataImportException(String message) {
        super(message);
    }
}
