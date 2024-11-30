package com.mapnaom.foodreservation.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generic DTO representing the response of an import operation.
 *
 * @param <ID>          The type of the record identifier (e.g., String for row numbers, Long for entity IDs).
 * @param <ErrorDetail> The type representing detailed error information (e.g., Map<String, String> for column-specific errors).
 * @param <D>           The type of successfully imported records (DTOs).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportResponse<ID, ErrorDetail, D> {
    /**
     * The number of successfully imported records.
     */
    private AtomicInteger successCount = new AtomicInteger(0);

    /**
     * The number of records that failed to import.
     */
    private AtomicInteger failCount = new AtomicInteger(0);

    /**
     * A list of successfully imported records.
     */
    private List<D> successRecords = new CopyOnWriteArrayList<>();

    /**
     * A list of general error messages not tied to specific records or columns.
     */
    private List<String> errorMessages = new CopyOnWriteArrayList<>();

    /**
     * A list of record identifiers that failed to import.
     */
    private List<ID> failedRecords = new CopyOnWriteArrayList<>();

    /**
     * A map detailing errors for each failed record.
     * Key: Record identifier (e.g., "Row 1", 1001L).
     * Value: List of error details (e.g., [{"columnName": "Error message"}, ...]).
     */
    private Map<ID, List<ErrorDetail>> failedRecordsMap = new ConcurrentHashMap<>();

    /**
     * Constructs an ImportResponse with initial success count and error messages.
     *
     * @param successCount  the number of successful imports
     * @param errorMessages the list of general error messages
     */
    public ImportResponse(int successCount, List<String> errorMessages) {
        this.successCount.set(successCount);
        this.errorMessages = errorMessages != null ? new CopyOnWriteArrayList<>(errorMessages) : new CopyOnWriteArrayList<>();
        this.failCount.set(0);
        this.failedRecords = new CopyOnWriteArrayList<>();
        this.failedRecordsMap = new ConcurrentHashMap<>();
        this.successRecords = new CopyOnWriteArrayList<>();
    }

    /**
     * Adds a successfully imported record.
     *
     * @param record the successfully imported record.
     */
    public void addSuccessRecord(D record) {
        if (record != null) {
            successRecords.add(record);
            successCount.incrementAndGet();
        }
    }

    /**
     * Adds a failed record with specific error details.
     *
     * @param recordId    the identifier of the failed record (e.g., "Row 1", 1001L)
     * @param errorDetail the detailed error information associated with the record
     */
    public void addFailedRecord(ID recordId, ErrorDetail errorDetail) {
        if (recordId == null) {
            throw new IllegalArgumentException("Record identifier cannot be null.");
        }
        if (errorDetail == null) {
            throw new IllegalArgumentException("Error detail cannot be null.");
        }

        // Initialize the list if the recordId is encountered for the first time
        failedRecordsMap.computeIfAbsent(recordId, k -> Collections.synchronizedList(new ArrayList<>()));

        // Add the error detail to the specific record
        failedRecordsMap.get(recordId).add(errorDetail);

        // Add to failedRecords list if not already present
        synchronized (failedRecords) {
            if (!failedRecords.contains(recordId)) {
                failedRecords.add(recordId);
            }
        }

        // Increment the fail count
        failCount.incrementAndGet();
    }

    /**
     * Adds a general error message not tied to a specific record or column.
     *
     * @param errorMessage the general error message to add
     */
    public void addErrorMessage(String errorMessage) {
        if (errorMessage != null && !errorMessage.trim().isEmpty()) {
            errorMessages.add(errorMessage);
            // Optionally, increment the fail count if a general error indicates a failure
            failCount.incrementAndGet();
        }
    }

    /**
     * Increments the success count by one.
     */
    public void incrementSuccessCount() {
        successCount.incrementAndGet();
    }

    /**
     * Sets the success count to a specific value.
     *
     * @param count the number of successful imports.
     */
    public void setSuccessCount(int count) {
        successCount.set(count);
    }

    /**
     * Sets the fail count to a specific value.
     *
     * @param count the number of failed imports.
     */
    public void setFailCount(int count) {
        failCount.set(count);
    }

    /**
     * Resets the ImportResponse to its initial state.
     */
    public void reset() {
        successCount.set(0);
        failCount.set(0);
        successRecords.clear();
        errorMessages.clear();
        failedRecords.clear();
        failedRecordsMap.clear();
    }

    /**
     * Provides a detailed string representation of failed records.
     *
     * @return a formatted string detailing failed records and their errors
     */
    public String getDetailedFailedRecords() {
        StringBuilder sb = new StringBuilder();
        failedRecordsMap.forEach((recordId, errors) -> {
            sb.append("Record [").append(recordId).append("]:\n");
            errors.forEach(error -> sb.append(" - ").append(error.toString()).append("\n"));
        });
        return sb.toString();
    }
}
