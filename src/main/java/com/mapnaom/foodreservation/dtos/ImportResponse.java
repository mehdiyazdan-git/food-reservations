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
 * It contains the total number of successful and failed imports, a map of errors encountered,
 * and a list of successfully imported top-level DTOs.
 *
 * @param <T> the type of the top-level imported data
 */
@Getter
@Setter
@RequiredArgsConstructor
public class ImportResponse<T> {
    /**
     * The total number of successfully imported records.
     */
    private final AtomicLong totalSuccess = new AtomicLong(0);

    /**
     * The total number of failed imports.
     */
    private final AtomicLong totalFailed = new AtomicLong(0);

    /**
     * A map of row indices to lists of errors encountered during import.
     * Key: Row number (1-based index).
     * Value: List of errors for that row.
     */
    private final Map<Integer, List<ExcelCellError>> errors = new ConcurrentHashMap<>();

    /**
     * A list of successfully imported top-level DTO instances.
     */
    private final List<T> successfulImports = new ArrayList<>();

    /**
     * Increments the success count and adds the imported instance to the list.
     *
     * @param instance the successfully imported DTO instance
     */
    public void incrementSuccess(T instance) {
        totalSuccess.incrementAndGet();
        successfulImports.add(instance);
    }

    /**
     * Increments the failed count by one.
     */
    public void incrementFailed() {
        totalFailed.incrementAndGet();
    }

    /**
     * Adds an error to the errors map for a specific row.
     *
     * @param rowIndex the row index where the error occurred (1-based)
     * @param error    the error to add
     */
    public void addError(int rowIndex, ExcelCellError error) {
        errors.computeIfAbsent(rowIndex, k -> new ArrayList<>()).add(error);
    }

    /**
     * Generates a summary of the import process, including total successes, failures, and detailed errors.
     *
     * @return a string summarizing the import results
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Import Summary:\n")
                .append("Total Success: ").append(totalSuccess.get()).append("\n")
                .append("Total Failed: ").append(totalFailed.get()).append("\n");

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
