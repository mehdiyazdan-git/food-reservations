package com.mapnaom.foodreservation.controllers;

import com.mapnaom.foodreservation.dtos.ImportResponse;
import com.mapnaom.foodreservation.services.FoodService;
import com.mapnaom.foodreservation.utils.ExcelCellError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

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
    public ResponseEntity<?> importFoodsFromExcel(
            @RequestParam("file") MultipartFile file) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (!Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            return new ResponseEntity<>("Invalid file type. Please upload an Excel file.", HttpStatus.BAD_REQUEST);
        }
                return new ResponseEntity<>(foodService.importFoodsFromExcel(file), HttpStatus.OK);
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
