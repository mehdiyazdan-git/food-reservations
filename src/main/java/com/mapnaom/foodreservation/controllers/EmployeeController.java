package com.mapnaom.foodreservation.controllers;

import com.mapnaom.foodreservation.dtos.EmployeeDto;
import com.mapnaom.foodreservation.searchForms.EmployeeSearchForm;
import com.mapnaom.foodreservation.services.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * کنترلر برای مدیریت عملیات‌های مربوط به کارکنان
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * دریافت تمام کارکنان به صورت صفحه‌بندی شده با شرایط جستجو
     *
     * @param page        شماره صفحه (پیش‌فرض: 0)
     * @param size        تعداد آیتم‌ها در هر صفحه (پیش‌فرض: 10)
     * @param sortBy      فیلدی که بر اساس آن مرتب‌سازی می‌شود (پیش‌فرض: "id")
     * @param order       نوع مرتب‌سازی (ASC یا DESC) (پیش‌فرض: "ASC")
     * @param searchForm  فرم جستجو شامل فیلدهای مختلف برای فیلتر کردن
     * @return صفحه‌ای از EmployeeDto
     */
    @GetMapping
    public ResponseEntity<Page<EmployeeDto>> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "order", defaultValue = "ASC") String order,
            @ModelAttribute EmployeeSearchForm searchForm
    ) {
        Sort.Direction sortDirection = order.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<EmployeeDto> employeePage = employeeService.findAll(searchForm, pageable);
        return ResponseEntity.ok(employeePage);
    }

    /**
     * دریافت یک کارمند بر اساس شناسه
     *
     * @param id شناسه کارمند
     * @return EmployeeDto
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> findById(@PathVariable Long id) {
        EmployeeDto employeeDto = employeeService.findById(id);
        return ResponseEntity.ok(employeeDto);
    }

    /**
     * ایجاد یک کارمند جدید
     *
     * @param employeeDto داده‌های کارمند جدید
     * @return EmployeeDto ایجاد شده
     */
    @PostMapping
    public ResponseEntity<EmployeeDto> create(@Valid @RequestBody EmployeeDto employeeDto) {
        EmployeeDto createdEmployee = employeeService.create(employeeDto);
        return ResponseEntity.status(201).body(createdEmployee);
    }

    /**
     * به‌روزرسانی یک کارمند موجود
     *
     * @param id          شناسه کارمند مورد نظر برای به‌روزرسانی
     * @param employeeDto داده‌های جدید برای به‌روزرسانی
     * @return EmployeeDto به‌روز شده
     */
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDto> update(@PathVariable Long id, @Valid @RequestBody EmployeeDto employeeDto) {
        EmployeeDto updatedEmployee = employeeService.update(id, employeeDto);
        return ResponseEntity.ok(updatedEmployee);
    }

    /**
     * حذف یک کارمند بر اساس شناسه
     *
     * @param id شناسه کارمند مورد نظر برای حذف
     * @return ResponseEntity بدون محتوا
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
