package com.mapnaom.foodreservation.controllers;

import com.mapnaom.foodreservation.dtos.ReservationDto;
import com.mapnaom.foodreservation.searchForms.ReservationSearchForm;
import com.mapnaom.foodreservation.services.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * کنترلر برای مدیریت عملیات‌های مربوط به رزروها
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    /**
     * دریافت تمام رزروها به صورت صفحه‌بندی شده با شرایط جستجو
     *
     * @param page          شماره صفحه (پیش‌فرض: 0)
     * @param size          تعداد آیتم‌ها در هر صفحه (پیش‌فرض: 10)
     * @param sortBy        فیلدی که بر اساس آن مرتب‌سازی می‌شود (پیش‌فرض: "id")
     * @param order         نوع مرتب‌سازی (ASC یا DESC) (پیش‌فرض: "ASC")
     * @param searchForm    فرم جستجو شامل فیلدهای id، personnelFirstName، personnelLastName، personnelCode و reservedFood
     * @return صفحه‌ای از ReservationDto
     */
    @GetMapping
    public ResponseEntity<Page<ReservationDto>> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "order", defaultValue = "ASC") String order,
            @ModelAttribute ReservationSearchForm searchForm
    ) {
        Sort.Direction sortDirection = order.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<ReservationDto> reservationPage = reservationService.findAll(searchForm, pageable);
        return ResponseEntity.ok(reservationPage);
    }

    /**
     * دریافت یک رزرو بر اساس شناسه
     *
     * @param id شناسه رزرو
     * @return ReservationDto
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReservationDto> findById(@PathVariable Long id) {
        ReservationDto reservation = reservationService.findById(id);
        return ResponseEntity.ok(reservation);
    }

    /**
     * ایجاد یک رزرو جدید
     *
     * @param reservationDto داده‌های رزرو جدید
     * @return ReservationDto ایجاد شده
     */
    @PostMapping
    public ResponseEntity<ReservationDto> create(@Valid @RequestBody ReservationDto reservationDto) {
        ReservationDto createdReservation = reservationService.create(reservationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReservation);
    }

    /**
     * به‌روزرسانی یک رزرو موجود
     *
     * @param id شناسه رزرو مورد نظر برای به‌روزرسانی
     * @param reservationDto داده‌های جدید برای به‌روزرسانی
     * @return ReservationDto به‌روز شده
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReservationDto> update(@PathVariable Long id, @Valid @RequestBody ReservationDto reservationDto) {
        ReservationDto updatedReservation = reservationService.update(id, reservationDto);
        return ResponseEntity.ok(updatedReservation);
    }

    /**
     * حذف یک رزرو بر اساس شناسه
     *
     * @param id شناسه رزرو مورد نظر برای حذف
     * @return ResponseEntity بدون محتوا
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
