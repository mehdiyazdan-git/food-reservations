package com.mapnaom.foodreservation.controllers;

import com.mapnaom.foodreservation.dtos.FoodDto;
import com.mapnaom.foodreservation.dtos.ImportResponse;
import com.mapnaom.foodreservation.services.FoodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
@Slf4j
public class FoodImportController {

    private final FoodService foodService;

    /**
     * Endpoint to import foods from an uploaded Excel file.
     *
     * @param file The uploaded Excel file containing food data.
     * @return ResponseEntity containing ImportResponse with import results.
     */
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportResponse<String, Map<String, String>, FoodDto>> importFoodsFromExcel(
            @RequestParam("file") MultipartFile file) {

        // Validate the uploaded file
        if (file.isEmpty()) {
            ImportResponse<String, Map<String, String>, FoodDto> response = new ImportResponse<>();
            response.addErrorMessage("Uploaded file is empty.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Optionally, validate the file type (e.g., Excel MIME types)
        String contentType = file.getContentType();
        if (!isExcelFile(contentType)) {
            ImportResponse<String, Map<String, String>, FoodDto> response = new ImportResponse<>();
            response.addErrorMessage("Invalid file type. Please upload an Excel file.");
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(response);
        }

        try {
            // Invoke the service method to import foods
            ImportResponse<String, Map<String, String>, FoodDto> importResponse = foodService.importFoodsFromExcel(file);

            // Determine HTTP status based on import results
            if (importResponse.getFailCount().get() > 0) {
                // Partial success: Some records failed to import
                return ResponseEntity.status(HttpStatus.MULTI_STATUS).body(importResponse);
            } else {
                // All records imported successfully
                return ResponseEntity.ok(importResponse);
            }

        } catch (Exception e) {
            // Handle unexpected exceptions gracefully
            log.error("Unexpected error during food import: {}", e.getMessage(), e);
            ImportResponse<String, Map<String,String>,FoodDto> response = new ImportResponse<>();
            response.addErrorMessage("An unexpected error occurred during import.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Validates if the provided content type corresponds to an Excel file.
     *
     * @param contentType The MIME type of the uploaded file.
     * @return True if the file is an Excel file, false otherwise.
     */
    private boolean isExcelFile(String contentType) {
        return contentType != null && (
                contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                contentType.equals("application/vnd.ms-excel")
        );
    }
}
