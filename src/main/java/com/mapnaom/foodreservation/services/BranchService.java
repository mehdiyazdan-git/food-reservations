package com.mapnaom.foodreservation.services;

import com.mapnaom.foodreservation.dtos.BranchDto;
import com.mapnaom.foodreservation.dtos.ImportResponse;
import com.mapnaom.foodreservation.dtos.Select;
import com.mapnaom.foodreservation.exceptions.ResourceNotFoundException;
import com.mapnaom.foodreservation.mappers.BranchMapper;
import com.mapnaom.foodreservation.entities.Branch;
import com.mapnaom.foodreservation.repositories.BranchRepository;
import com.mapnaom.foodreservation.searchForms.BranchSearchForm;
import com.mapnaom.foodreservation.specifications.BranchSpecification;
import com.mapnaom.foodreservation.utils.ExcelCellError;
import com.mapnaom.foodreservation.utils.ExcelImporter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;
    private final BranchMapper branchMapper;

    Logger logger = LoggerFactory.getLogger(BranchService.class);

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


    @Transactional
    public ImportResponse<BranchDto> importBranchesFromExcel(MultipartFile file) {
        ImportResponse<BranchDto> response = new ImportResponse<>();

        // Preliminary validations
        if (file.isEmpty()) {
            response.incrementFailed();
            response.addError(-1, new ExcelCellError("فایل انتخاب نشده است."));
            return response;
        }

        String contentType = file.getContentType();
        if (!"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType) &&
                !"application/vnd.ms-excel".equals(contentType)) {
            response.incrementFailed();
            response.addError(-1, new ExcelCellError("نوع فایل نامعتبر است. لطفاً یک فایل Excel آپلود کنید."));
            return response;
        }

        try {
            // Step 1: Import branches from Excel
            response = ExcelImporter.importFromExcel(file, BranchDto.class);

            // Step 2: Fetch existing branches to check for duplicates
            Map<String, Branch> branchMap = branchRepository.findAll()
                    .stream()
                    .collect(Collectors.toMap(Branch::getName, branch -> branch));

            // Step 3: Validate and prepare branches for saving using a for-loop
            List<BranchDto> successfulImports = response.getSuccessfulImports();
            List<Branch> validatedList = new ArrayList<>();
            List<BranchDto> successfullyProcessed = new ArrayList<>(); // Temporary list to store successfully processed branches

            for (BranchDto branchDto : successfulImports) {
                if (branchMap.containsKey(branchDto.getName())) {
                    // Add error if branch with the same name already exists
                    response.addError(branchDto.getId().intValue(),
                            new ExcelCellError("شعبه با نام " + branchDto.getName() + " قبلا اضافه شده است."));
                    response.incrementFailed();
                } else {
                    validatedList.add(branchMapper.toEntity(branchDto));
                    successfullyProcessed.add(branchDto); // Store successfully processed items
                }
            }

            // Step 4: Save validated branches to the repository
            if (!validatedList.isEmpty()) {
                List<Branch> savedAll = branchRepository.saveAll(validatedList);
                // Add successfully saved branches to the response
                ImportResponse<BranchDto> finalResponse = response;
                savedAll.forEach(branch -> finalResponse.incrementSuccess(branchMapper.toDto(branch)));
            }

            // Add the successfully processed items to the successfulImports list
            response.getSuccessfulImports().addAll(successfullyProcessed);

        } catch (DataAccessException e) {
            // Handle exceptions related to database access
            response.incrementFailed();
            response.addError(-1, new ExcelCellError("خطا در دسترسی به پایگاه داده: " + e.getMessage()));
            logger.error("DataAccessException during importBranchesFromExcel: ", e);

        } catch (Exception e) {
            // Catch-all for any other exceptions
            response.incrementFailed();
            response.addError(-1, new ExcelCellError("خطای غیرمنتظره: " + e.getMessage()));
            logger.error("Unexpected exception during importBranchesFromExcel: ", e);
        }
        return response;
    }


}
