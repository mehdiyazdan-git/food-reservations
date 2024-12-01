package com.mapnaom.foodreservation.controllers;

import com.mapnaom.foodreservation.dtos.BranchDto;
import com.mapnaom.foodreservation.dtos.Select;
import com.mapnaom.foodreservation.searchForms.BranchSearchForm;
import com.mapnaom.foodreservation.services.BranchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

/**
 * کنترلر برای مدیریت عملیات‌های مربوط به شعب
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    /**
     * دریافت تمام شعب به صورت صفحه‌بندی شده با شرایط جستجو
     *
     * @param page       شماره صفحه (پیش‌فرض: 0)
     * @param size       تعداد آیتم‌ها در هر صفحه (پیش‌فرض: 10)
     * @param sortBy     فیلدی که بر اساس آن مرتب‌سازی می‌شود (پیش‌فرض: "id")
     * @param order      نوع مرتب‌سازی (ASC یا DESC) (پیش‌فرض: "ASC")
     * @param searchForm فرم جستجو شامل فیلدهای مختلف برای فیلتر کردن
     * @return صفحه‌ای از BranchDto
     */
    @GetMapping
    public ResponseEntity<Page<BranchDto>> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "order", defaultValue = "ASC") String order,
            @ModelAttribute BranchSearchForm searchForm
    ) {
        Sort.Direction sortDirection = order.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<BranchDto> branches = branchService.findAll(searchForm, pageable);
        return ResponseEntity.ok(branches);
    }

    /**
     * دریافت یک شعبه بر اساس شناسه
     *
     * @param id شناسه شعبه
     * @return BranchDto
     */
    @GetMapping("/{id}")
    public ResponseEntity<BranchDto> findById(@PathVariable Long id) {
        BranchDto branchDto = branchService.findById(id);
        return ResponseEntity.ok(branchDto);
    }


    @GetMapping(path = "/select")
    public ResponseEntity<List<Select>> selectList(@RequestParam(name = "searchKey",defaultValue = "",required = false) String searchKey) {
        return ResponseEntity.ok(branchService.selectList(searchKey));
    }

    /**
     * ایجاد یک شعبه جدید
     *
     * @param branchDto داده‌های شعبه جدید
     * @return BranchDto ایجاد شده
     */
    @PostMapping
    public ResponseEntity<BranchDto> create(@Valid @RequestBody BranchDto branchDto) {
        BranchDto createdBranch = branchService.create(branchDto);
        return ResponseEntity.status(201).body(createdBranch);
    }

    /**
     * به‌روزرسانی یک شعبه موجود
     *
     * @param id        شناسه شعبه مورد نظر برای به‌روزرسانی
     * @param branchDto داده‌های جدید برای به‌روزرسانی
     * @return BranchDto به‌روز شده
     */
    @PutMapping("/{id}")
    public ResponseEntity<BranchDto> update(@PathVariable Long id, @Valid @RequestBody BranchDto branchDto) {
        BranchDto updatedBranch = branchService.update(id, branchDto);
        return ResponseEntity.ok(updatedBranch);
    }

    /**
     * حذف یک شعبه بر اساس شناسه
     *
     * @param id شناسه شعبه مورد نظر برای حذف
     * @return ResponseEntity بدون محتوا
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        branchService.delete(id);
        return ResponseEntity.noContent().build();
    }
    /**
     * Endpoint to import foods from an uploaded Excel file.
     *
     * @param file The uploaded Excel file containing food data.
     * @return ResponseEntity containing ImportResponse with import results.
     */
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importFoodsFromExcel(
            @RequestParam("file") MultipartFile file){
          try {

              if (!Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
                  return new ResponseEntity<>("Invalid file type. Please upload an Excel file.", HttpStatus.BAD_REQUEST);
              }
              return new ResponseEntity<>(branchService.importBranchesFromExcel(file), HttpStatus.OK);
          }catch (Exception e){
              return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
          }
    }
}
