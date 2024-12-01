package com.mapnaom.foodreservation.dtos;

import com.mapnaom.foodreservation.utils.ExcelCellError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * DTO representing the response of an import operation specific to Excel parsing.
 * It contains the total number of successful and failed imports, and a map of errors encountered.
 * @param <T> the type of the imported data
 */
@Setter
@Getter
@RequiredArgsConstructor
public class ImportResponse<T> {
    private final AtomicLong totalSuccess = new AtomicLong(0);
    private final AtomicLong totalFailed = new AtomicLong(0);
    private final Map<Integer, List<ExcelCellError>> errors = new ConcurrentHashMap<>();
    private final List<T> successfulImports = new ArrayList<>(); // New field

    /**
     * Increment the total success count by one.
     */
    public void incrementSuccess(T instance) {
        totalSuccess.incrementAndGet();
        successfulImports.add(instance);
    }

    /**
     * Increment the total failed count by one.
     */
    public void incrementFailed() {
        totalFailed.incrementAndGet();
    }

    /**
     * Add an error to the errors map.
     *
     * @param rowIndex the row index where the error occurred
     * @param error the error to add
     */
    public void addError(int rowIndex, ExcelCellError error) {
        errors.computeIfAbsent(rowIndex, k -> new ArrayList<>()).add(error);
    }

    /**
     * Get a summary of the import process, including total successes, failures, and error details.
     *
     * @return a string summary of the import process
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Import Summary:\n")
                .append("Total Success: ").append(totalSuccess).append("\n")
                .append("Total Failed: ").append(totalFailed).append("\n");

        if (!errors.isEmpty()) {
            summary.append("Errors:\n");
            errors.forEach((row, errorList) -> {
                summary.append("Row ").append(row).append(":\n");
                errorList.forEach(error -> summary.append("  - ").append(error.getMessage()).append("\n"));
            });
        } else {
            summary.append("No errors encountered.\n");
        }

        return summary.toString();
    }
}


//ImportResponse
// fields: totalSuccess, totalFailed, errors
// methods: addError, getSummary
