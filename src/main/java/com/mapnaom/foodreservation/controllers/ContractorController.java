package com.mapnaom.foodreservation.controllers;

import com.mapnaom.foodreservation.dtos.ContractorDto;
import com.mapnaom.foodreservation.searchForms.ContractorSearchForm;
import com.mapnaom.foodreservation.services.ContractorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * کنترلر برای مدیریت عملیات‌های مربوط به پیمانکاران
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/contractors")
@RequiredArgsConstructor
public class ContractorController {

    private final ContractorService contractorService;

    /**
     * دریافت تمام پیمانکاران به صورت صفحه‌بندی شده با شرایط جستجو
     *
     * @param page        شماره صفحه (پیش‌فرض: 0)
     * @param size        تعداد آیتم‌ها در هر صفحه (پیش‌فرض: 10)
     * @param sortBy      فیلدی که بر اساس آن مرتب‌سازی می‌شود (پیش‌فرض: "id")
     * @param order       نوع مرتب‌سازی (ASC یا DESC) (پیش‌فرض: "ASC")
     * @param searchForm  فرم جستجو شامل فیلدهای مختلف برای فیلتر کردن
     * @return صفحه‌ای از ContractorDto
     */
    @GetMapping
    public ResponseEntity<Page<ContractorDto>> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "order", defaultValue = "ASC") String order,
            @ModelAttribute ContractorSearchForm searchForm
    ) {
        Sort.Direction sortDirection = order.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<ContractorDto> contractorPage = contractorService.findAll(searchForm, pageable);
        return ResponseEntity.ok(contractorPage);
    }

    /**
     * دریافت یک پیمانکار بر اساس شناسه
     *
     * @param id شناسه پیمانکار
     * @return ContractorDto
     */
    @GetMapping("/{id}")
    public ResponseEntity<ContractorDto> findById(@PathVariable Long id) {
        ContractorDto contractorDto = contractorService.findById(id);
        return ResponseEntity.ok(contractorDto);
    }

    /**
     * ایجاد یک پیمانکار جدید
     *
     * @param contractorDto داده‌های پیمانکار جدید
     * @return ContractorDto ایجاد شده
     */
    @PostMapping
    public ResponseEntity<ContractorDto> create(@Valid @RequestBody ContractorDto contractorDto) {
        ContractorDto createdContractor = contractorService.create(contractorDto);
        return ResponseEntity.status(201).body(createdContractor);
    }

    /**
     * به‌روزرسانی یک پیمانکار موجود
     *
     * @param id            شناسه پیمانکار مورد نظر برای به‌روزرسانی
     * @param contractorDto داده‌های جدید برای به‌روزرسانی
     * @return ContractorDto به‌روز شده
     */
    @PutMapping("/{id}")
    public ResponseEntity<ContractorDto> update(@PathVariable Long id, @Valid @RequestBody ContractorDto contractorDto) {
        ContractorDto updatedContractor = contractorService.update(id, contractorDto);
        return ResponseEntity.ok(updatedContractor);
    }

    /**
     * حذف یک پیمانکار بر اساس شناسه
     *
     * @param id شناسه پیمانکار مورد نظر برای حذف
     * @return ResponseEntity بدون محتوا
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contractorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
