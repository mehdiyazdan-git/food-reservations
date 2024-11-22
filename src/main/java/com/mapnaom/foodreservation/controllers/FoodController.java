package com.mapnaom.foodreservation.controllers;

import com.mapnaom.foodreservation.dtos.FoodDto;
import com.mapnaom.foodreservation.dtos.FoodImportResponse;
import com.mapnaom.foodreservation.searchForms.FoodSearchForm;
import com.mapnaom.foodreservation.services.FoodService;
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

import java.util.List;

/**
 * کنترلر برای مدیریت عملیات‌های مربوط به غذاها
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/foods")
@RequiredArgsConstructor
public class FoodController {
    private final FoodService foodService;

    /**
     * دریافت تمام غذاها به صورت صفحه‌بندی شده با شرایط جستجو
     *
     * @param page       شماره صفحه (پیش‌فرض: 0)
     * @param size       تعداد آیتم‌ها در هر صفحه (پیش‌فرض: 10)
     * @param sortBy     فیلدی که بر اساس آن مرتب‌سازی می‌شود (پیش‌فرض: "id")
     * @param order      نوع مرتب‌سازی (ASC یا DESC) (پیش‌فرض: "ASC")
     * @param searchForm فرم جستجو شامل فیلدهای id و name
     * @return صفحه‌ای از FoodDto
     */
    @GetMapping
    public ResponseEntity<Page<FoodDto>> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "order", defaultValue = "ASC") String order,
            @ModelAttribute FoodSearchForm searchForm
    ) {
        Sort.Direction sortDirection = order.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<FoodDto> foodPage = foodService.findAll(searchForm, pageable);
        return ResponseEntity.ok(foodPage);
    }

    /**
     * دریافت یک غذا بر اساس شناسه
     *
     * @param id شناسه غذا
     * @return FoodDto
     */
    @GetMapping("/{id}")
    public ResponseEntity<FoodDto> findById(@PathVariable Long id) {
        FoodDto food = foodService.findById(id);
        return ResponseEntity.ok(food);
    }

    /**
     * ایجاد یک غذا جدید
     *
     * @param foodDto داده‌های غذا جدید
     * @return FoodDto ایجاد شده
     */
    @PostMapping
    public ResponseEntity<FoodDto> create(@Valid @RequestBody FoodDto foodDto) {
        FoodDto createdFood = foodService.create(foodDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFood);
    }

    /**
     * به‌روزرسانی یک غذا موجود
     *
     * @param id شناسه غذای مورد نظر برای به‌روزرسانی
     * @param foodDto داده‌های جدید برای به‌روزرسانی
     * @return FoodDto به‌روز شده
     */
    @PutMapping("/{id}")
    public ResponseEntity<FoodDto> update(@PathVariable Long id, @Valid @RequestBody FoodDto foodDto) {
        FoodDto updatedFood = foodService.update(id, foodDto);
        return ResponseEntity.ok(updatedFood);
    }

    /**
     * حذف یک غذا بر اساس شناسه
     *
     * @param id شناسه غذای مورد نظر برای حذف
     * @return ResponseEntity بدون محتوا
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        foodService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * ورود غذاها از طریق فایل اکسل
     *
     * @param file فایل اکسل حاوی اطلاعات غذاها
     * @return پاسخ شامل تعداد رکوردهای موفق و خطاهای احتمالی
     */
    @PostMapping("/import")
    public ResponseEntity<FoodImportResponse> importFoods(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new FoodImportResponse(0, List.of("فایل ارسال شده خالی است.")));
        }

        String contentType = file.getContentType();
        if (!"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType)
            && !"application/vnd.ms-excel".equals(contentType)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new FoodImportResponse(0, List.of("فایل ارسال شده اکسل نیست.")));
        }

        try {
            FoodImportResponse response = foodService.importFoodsFromExcel(file);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new FoodImportResponse(0, List.of("خطای سروری رخ داده است: " + e.getMessage())));
        }
    }
}
