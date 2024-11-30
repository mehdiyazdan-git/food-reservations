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
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final FoodRepository foodRepository;
    private final FoodMapper foodMapper;

    private  ExcelReader<FoodDto> excelReader;


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
    public ImportResponse<FoodDto> importFoodsFromExcel(MultipartFile file) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ImportResponse<FoodDto> dtoList = excelReader.readExcel(file);
        List<Food> savedAll = foodRepository.saveAll(
                dtoList.getImportedRecords().stream()
                        .map(foodMapper::toEntity)
                        .collect(Collectors.toList())

        );


        return null;
    }

    /**
     * Converts a MultipartFile to a File for further processing or storage.
     * <p>
     * This method takes a MultipartFile, typically received in a web application
     * through file uploads, and writes its content to a temporary File stored in
     * the system's temporary directory. The generated File can then be used
     * for operations that require a standard File object.
     * </p>
     *
     * <p><strong>Usage Example:</strong></p>
     * <pre>
     * {@code
     * MultipartFile multipartFile = ...; // File received from a request
     * File file = convertMultipartFileToFile(multipartFile);
     * // Use the file (e.g., read, process, upload)
     * }
     * </pre>
     *
     * <p><strong>Important Notes:</strong></p>
     * <ul>
     *   <li>The file is created in the system's temporary directory, which may be cleared
     *       periodically or upon application shutdown.</li>
     *   <li>Ensure the returned File is deleted after use to avoid unnecessary resource usage.</li>
     *   <li>If the MultipartFile is empty or invalid, an exception will be thrown.</li>
     * </ul>
     *
     * @param multipartFile The MultipartFile to convert. Must not be null and must have valid content.
     * @return The converted File stored in the system's temporary directory.
     * @throws ExcelDataImportException if an error occurs during the conversion process.
     * @see MultipartFile
     * @see File
     */
    private File convertMultipartFileToFile(MultipartFile multipartFile) {
        File tempFile = new File(System.getProperty("java.io.tmpdir"),
                Objects.requireNonNull(multipartFile.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
        } catch (IOException e) {
            logger.error("Error converting MultipartFile to File: {}", e.getMessage(), e);
            throw new ExcelDataImportException(e, "Failed to convert file.");
        }
        return tempFile;
    }

}