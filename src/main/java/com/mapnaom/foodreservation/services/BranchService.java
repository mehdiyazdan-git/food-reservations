package com.mapnaom.foodreservation.services;

import com.mapnaom.foodreservation.dtos.BranchDto;
import com.mapnaom.foodreservation.dtos.Select;
import com.mapnaom.foodreservation.exceptions.ResourceNotFoundException;
import com.mapnaom.foodreservation.mappers.BranchMapper;
import com.mapnaom.foodreservation.entities.Branch;
import com.mapnaom.foodreservation.repositories.BranchRepository;
import com.mapnaom.foodreservation.searchForms.BranchSearchForm;
import com.mapnaom.foodreservation.specifications.BranchSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;
    private final BranchMapper branchMapper;

    /**
     * دریافت تمام شعب به صورت صفحه‌بندی شده با شرایط جستجو
     *
     * @param searchForm فرم جستجو
     * @param pageable   اطلاعات صفحه‌بندی و مرتب‌سازی
     * @return صفحه‌ای از BranchDto
     */
    @Transactional(readOnly = true)
    public Page<BranchDto> findAll(BranchSearchForm searchForm, Pageable pageable) {
        Specification<Branch> specification = BranchSpecification.getBranchSpecification(searchForm);
        Page<Branch> branchPage = branchRepository.findAll(specification, pageable);
        List<BranchDto> branchDtos = branchPage.stream()
                .map(branchMapper::toDto)
                .collect(Collectors.toList());
        return new PageImpl<>(branchDtos, pageable, branchPage.getTotalElements());
    }

    public List<Select> selectList(String searchKey) {
        return branchRepository.findAll().stream()
                .map(i -> new Select(i.getId(), i.getName()))
                .collect(Collectors.toList());
    }

    /**
     * دریافت یک شعبه بر اساس شناسه
     *
     * @param id شناسه شعبه
     * @return BranchDto
     * @throws ResourceNotFoundException اگر شعبه با شناسه داده شده یافت نشد
     */
    @Transactional(readOnly = true)
    public BranchDto findById(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("شعبه با شناسه " + id + " یافت نشد."));
        return branchMapper.toDto(branch);
    }

    /**
     * ایجاد یک شعبه جدید
     *
     * @param branchDto داده‌های شعبه جدید
     * @return BranchDto ایجاد شده
     */
    @Transactional
    public BranchDto create(BranchDto branchDto) {
        Branch branch = branchMapper.toEntity(branchDto);
        Branch savedBranch = branchRepository.save(branch);
        return branchMapper.toDto(savedBranch);
    }

    /**
     * به‌روزرسانی یک شعبه موجود
     *
     * @param id        شناسه شعبه مورد نظر برای به‌روزرسانی
     * @param branchDto داده‌های جدید برای به‌روزرسانی
     * @return BranchDto به‌روز شده
     * @throws ResourceNotFoundException اگر شعبه با شناسه داده شده یافت نشد
     */
    @Transactional
    public BranchDto update(Long id, BranchDto branchDto) {
        Branch existingBranch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("شعبه با شناسه " + id + " یافت نشد."));
        branchMapper.partialUpdate(branchDto, existingBranch);
        Branch updatedBranch = branchRepository.save(existingBranch);
        return branchMapper.toDto(updatedBranch);
    }

    /**
     * حذف یک شعبه بر اساس شناسه
     *
     * @param id شناسه شعبه مورد نظر برای حذف
     * @throws ResourceNotFoundException اگر شعبه با شناسه داده شده یافت نشد
     */
    @Transactional
    public void delete(Long id) {
        if (!branchRepository.existsById(id)) {
            throw new ResourceNotFoundException("شعبه با شناسه " + id + " یافت نشد.");
        }
        branchRepository.deleteById(id);
    }
}
