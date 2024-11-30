package com.mapnaom.foodreservation.services;

import com.mapnaom.foodreservation.dtos.FoodDto;
import com.mapnaom.foodreservation.dtos.ImportResponse;
import com.mapnaom.foodreservation.dtos.Select;
import com.mapnaom.foodreservation.entities.Food;
import com.mapnaom.foodreservation.exceptions.ExcelDataImportException;
import com.mapnaom.foodreservation.exceptions.ResourceNotFoundException;
import com.mapnaom.foodreservation.functionalInterfaces.ExcelReader;
import com.mapnaom.foodreservation.mappers.FoodMapper;
import com.mapnaom.foodreservation.repositories.FoodRepository;
import com.mapnaom.foodreservation.searchForms.FoodSearchForm;
import com.mapnaom.foodreservation.specifications.FoodSpecification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final FoodRepository foodRepository;
    private final FoodMapper foodMapper;

    Logger logger = LoggerFactory.getLogger(FoodService.class);

    /**
     * دریافت تمام غذاها به صورت صفحه‌بندی شده با شرایط جستجو
     *
     * @param searchForm فرم جستجو
     * @param pageable   اطلاعات صفحه‌بندی و مرتب‌سازی
     * @return صفحه‌ای از FoodDto
     */
    @Transactional(readOnly = true)
    public Page<FoodDto> findAll(FoodSearchForm searchForm, Pageable pageable) {
        Specification<Food> specification = FoodSpecification.getFoodSpecification(searchForm);
        Page<Food> foodPage = foodRepository.findAll(specification, pageable);
        List<FoodDto> foodDtos = foodPage.stream()
                .map(foodMapper::toDto)
                .collect(Collectors.toList());
        return new PageImpl<>(foodDtos, pageable, foodPage.getTotalElements());
    }

    public List<Select> selectList(String searchKey) {
        return foodRepository.findAll().stream()
                .map(i -> new Select(i.getId(), i.getName()))
                .collect(Collectors.toList());
    }

    /**
     * دریافت یک غذا بر اساس شناسه
     *
     * @param id شناسه غذا
     * @return FoodDto
     * @throws ResourceNotFoundException اگر غذا با شناسه داده شده یافت نشد
     */
    @Transactional(readOnly = true)
    public FoodDto findById(Long id) {
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("غذا با شناسه " + id + " یافت نشد."));
        return foodMapper.toDto(food);
    }

    /**
     * ایجاد یک غذای جدید
     *
     * @param foodDto داده‌های غذای جدید
     * @return FoodDto ایجاد شده
     */
    @Transactional
    public FoodDto create(FoodDto foodDto) {
        Food food = foodMapper.toEntity(foodDto);
        Food savedFood = foodRepository.save(food);
        return foodMapper.toDto(savedFood);
    }

    /**
     * به‌روزرسانی یک غذای موجود
     *
     * @param id      شناسه غذای مورد نظر برای به‌روزرسانی
     * @param foodDto داده‌های جدید برای به‌روزرسانی
     * @return FoodDto به‌روز شده
     * @throws ResourceNotFoundException اگر غذا با شناسه داده شده یافت نشد
     */
    @Transactional
    public FoodDto update(Long id, FoodDto foodDto) {
        Food existingFood = foodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("غذا با شناسه " + id + " یافت نشد."));
        foodMapper.partialUpdate(foodDto, existingFood);
        Food updatedFood = foodRepository.save(existingFood);
        return foodMapper.toDto(updatedFood);
    }

    /**
     * حذف یک غذا بر اساس شناسه
     *
     * @param id شناسه غذای مورد نظر برای حذف
     * @throws ResourceNotFoundException اگر غذا با شناسه داده شده یافت نشد
     */
    @Transactional
    public void delete(Long id) {
        if (!foodRepository.existsById(id)) {
            throw new ResourceNotFoundException("غذا با شناسه " + id + " یافت نشد.");
        }
        foodRepository.deleteById(id);
    }

    /**
     * Imports foods from an Excel file using a functional programming pipeline.
     *
     * @param file The uploaded Excel file.
     * @return ImportResponse detailing the import results.
     */
    @Transactional
    public ImportResponse<String, Map<String, String>, FoodDto> importFoodsFromExcel(MultipartFile file) {
        ImportResponse<String, Map<String, String>, FoodDto> importResponse = new ImportResponse<>();

        // Step 1: Initialize ExcelReader for FoodDto
        ExcelReader<File, FoodDto> excelReader = new ExcelReader<>(FoodDto.class);

        // Step 2: Convert MultipartFile to File (temporary file)
        File tempFile = convertMultipartFileToFile(file);
        if (tempFile == null) {
            String errorMsg = "Failed to convert MultipartFile to File.";
            importResponse.addErrorMessage(errorMsg);
            logger.error(errorMsg);
            return importResponse;
        }

        try {
            // Step 3: Apply the ExcelReader to parse the file
            ImportResponse<String, Map<String, String>, FoodDto> parseResponse = excelReader.apply(tempFile);

            // Step 4: Set error counts and messages from parseResponse
            importResponse.setFailCount(parseResponse.getFailCount().get());
            importResponse.setErrorMessages(parseResponse.getErrorMessages());
            importResponse.setFailedRecords(parseResponse.getFailedRecords());
            importResponse.setFailedRecordsMap(parseResponse.getFailedRecordsMap());

            // Step 5: Retrieve successfully parsed FoodDto records
            List<FoodDto> foodDtos = parseResponse.getSuccessRecords();

            if (!foodDtos.isEmpty()) {
                // Step 6: Map DTOs to Entities
                List<Food> foodsToSave = foodDtos.stream()
                        .map(foodMapper::toEntity)
                        .collect(Collectors.toList());

                // Step 7: Save all valid Food entities
                List<Food> savedFoods = foodRepository.saveAll(foodsToSave);

                // Step 8: Update success count
                importResponse.setSuccessCount(savedFoods.size());

                // Step 9: Validate save operation
                if (savedFoods.size() != foodsToSave.size()) {
                    int failedSaves = foodsToSave.size() - savedFoods.size();
                    importResponse.setFailCount(importResponse.getFailCount().get() + failedSaves);
                    importResponse.addErrorMessage("Mismatch in expected and actual saved records.");
                }
            }

        } catch (Exception e) {
            String errorMsg = "Error during Excel import: " + e.getMessage();
            importResponse.addErrorMessage(errorMsg);
            logger.error(errorMsg, e);
        } finally {
            // Step 10: Clean up temporary file
            if (tempFile.exists()) {
                boolean deleted = tempFile.delete();
                if (!deleted) {
                    logger.warn("Failed to delete temporary file: {}", tempFile.getAbsolutePath());
                }
            }
        }

        return importResponse;
    }

    /**
     * Converts a MultipartFile to a File.
     *
     * @param multipartFile The MultipartFile to convert.
     * @return The converted File.
     */

    private File convertMultipartFileToFile(MultipartFile multipartFile) {
        File tempFile = new File(System.getProperty("java.io.tmpdir"), Objects.requireNonNull(multipartFile.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
        } catch (IOException e) {
            logger.error("Error converting MultipartFile to File: {}", e.getMessage(), e);
            throw new ExcelDataImportException(e, "Failed to convert file.");
        }
        return tempFile;
    }
}