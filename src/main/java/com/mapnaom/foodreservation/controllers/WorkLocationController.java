package com.mapnaom.foodreservation.controllers;

import com.mapnaom.foodreservation.dtos.WorkLocationDto;
import com.mapnaom.foodreservation.dtos.WorkLocationImportResponse;
import com.mapnaom.foodreservation.searchForms.WorkLocationSearchForm;
import com.mapnaom.foodreservation.services.WorkLocationService;
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
 * کنترلر برای مدیریت عملیات‌های مربوط به محل‌های کاری
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/work-locations")
@RequiredArgsConstructor
public class WorkLocationController {
    private final WorkLocationService workLocationService;

    /**
     * دریافت تمام محل‌های کاری به صورت صفحه‌بندی شده با شرایط جستجو
     *
     * @param page          شماره صفحه (پیش‌فرض: 0)
     * @param size          تعداد آیتم‌ها در هر صفحه (پیش‌فرض: 10)
     * @param sortBy        فیلدی که بر اساس آن مرتب‌سازی می‌شود (پیش‌فرض: "id")
     * @param order         نوع مرتب‌سازی (ASC یا DESC) (پیش‌فرض: "ASC")
     * @param searchForm    فرم جستجو شامل فیلدهای id و workLocationName
     * @return صفحه‌ای از WorkLocationDto
     */
    @GetMapping
    public ResponseEntity<Page<WorkLocationDto>> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "order", defaultValue = "ASC") String order,
            @ModelAttribute WorkLocationSearchForm searchForm
    ) {
        Sort.Direction sortDirection = order.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<WorkLocationDto> workLocationPage = workLocationService.findAll(searchForm, pageable);
        return ResponseEntity.ok(workLocationPage);
    }

    /**
     * دریافت یک محل کاری بر اساس شناسه
     *
     * @param id شناسه محل کاری
     * @return WorkLocationDto
     */
    @GetMapping("/{id}")
    public ResponseEntity<WorkLocationDto> findById(@PathVariable Long id) {
        WorkLocationDto workLocation = workLocationService.findById(id);
        return ResponseEntity.ok(workLocation);
    }

    /**
     * ایجاد یک محل کاری جدید
     *
     * @param workLocationDto داده‌های محل کاری جدید
     * @return WorkLocationDto ایجاد شده
     */
    @PostMapping
    public ResponseEntity<WorkLocationDto> create(@Valid @RequestBody WorkLocationDto workLocationDto) {
        WorkLocationDto createdWorkLocation = workLocationService.create(workLocationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdWorkLocation);
    }

    /**
     * به‌روزرسانی یک محل کاری موجود
     *
     * @param id شناسه محل کاری مورد نظر برای به‌روزرسانی
     * @param workLocationDto داده‌های جدید برای به‌روزرسانی
     * @return WorkLocationDto به‌روز شده
     */
    @PutMapping("/{id}")
    public ResponseEntity<WorkLocationDto> update(@PathVariable Long id, @Valid @RequestBody WorkLocationDto workLocationDto) {
        WorkLocationDto updatedWorkLocation = workLocationService.update(id, workLocationDto);
        return ResponseEntity.ok(updatedWorkLocation);
    }

    /**
     * حذف یک محل کاری بر اساس شناسه
     *
     * @param id شناسه محل کاری مورد نظر برای حذف
     * @return ResponseEntity بدون محتوا
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        workLocationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * واردسازی مکان‌های کاری از طریق فایل اکسل
     *
     * @param file فایل اکسل حاوی اطلاعات مکان‌های کاری
     * @return پاسخ شامل تعداد رکوردهای موفق و خطاهای احتمالی
     */
    @PostMapping("/import")
    public ResponseEntity<WorkLocationImportResponse> importWorkLocations(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new WorkLocationImportResponse(0, List.of("فایل ارسال شده خالی است.")));
        }

        String contentType = file.getContentType();
        if (!"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType)
            && !"application/vnd.ms-excel".equals(contentType)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new WorkLocationImportResponse(0, List.of("فایل ارسال شده اکسل نیست.")));
        }

        try {
            WorkLocationImportResponse response = workLocationService.importWorkLocationsFromExcelFile(file);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new WorkLocationImportResponse(0, List.of("خطای سروری رخ داده است: " + e.getMessage())));
        }
    }
}
