package com.mapnaom.foodreservation.exceptions;

import com.mapnaom.foodreservation.utils.ExcelCellError;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Custom exception to handle parsing errors in Excel rows.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class ExcelParsingException extends RuntimeException {
    /**
     * -- GETTER --
     *  Retrieves the ExcelCellError associated with this exception.
     *
     * @return The ExcelCellError.
     */
    private final ExcelCellError error;

    /**
     * Constructs a new ExcelParsingException with the specified detail message and ExcelCellError.
     *
     * @param message The detail message.
     * @param error   The ExcelCellError containing error details.
     */
    public ExcelParsingException(String message, ExcelCellError error) {
        super(message);
        this.error = error;
    }

    /**
     * Constructs a new ExcelParsingException with the specified detail message, ExcelCellError, and cause.
     *
     * @param message The detail message.
     * @param error   The ExcelCellError containing error details.
     * @param cause   The cause of the exception.
     */
    public ExcelParsingException(String message, ExcelCellError error, Throwable cause) {
        super(message, cause);
        this.error = error;
    }

}
