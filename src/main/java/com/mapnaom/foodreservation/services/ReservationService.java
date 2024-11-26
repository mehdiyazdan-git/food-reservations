package com.mapnaom.foodreservation.services;

import com.mapnaom.foodreservation.dtos.ReservationDto;
import com.mapnaom.foodreservation.entities.Reservation;
import com.mapnaom.foodreservation.exceptions.ResourceNotFoundException;
import com.mapnaom.foodreservation.mappers.ReservationMapper;
import com.mapnaom.foodreservation.repositories.v1.ReservationRepository;
import com.mapnaom.foodreservation.searchForms.ReservationSearchForm;
import com.mapnaom.foodreservation.specifications.ReservationSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;

    /**
     * پیدا کردن تمام رزروها به صورت صفحه‌بندی شده با شرایط جستجو
     *
     * @param searchForm فرم جستجو شامل فیلدهای id، personnelFirstName، personnelLastName، personnelCode و reservedFood
     * @param pageable   اطلاعات صفحه‌بندی و مرتب‌سازی
     * @return صفحه‌ای از ReservationDto
     */
    @Transactional(readOnly = true)
    public Page<ReservationDto> findAll(ReservationSearchForm searchForm, Pageable pageable) {
        Specification<Reservation> specification = ReservationSpecification.getReservationSpecification(searchForm);
        Page<Reservation> reservationPage = reservationRepository.findAll(specification, pageable);
        List<ReservationDto> reservationDtos = reservationPage.stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
        return new PageImpl<>(reservationDtos, pageable, reservationPage.getTotalElements());
    }

    /**
     * پیدا کردن یک رزرو بر اساس شناسه
     *
     * @param id شناسه رزرو
     * @return ReservationDto
     * @throws ResourceNotFoundException اگر رزرو با شناسه داده شده یافت نشد
     */
    @Transactional(readOnly = true)
    public ReservationDto findById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
        return reservationMapper.toDto(reservation);
    }

    /**
     * ایجاد یک رزرو جدید
     *
     * @param reservationDto داده‌های رزرو جدید
     * @return ReservationDto ایجاد شده
     */
    @Transactional
    public ReservationDto create(ReservationDto reservationDto) {
        Reservation reservation = reservationMapper.toEntity(reservationDto);
        Reservation savedReservation = reservationRepository.save(reservation);
        return reservationMapper.toDto(savedReservation);
    }

    /**
     * به‌روزرسانی یک رزرو موجود
     *
     * @param id شناسه رزرو مورد نظر برای به‌روزرسانی
     * @param reservationDto داده‌های جدید برای به‌روزرسانی
     * @return ReservationDto به‌روز شده
     * @throws ResourceNotFoundException اگر رزرو با شناسه داده شده یافت نشد
     */
    @Transactional
    public ReservationDto update(Long id, ReservationDto reservationDto) {
        Reservation existingReservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));

        reservationMapper.partialUpdate(reservationDto, existingReservation);
        Reservation updatedReservation = reservationRepository.save(existingReservation);
        return reservationMapper.toDto(updatedReservation);
    }

    /**
     * حذف یک رزرو بر اساس شناسه
     *
     * @param id شناسه رزرو مورد نظر برای حذف
     * @throws ResourceNotFoundException اگر رزرو با شناسه داده شده یافت نشد
     */
    @Transactional
    public void delete(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reservation not found with id: " + id);
        }
        reservationRepository.deleteById(id);
    }
}
