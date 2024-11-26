package com.mapnaom.foodreservation.controllers;

import com.mapnaom.foodreservation.dtos.OrderDto;
import com.mapnaom.foodreservation.searchForms.OrderSearchForm;
import com.mapnaom.foodreservation.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * کنترلر برای مدیریت عملیات‌های مربوط به سفارش‌ها
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * دریافت تمام سفارش‌ها به صورت صفحه‌بندی شده با شرایط جستجو
     *
     * @param page        شماره صفحه (پیش‌فرض: 0)
     * @param size        تعداد آیتم‌ها در هر صفحه (پیش‌فرض: 10)
     * @param sortBy      فیلدی که بر اساس آن مرتب‌سازی می‌شود (پیش‌فرض: "id")
     * @param order       نوع مرتب‌سازی (ASC یا DESC) (پیش‌فرض: "ASC")
     * @param searchForm  فرم جستجو شامل فیلدهای مختلف برای فیلتر کردن
     * @return صفحه‌ای از OrderDto
     */
    @GetMapping
    public ResponseEntity<Page<OrderDto>> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "order", defaultValue = "ASC") String order,
            @ModelAttribute OrderSearchForm searchForm
    ) {
        Sort.Direction sortDirection = order.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<OrderDto> orderPage = orderService.findAll(searchForm, pageable);
        return ResponseEntity.ok(orderPage);
    }

    /**
     * دریافت یک سفارش بر اساس شناسه
     *
     * @param id شناسه سفارش
     * @return OrderDto
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> findById(@PathVariable Long id) {
        OrderDto orderDto = orderService.findById(id);
        return ResponseEntity.ok(orderDto);
    }

    /**
     * ایجاد یک سفارش جدید
     *
     * @param orderDto داده‌های سفارش جدید
     * @return OrderDto ایجاد شده
     */
    @PostMapping
    public ResponseEntity<OrderDto> create(@Valid @RequestBody OrderDto orderDto) {
        OrderDto createdOrder = orderService.create(orderDto);
        return ResponseEntity.status(201).body(createdOrder);
    }

    /**
     * به‌روزرسانی یک سفارش موجود
     *
     * @param id       شناسه سفارش مورد نظر برای به‌روزرسانی
     * @param orderDto داده‌های جدید برای به‌روزرسانی
     * @return OrderDto به‌روز شده
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrderDto> update(@PathVariable Long id, @Valid @RequestBody OrderDto orderDto) {
        OrderDto updatedOrder = orderService.update(id, orderDto);
        return ResponseEntity.ok(updatedOrder);
    }

    /**
     * حذف یک سفارش بر اساس شناسه
     *
     * @param id شناسه سفارش مورد نظر برای حذف
     * @return ResponseEntity بدون محتوا
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
