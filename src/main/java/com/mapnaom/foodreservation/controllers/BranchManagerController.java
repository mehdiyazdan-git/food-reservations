package com.mapnaom.foodreservation.controllers;

import com.mapnaom.foodreservation.dtos.BranchManagerDto;
import com.mapnaom.foodreservation.searchForms.BranchManagerSearchForm;
import com.mapnaom.foodreservation.services.BranchManagerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * کنترلر برای مدیریت عملیات‌های مربوط به مدیران شعب
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/branch-managers")
@RequiredArgsConstructor
public class BranchManagerController {

    private final BranchManagerService branchManagerService;

    /**
     * دریافت تمام مدیران شعب به صورت صفحه‌بندی شده با شرایط جستجو
     *
     * @param page         شماره صفحه (پیش‌فرض: 0)
     * @param size         تعداد آیتم‌ها در هر صفحه (پیش‌فرض: 10)
     * @param sortBy       فیلدی که بر اساس آن مرتب‌سازی می‌شود (پیش‌فرض: "id")
     * @param order        نوع مرتب‌سازی (ASC یا DESC) (پیش‌فرض: "ASC")
     * @param searchForm   فرم جستجو شامل فیلدهای مختلف برای فیلتر کردن
     * @return صفحه‌ای از BranchManagerDto
     */
    @GetMapping
    public ResponseEntity<Page<BranchManagerDto>> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "order", defaultValue = "ASC") String order,
            @ModelAttribute BranchManagerSearchForm searchForm
    ) {
        Sort.Direction sortDirection = order.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<BranchManagerDto> branchManagerPage = branchManagerService.findAll(searchForm, pageable);
        return ResponseEntity.ok(branchManagerPage);
    }

    /**
     * دریافت یک مدیر شعبه بر اساس شناسه
     *
     * @param id شناسه مدیر شعبه
     * @return BranchManagerDto
     */
    @GetMapping("/{id}")
    public ResponseEntity<BranchManagerDto> findById(@PathVariable Long id) {
        BranchManagerDto branchManagerDto = branchManagerService.findById(id);
        return ResponseEntity.ok(branchManagerDto);
    }

    /**
     * ایجاد یک مدیر شعبه جدید
     *
     * @param branchManagerDto داده‌های مدیر شعبه جدید
     * @return BranchManagerDto ایجاد شده
     */
    @PostMapping
    public ResponseEntity<BranchManagerDto> create(@Valid @RequestBody BranchManagerDto branchManagerDto) {
        BranchManagerDto createdBranchManager = branchManagerService.create(branchManagerDto);
        return ResponseEntity.status(201).body(createdBranchManager);
    }

    /**
     * به‌روزرسانی یک مدیر شعبه موجود
     *
     * @param id               شناسه مدیر شعبه مورد نظر برای به‌روزرسانی
     * @param branchManagerDto داده‌های جدید برای به‌روزرسانی
     * @return BranchManagerDto به‌روز شده
     */
    @PutMapping("/{id}")
    public ResponseEntity<BranchManagerDto> update(@PathVariable Long id, @Valid @RequestBody BranchManagerDto branchManagerDto) {
        BranchManagerDto updatedBranchManager = branchManagerService.update(id, branchManagerDto);
        return ResponseEntity.ok(updatedBranchManager);
    }

    /**
     * حذف یک مدیر شعبه بر اساس شناسه
     *
     * @param id شناسه مدیر شعبه مورد نظر برای حذف
     * @return ResponseEntity بدون محتوا
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        branchManagerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
