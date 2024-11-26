package com.mapnaom.foodreservation.services;

import com.mapnaom.foodreservation.dtos.BranchManagerDto;
import com.mapnaom.foodreservation.exceptions.ResourceNotFoundException;
import com.mapnaom.foodreservation.mappers.BranchManagerMapper;
import com.mapnaom.foodreservation.models.BranchManager;
import com.mapnaom.foodreservation.repositories.BranchManagerRepository;
import com.mapnaom.foodreservation.searchForms.BranchManagerSearchForm;
import com.mapnaom.foodreservation.specifications.BranchManagerSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BranchManagerService {

    private final BranchManagerRepository branchManagerRepository;
    private final BranchManagerMapper branchManagerMapper;

    /**
     * دریافت تمام مدیران شعب به صورت صفحه‌بندی شده با شرایط جستجو
     *
     * @param searchForm فرم جستجو
     * @param pageable   اطلاعات صفحه‌بندی و مرتب‌سازی
     * @return صفحه‌ای از BranchManagerDto
     */
    @Transactional(readOnly = true)
    public Page<BranchManagerDto> findAll(BranchManagerSearchForm searchForm, Pageable pageable) {
        Specification<BranchManager> specification = BranchManagerSpecification.getBranchManagerSpecification(searchForm);
        Page<BranchManager> branchManagerPage = branchManagerRepository.findAll(specification, pageable);
        List<BranchManagerDto> branchManagerDtos = branchManagerPage.stream()
                .map(branchManagerMapper::toDto)
                .collect(Collectors.toList());
        return new PageImpl<>(branchManagerDtos, pageable, branchManagerPage.getTotalElements());
    }

    /**
     * دریافت یک مدیر شعبه بر اساس شناسه
     *
     * @param id شناسه مدیر شعبه
     * @return BranchManagerDto
     * @throws ResourceNotFoundException اگر مدیر شعبه با شناسه داده شده یافت نشد
     */
    @Transactional(readOnly = true)
    public BranchManagerDto findById(Long id) {
        BranchManager branchManager = branchManagerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("مدیر شعبه با شناسه " + id + " یافت نشد."));
        return branchManagerMapper.toDto(branchManager);
    }

    /**
     * ایجاد یک مدیر شعبه جدید
     *
     * @param branchManagerDto داده‌های مدیر شعبه جدید
     * @return BranchManagerDto ایجاد شده
     */
    @Transactional
    public BranchManagerDto create(BranchManagerDto branchManagerDto) {
        BranchManager branchManager = branchManagerMapper.toEntity(branchManagerDto);
        BranchManager savedBranchManager = branchManagerRepository.save(branchManager);
        return branchManagerMapper.toDto(savedBranchManager);
    }

    /**
     * به‌روزرسانی یک مدیر شعبه موجود
     *
     * @param id                شناسه مدیر شعبه مورد نظر برای به‌روزرسانی
     * @param branchManagerDto  داده‌های جدید برای به‌روزرسانی
     * @return BranchManagerDto به‌روز شده
     * @throws ResourceNotFoundException اگر مدیر شعبه با شناسه داده شده یافت نشد
     */
    @Transactional
    public BranchManagerDto update(Long id, BranchManagerDto branchManagerDto) {
        BranchManager existingBranchManager = branchManagerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("مدیر شعبه با شناسه " + id + " یافت نشد."));
        branchManagerMapper.partialUpdate(branchManagerDto, existingBranchManager);
        BranchManager updatedBranchManager = branchManagerRepository.save(existingBranchManager);
        return branchManagerMapper.toDto(updatedBranchManager);
    }

    /**
     * حذف یک مدیر شعبه بر اساس شناسه
     *
     * @param id شناسه مدیر شعبه مورد نظر برای حذف
     * @throws ResourceNotFoundException اگر مدیر شعبه با شناسه داده شده یافت نشد
     */
    @Transactional
    public void delete(Long id) {
        if (!branchManagerRepository.existsById(id)) {
            throw new ResourceNotFoundException("مدیر شعبه با شناسه " + id + " یافت نشد.");
        }
        branchManagerRepository.deleteById(id);
    }
}
