package com.mapnaom.foodreservation.controllers;

import com.mapnaom.foodreservation.dtos.FoodOptionDto;
import com.mapnaom.foodreservation.dtos.ImportResponse;
import com.mapnaom.foodreservation.searchForms.FoodOptionSearchForm;
import com.mapnaom.foodreservation.services.FoodOptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * کنترلر برای مدیریت عملیات‌های مربوط به گزینه‌های غذایی
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/food-options")
@RequiredArgsConstructor
public class FoodOptionController {

    private final FoodOptionService foodOptionService;

    /**
     * دریافت تمام گزینه‌های غذایی به صورت صفحه‌بندی شده با شرایط جستجو
     *
     * @param page       شماره صفحه (پیش‌فرض: 0)
     * @param size       تعداد آیتم‌ها در هر صفحه (پیش‌فرض: 10)
     * @param sortBy     فیلدی که بر اساس آن مرتب‌سازی می‌شود (پیش‌فرض: "id")
     * @param order      نوع مرتب‌سازی (ASC یا DESC) (پیش‌فرض: "ASC")
     * @param searchForm فرم جستجو شامل فیلدهای id، name، price و غیره
     * @return صفحه‌ای از FoodOptionDto
     */
    @GetMapping
    public ResponseEntity<Page<FoodOptionDto>> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "order", defaultValue = "ASC") String order,
            @ModelAttribute FoodOptionSearchForm searchForm
    ) {
        Sort.Direction sortDirection = order.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<FoodOptionDto> foodOptionPage = foodOptionService.findAll(searchForm, pageable);
        return ResponseEntity.ok(foodOptionPage);
    }

    /**
     * دریافت یک گزینه غذایی بر اساس شناسه
     *
     * @param id شناسه گزینه غذایی
     * @return FoodOptionDto
     */
    @GetMapping("/{id}")
    public ResponseEntity<FoodOptionDto> findById(@PathVariable Long id) {
        FoodOptionDto foodOption = foodOptionService.findById(id);
        return ResponseEntity.ok(foodOption);
    }

    /**
     * ایجاد یک گزینه غذایی جدید
     *
     * @param foodOptionDto داده‌های گزینه غذایی جدید
     * @return FoodOptionDto ایجاد شده
     */
    @PostMapping
    public ResponseEntity<FoodOptionDto> create(@Valid @RequestBody FoodOptionDto foodOptionDto) {
        FoodOptionDto createdFoodOption = foodOptionService.create(foodOptionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFoodOption);
    }

    /**
     * به‌روزرسانی یک گزینه غذایی موجود
     *
     * @param id            شناسه گزینه غذایی مورد نظر برای به‌روزرسانی
     * @param foodOptionDto داده‌های جدید برای به‌روزرسانی
     * @return FoodOptionDto به‌روز شده
     */
    @PutMapping("/{id}")
    public ResponseEntity<FoodOptionDto> update(@PathVariable Long id, @Valid @RequestBody FoodOptionDto foodOptionDto) {
        FoodOptionDto updatedFoodOption = foodOptionService.update(id, foodOptionDto);
        return ResponseEntity.ok(updatedFoodOption);
    }

    /**
     * حذف یک گزینه غذایی بر اساس شناسه
     *
     * @param id شناسه گزینه غذایی مورد نظر برای حذف
     * @return ResponseEntity بدون محتوا
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        foodOptionService.delete(id);
        return ResponseEntity.noContent().build();
    }
    /**
     * وارد کردن گزینه‌های غذایی از فایل Excel
     *
     * @param file فایل Excel حاوی داده‌های گزینه‌های غذایی
     * @return پاسخ وارد کردن شامل تعداد موفقیت‌آمیز و پیام‌های خطا
     */
   
}
