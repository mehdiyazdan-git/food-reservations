package com.mapnaom.foodreservation.services;

import com.mapnaom.foodreservation.dtos.FoodOptionDto;
import com.mapnaom.foodreservation.dtos.ImportResponse;
import com.mapnaom.foodreservation.entities.FoodOption;
import com.mapnaom.foodreservation.exceptions.ExcelDataImportException;
import com.mapnaom.foodreservation.exceptions.ResourceNotFoundException;
import com.mapnaom.foodreservation.mappers.FoodOptionMapper;
import com.mapnaom.foodreservation.repositories.FoodOptionRepository;
import com.mapnaom.foodreservation.searchForms.FoodOptionSearchForm;
import com.mapnaom.foodreservation.specifications.FoodOptionSpecification;
import com.mapnaom.foodreservation.utils.ExcelDataExporter;
import com.mapnaom.foodreservation.utils.ExcelDataImporter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodOptionService {

    private final FoodOptionRepository foodOptionRepository;

    private final FoodOptionMapper foodOptionMapper;

    /**
     * پیدا کردن تمام گزینه‌های غذایی به صورت صفحه‌بندی شده با شرایط جستجو
     *
     * @param searchForm فرم جستجو
     * @param pageable   اطلاعات صفحه‌بندی و مرتب‌سازی
     * @return صفحه‌ای از FoodOptionDto
     */
    @Transactional(readOnly = true)
    public Page<FoodOptionDto> findAll(FoodOptionSearchForm searchForm, Pageable pageable) {
        Specification<FoodOption> specification = FoodOptionSpecification.getFoodOptionSpecification(searchForm);
        Page<FoodOption> foodOptionPage = foodOptionRepository.findAll(specification, pageable);
        List<FoodOptionDto> foodOptionDtos = foodOptionPage.stream()
                .map(foodOptionMapper::toDto)
                .collect(Collectors.toList());
        return new PageImpl<>(foodOptionDtos, pageable, foodOptionPage.getTotalElements());
    }

    /**
     * پیدا کردن یک گزینه غذایی بر اساس شناسه
     *
     * @param id شناسه گزینه غذایی
     * @return FoodOptionDto
     * @throws ResourceNotFoundException اگر گزینه غذایی با شناسه داده شده یافت نشد
     */
    @Transactional(readOnly = true)
    public FoodOptionDto findById(Long id) {
        FoodOption foodOption = foodOptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("گزینه غذایی با شناسه " + id + " یافت نشد."));
        return foodOptionMapper.toDto(foodOption);
    }

    /**
     * ایجاد یک گزینه غذایی جدید
     *
     * @param foodOptionDto داده‌های گزینه غذایی جدید
     * @return FoodOptionDto ایجاد شده
     */
    @Transactional
    public FoodOptionDto create(FoodOptionDto foodOptionDto) {
        FoodOption foodOption = foodOptionMapper.toEntity(foodOptionDto);
        FoodOption savedFoodOption = foodOptionRepository.save(foodOption);
        return foodOptionMapper.toDto(savedFoodOption);
    }

    /**
     * به‌روزرسانی یک گزینه غذایی موجود
     *
     * @param id            شناسه گزینه غذایی مورد نظر برای به‌روزرسانی
     * @param foodOptionDto داده‌های جدید برای به‌روزرسانی
     * @return FoodOptionDto به‌روز شده
     * @throws ResourceNotFoundException اگر گزینه غذایی با شناسه داده شده یافت نشد
     */
    @Transactional
    public FoodOptionDto update(Long id, FoodOptionDto foodOptionDto) {
        FoodOption existingFoodOption = foodOptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("گزینه غذایی با شناسه " + id + " یافت نشد."));
        foodOptionMapper.partialUpdate(foodOptionDto, existingFoodOption);
        FoodOption updatedFoodOption = foodOptionRepository.save(existingFoodOption);
        return foodOptionMapper.toDto(updatedFoodOption);
    }

    /**
     * حذف یک گزینه غذایی بر اساس شناسه
     *
     * @param id شناسه گزینه غذایی مورد نظر برای حذف
     * @throws ResourceNotFoundException اگر گزینه غذایی با شناسه داده شده یافت نشد
     */
    @Transactional
    public void delete(Long id) {
        if (!foodOptionRepository.existsById(id)) {
            throw new ResourceNotFoundException("گزینه غذایی با شناسه " + id + " یافت نشد.");
        }
        foodOptionRepository.deleteById(id);
    }
    /**
     * وارد کردن گزینه‌های غذایی از فایل Excel
     *
     * @param file فایل Excel حاوی داده‌های گزینه‌های غذایی
     * @return پاسخ وارد کردن شامل تعداد موفقیت‌آمیز و پیام‌های خطا
     */
    @Transactional
    public ImportResponse importFoodOptionsFromExcel(MultipartFile file) {
        int successCount = 0;
        List<String> errorMessages = new ArrayList<>();

        try {
            // استفاده از ExcelDataImporter برای وارد کردن داده‌ها
            List<FoodOptionDto> foodOptionDtos = ExcelDataImporter.importData(file, FoodOptionDto.class);

            for (FoodOptionDto dto : foodOptionDtos) {
                try {
                    // تبدیل DTO به موجودیت و ذخیره در پایگاه داده
                    FoodOption foodOption = foodOptionMapper.toEntity(dto);
                    foodOptionRepository.save(foodOption);
                    successCount++;
                } catch (Exception e) {
                    String errorMsg = "خطا در ذخیره گزینه غذایی: " + dto.getFoodName() + " - " + e.getMessage();
                    errorMessages.add(errorMsg);
                }
            }
        } catch (ExcelDataImportException e) {
            // خطاهای مربوط به خواندن فایل Excel
            errorMessages.add("خطا در وارد کردن داده‌ها از فایل Excel: " + e.getMessage());
        }

        return new ImportResponse(successCount, errorMessages);
    }
    public byte[] exportCustomersToExcel() throws IOException {
        List<FoodOptionDto> customerDtos = foodOptionRepository.findAll().stream().map(foodOptionMapper::toDto)
                .collect(Collectors.toList());
        return ExcelDataExporter.exportData(customerDtos, FoodOptionDto.class);
    }
}
