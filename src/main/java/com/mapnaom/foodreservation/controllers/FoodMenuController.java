package com.mapnaom.foodreservation.controllers;

import com.mapnaom.foodreservation.dtos.FoodPlanImportResponse;
import com.mapnaom.foodreservation.entities.FoodMenuDto;
import com.mapnaom.foodreservation.searchForms.FoodPlanSearchForm;
import com.mapnaom.foodreservation.services.FoodMenuService;
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
 * کنترلر برای مدیریت عملیات‌های مربوط به برنامه‌های غذایی
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/food-plans")
@RequiredArgsConstructor
public class FoodMenuController {
    private final FoodMenuService foodMenuService;

    /**
     * دریافت تمام برنامه‌های غذایی به صورت صفحه‌بندی شده با شرایط جستجو
     *
     * @param page        شماره صفحه (پیش‌فرض: 0)
     * @param size        تعداد آیتم‌ها در هر صفحه (پیش‌فرض: 10)
     * @param sortBy      فیلدی که بر اساس آن مرتب‌سازی می‌شود (پیش‌فرض: "id")
     * @param order       نوع مرتب‌سازی (ASC یا DESC) (پیش‌فرض: "ASC")
     * @param searchForm  فرم جستجو شامل فیلدهای id، foodName و localDate
     * @return صفحه‌ای از FoodMenuDto
     */
    @GetMapping
    public ResponseEntity<Page<FoodMenuDto>> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "order", defaultValue = "ASC") String order,
            @ModelAttribute FoodPlanSearchForm searchForm
    ) {
        Sort.Direction sortDirection = order.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<FoodMenuDto> foodPlanPage = foodMenuService.findAll(searchForm, pageable);
        return ResponseEntity.ok(foodPlanPage);
    }

    /**
     * دریافت یک برنامه غذایی بر اساس شناسه
     *
     * @param id شناسه برنامه غذایی
     * @return FoodMenuDto
     */
    @GetMapping("/{id}")
    public ResponseEntity<FoodMenuDto> findById(@PathVariable Long id) {
        FoodMenuDto foodPlan = foodMenuService.findById(id);
        return ResponseEntity.ok(foodPlan);
    }

    /**
     * ایجاد یک برنامه غذایی جدید
     *
     * @param foodMenuDto داده‌های برنامه غذایی جدید
     * @return FoodMenuDto ایجاد شده
     */
    @PostMapping
    public ResponseEntity<FoodMenuDto> create(@Valid @RequestBody FoodMenuDto foodMenuDto) {
        FoodMenuDto createdFoodPlan = foodMenuService.create(foodMenuDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFoodPlan);
    }

    /**
     * به‌روزرسانی یک برنامه غذایی موجود
     *
     * @param id شناسه برنامه غذایی مورد نظر برای به‌روزرسانی
     * @param foodMenuDto داده‌های جدید برای به‌روزرسانی
     * @return FoodMenuDto به‌روز شده
     */
    @PutMapping("/{id}")
    public ResponseEntity<FoodMenuDto> update(@PathVariable Long id, @Valid @RequestBody FoodMenuDto foodMenuDto) {
        FoodMenuDto updatedFoodPlan = foodMenuService.update(id, foodMenuDto);
        return ResponseEntity.ok(updatedFoodPlan);
    }

    /**
     * حذف یک برنامه غذایی بر اساس شناسه
     *
     * @param id شناسه برنامه غذایی مورد نظر برای حذف
     * @return ResponseEntity بدون محتوا
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        foodMenuService.delete(id);
        return ResponseEntity.noContent().build();
    }
    /**
     * ورود برنامه‌های غذایی از طریق فایل اکسل
     *
     * @param file فایل اکسل حاوی اطلاعات برنامه‌های غذایی
     * @return پاسخ شامل تعداد رکوردهای موفق و خطاهای احتمالی
     */
    @PostMapping("/import")
    public ResponseEntity<FoodPlanImportResponse> importFoodPlans(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new FoodPlanImportResponse(0, List.of("فایل ارسال شده خالی است.")));
        }

        String contentType = file.getContentType();
        if (!"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType)
            && !"application/vnd.ms-excel".equals(contentType)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new FoodPlanImportResponse(0, List.of("فایل ارسال شده اکسل نیست.")));
        }

        try {
            FoodPlanImportResponse response = foodMenuService.importFoodPlanFromExcel(file);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new FoodPlanImportResponse(0, List.of("خطای سروری رخ داده است: " + e.getMessage())));
        }
    }
}
