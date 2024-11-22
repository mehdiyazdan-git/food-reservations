package com.mapnaom.foodreservation.services;

import com.mapnaom.foodreservation.dtos.FoodDto;
import com.mapnaom.foodreservation.dtos.FoodImportResponse;
import com.mapnaom.foodreservation.entities.Food;
import com.mapnaom.foodreservation.exceptions.ResourceNotFoundException;
import com.mapnaom.foodreservation.mappers.FoodMapper;
import com.mapnaom.foodreservation.repositories.FoodRepository;
import com.mapnaom.foodreservation.searchForms.FoodSearchForm;
import com.mapnaom.foodreservation.specifications.FoodSpecification;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodService {
    private final FoodRepository foodRepository;
    private final FoodMapper foodMapper;

    /**
     * پیدا کردن تمام غذاها به صورت صفحه‌بندی شده با شرایط جستجو
     *
     * @param searchForm فرم جستجو شامل فیلدهای id و name
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

    /**
     * پیدا کردن یک غذا بر اساس شناسه
     *
     * @param id شناسه غذا
     * @return FoodDto
     * @throws ResourceNotFoundException اگر غذا با شناسه داده شده یافت نشد
     */
    public FoodDto findById(Long id) {
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found with id: " + id));
        return foodMapper.toDto(food);
    }

    /**
     * ایجاد یک غذا جدید
     *
     * @param foodDto داده‌های غذا جدید
     * @return FoodDto ایجاد شده
     */
    public FoodDto create(FoodDto foodDto) {
        Food food = foodMapper.toEntity(foodDto);
        Food savedFood = foodRepository.save(food);
        return foodMapper.toDto(savedFood);
    }

    /**
     * به‌روزرسانی یک غذا موجود
     *
     * @param id شناسه غذای مورد نظر برای به‌روزرسانی
     * @param foodDto داده‌های جدید برای به‌روزرسانی
     * @return FoodDto به‌روز شده
     * @throws ResourceNotFoundException اگر غذا با شناسه داده شده یافت نشد
     */
    public FoodDto update(Long id, FoodDto foodDto) {
        Food existingFood = foodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found with id: " + id));

        // استفاده از Mapper برای به‌روزرسانی فیلدهای موجودیت
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
    public void delete(Long id) {
        if (!foodRepository.existsById(id)) {
            throw new ResourceNotFoundException("Food not found with id: " + id);
        }
        foodRepository.deleteById(id);
    }

    /**
     * واردسازی غذاها از طریق فایل اکسل
     *
     * @param file فایل اکسل حاوی اطلاعات غذاها
     * @return پاسخ شامل تعداد رکوردهای موفق و خطاهای احتمالی
     */
    @Transactional
    public FoodImportResponse importFoodsFromExcel(MultipartFile file) {
        int successCount = 0;
        List<String> errorMessages = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0); // فرض بر این است که اطلاعات در اولین شیت قرار دارد
            Iterator<Row> rows = sheet.iterator();

            // فرض بر این است که اولین ردیف شامل عنوان ستون‌ها است
            if (rows.hasNext()) {
                rows.next(); // رد کردن ردیف عنوان
            }

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                try {
                    Cell idCell = currentRow.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    Cell nameCell = currentRow.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                    // خواندن مقدار ID (اختیاری)
                    Long id = null;
                    if (idCell.getCellType() == CellType.NUMERIC) {
                        id = (long) idCell.getNumericCellValue();
                    } else if (idCell.getCellType() == CellType.STRING) {
                        String idStr = idCell.getStringCellValue();
                        if (!idStr.isEmpty()) {
                            id = Long.parseLong(idStr);
                        }
                    }

                    // خواندن مقدار Name
                    String name = "";
                    if (nameCell.getCellType() == CellType.STRING) {
                        name = nameCell.getStringCellValue().trim();
                    } else if (nameCell.getCellType() == CellType.NUMERIC) {
                        name = String.valueOf(nameCell.getNumericCellValue()).trim();
                    }

                    if (name.isEmpty()) {
                        throw new IllegalArgumentException("نام غذا نمی‌تواند خالی باشد.");
                    }

                    Food food;
                    if (id != null) {
                        // بررسی وجود غذا با ID مشخص شده
                        food = foodRepository.findById(id).orElse(new Food());
                        food.setId(id);
                    } else {
                        food = new Food();
                    }

                    food.setName(name);
                    foodRepository.save(food);
                    successCount++;
                } catch (Exception e) {
                    errorMessages.add("ردیف " + (currentRow.getRowNum() + 1) + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("خطا در پردازش فایل اکسل: " + e.getMessage());
        }

        return new FoodImportResponse(successCount, errorMessages);
    }
}
