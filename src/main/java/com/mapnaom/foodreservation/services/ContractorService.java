package com.mapnaom.foodreservation.services;

import com.mapnaom.foodreservation.dtos.ContractorDto;
import com.mapnaom.foodreservation.exceptions.ResourceNotFoundException;
import com.mapnaom.foodreservation.mappers.ContractorMapper;
import com.mapnaom.foodreservation.entities.Contractor;
import com.mapnaom.foodreservation.repositories.ContractorRepository;
import com.mapnaom.foodreservation.searchForms.ContractorSearchForm;
import com.mapnaom.foodreservation.specifications.ContractorSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContractorService {

    private final ContractorRepository contractorRepository;
    private final ContractorMapper contractorMapper;

    /**
     * دریافت تمام پیمانکاران به صورت صفحه‌بندی شده با شرایط جستجو
     *
     * @param searchForm فرم جستجو
     * @param pageable   اطلاعات صفحه‌بندی و مرتب‌سازی
     * @return صفحه‌ای از ContractorDto
     */
    @Transactional(readOnly = true)
    public Page<ContractorDto> findAll(ContractorSearchForm searchForm, Pageable pageable) {
        Specification<Contractor> specification = ContractorSpecification.getContractorSpecification(searchForm);
        Page<Contractor> contractorPage = contractorRepository.findAll(specification, pageable);
        List<ContractorDto> contractorDtos = contractorPage.stream()
                .map(contractorMapper::toDto)
                .collect(Collectors.toList());
        return new PageImpl<>(contractorDtos, pageable, contractorPage.getTotalElements());
    }

    /**
     * دریافت یک پیمانکار بر اساس شناسه
     *
     * @param id شناسه پیمانکار
     * @return ContractorDto
     * @throws ResourceNotFoundException اگر پیمانکار با شناسه داده شده یافت نشد
     */
    @Transactional(readOnly = true)
    public ContractorDto findById(Long id) {
        Contractor contractor = contractorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("پیمانکار با شناسه " + id + " یافت نشد."));
        return contractorMapper.toDto(contractor);
    }

    /**
     * ایجاد یک پیمانکار جدید
     *
     * @param contractorDto داده‌های پیمانکار جدید
     * @return ContractorDto ایجاد شده
     */
    @Transactional
    public ContractorDto create(ContractorDto contractorDto) {
        Contractor contractor = contractorMapper.toEntity(contractorDto);
        Contractor savedContractor = contractorRepository.save(contractor);
        return contractorMapper.toDto(savedContractor);
    }

    /**
     * به‌روزرسانی یک پیمانکار موجود
     *
     * @param id            شناسه پیمانکار مورد نظر برای به‌روزرسانی
     * @param contractorDto داده‌های جدید برای به‌روزرسانی
     * @return ContractorDto به‌روز شده
     * @throws ResourceNotFoundException اگر پیمانکار با شناسه داده شده یافت نشد
     */
    @Transactional
    public ContractorDto update(Long id, ContractorDto contractorDto) {
        Contractor existingContractor = contractorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("پیمانکار با شناسه " + id + " یافت نشد."));
        contractorMapper.partialUpdate(contractorDto, existingContractor);
        Contractor updatedContractor = contractorRepository.save(existingContractor);
        return contractorMapper.toDto(updatedContractor);
    }

    /**
     * حذف یک پیمانکار بر اساس شناسه
     *
     * @param id شناسه پیمانکار مورد نظر برای حذف
     * @throws ResourceNotFoundException اگر پیمانکار با شناسه داده شده یافت نشد
     */
    @Transactional
    public void delete(Long id) {
        if (!contractorRepository.existsById(id)) {
            throw new ResourceNotFoundException("پیمانکار با شناسه " + id + " یافت نشد.");
        }
        contractorRepository.deleteById(id);
    }
}
