package com.mapnaom.foodreservation.controllers;

import com.mapnaom.foodreservation.dtos.MenuDto;
import com.mapnaom.foodreservation.searchForms.MenuSearchForm;
import com.mapnaom.foodreservation.services.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * کنترلر برای مدیریت عملیات‌های مربوط به منوها
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    /**
     * دریافت تمام منوها به صورت صفحه‌بندی شده با شرایط جستجو
     *
     * @param page        شماره صفحه (پیش‌فرض: 0)
     * @param size        تعداد آیتم‌ها در هر صفحه (پیش‌فرض: 10)
     * @param sortBy      فیلدی که بر اساس آن مرتب‌سازی می‌شود (پیش‌فرض: "id")
     * @param order       نوع مرتب‌سازی (ASC یا DESC) (پیش‌فرض: "ASC")
     * @param searchForm  فرم جستجو شامل فیلدهای مختلف برای فیلتر کردن
     * @return صفحه‌ای از MenuDto
     */
    @GetMapping
    public ResponseEntity<Page<MenuDto>> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "order", defaultValue = "ASC") String order,
            @ModelAttribute MenuSearchForm searchForm
    ) {
        Sort.Direction sortDirection = order.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<MenuDto> menuPage = menuService.findAll(searchForm, pageable);
        return ResponseEntity.ok(menuPage);
    }

    /**
     * دریافت یک منو بر اساس شناسه
     *
     * @param id شناسه منو
     * @return MenuDto
     */
    @GetMapping("/{id}")
    public ResponseEntity<MenuDto> findById(@PathVariable Long id) {
        MenuDto menuDto = menuService.findById(id);
        return ResponseEntity.ok(menuDto);
    }

    /**
     * ایجاد یک منوی جدید
     *
     * @param menuDto داده‌های منوی جدید
     * @return MenuDto ایجاد شده
     */
    @PostMapping
    public ResponseEntity<MenuDto> create(@Valid @RequestBody MenuDto menuDto) {
        MenuDto createdMenu = menuService.create(menuDto);
        return ResponseEntity.status(201).body(createdMenu);
    }

    /**
     * به‌روزرسانی یک منوی موجود
     *
     * @param id      شناسه منوی مورد نظر برای به‌روزرسانی
     * @param menuDto داده‌های جدید برای به‌روزرسانی
     * @return MenuDto به‌روز شده
     */
    @PutMapping("/{id}")
    public ResponseEntity<MenuDto> update(@PathVariable Long id, @Valid @RequestBody MenuDto menuDto) {
        MenuDto updatedMenu = menuService.update(id, menuDto);
        return ResponseEntity.ok(updatedMenu);
    }

    /**
     * حذف یک منو بر اساس شناسه
     *
     * @param id شناسه منوی مورد نظر برای حذف
     * @return ResponseEntity بدون محتوا
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        menuService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
