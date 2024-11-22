package com.mapnaom.foodreservation.controllers;

import com.mapnaom.foodreservation.dtos.PersonnelDto;
import com.mapnaom.foodreservation.dtos.PersonnelImportResponse;
import com.mapnaom.foodreservation.searchForms.PersonnelSearchForm;
import com.mapnaom.foodreservation.services.PersonnelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * کنترلر برای مدیریت عملیات‌های مربوط به پرسنل‌ها
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/personnel")
@RequiredArgsConstructor
public class PersonnelController {
    private final PersonnelService personnelService;

    /**
     * دریافت تمام پرسنل‌ها به صورت صفحه‌بندی شده با شرایط جستجو
     *
     * @param page          شماره صفحه (پیش‌فرض: 0)
     * @param size          تعداد آیتم‌ها در هر صفحه (پیش‌فرض: 10)
     * @param sortBy        فیلدی که بر اساس آن مرتب‌سازی می‌شود (پیش‌فرض: "id")
     * @param order         نوع مرتب‌سازی (ASC یا DESC) (پیش‌فرض: "ASC")
     * @param searchForm    فرم جستجو شامل فیلدهای id، firstname، lastname، code و workLocationName
     * @return صفحه‌ای از PersonnelDto
     */
    @GetMapping
    public ResponseEntity<Page<PersonnelDto>> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "order", defaultValue = "ASC") String order,
            @ModelAttribute PersonnelSearchForm searchForm
    ) {
        Sort.Direction sortDirection = order.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<PersonnelDto> personnelPage = personnelService.findAll(searchForm, pageable);
        return ResponseEntity.ok(personnelPage);
    }

    /**
     * دریافت یک پرسنل بر اساس شناسه
     *
     * @param id شناسه پرسنل
     * @return PersonnelDto
     */
    @GetMapping("/{id}")
    public ResponseEntity<PersonnelDto> findById(@PathVariable Long id) {
        PersonnelDto personnel = personnelService.findById(id);
        return ResponseEntity.ok(personnel);
    }

    /**
     * ایجاد یک پرسنل جدید
     *
     * @param personnelDto داده‌های پرسنل جدید
     * @return PersonnelDto ایجاد شده
     */
    @PostMapping
    public ResponseEntity<PersonnelDto> create(@Valid @RequestBody PersonnelDto personnelDto) {
        PersonnelDto createdPersonnel = personnelService.create(personnelDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPersonnel);
    }

    /**
     * به‌روزرسانی یک پرسنل موجود
     *
     * @param id شناسه پرسنل مورد نظر برای به‌روزرسانی
     * @param personnelDto داده‌های جدید برای به‌روزرسانی
     * @return PersonnelDto به‌روز شده
     */
    @PutMapping("/{id}")
    public ResponseEntity<PersonnelDto> update(@PathVariable Long id, @Valid @RequestBody PersonnelDto personnelDto) {
        PersonnelDto updatedPersonnel = personnelService.update(id, personnelDto);
        return ResponseEntity.ok(updatedPersonnel);
    }

    /**
     * حذف یک پرسنل بر اساس شناسه
     *
     * @param id شناسه پرسنل مورد نظر برای حذف
     * @return ResponseEntity بدون محتوا
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        personnelService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * واردسازی پرسنل‌ها از طریق فایل اکسل
     *
     * @param file فایل اکسل حاوی اطلاعات پرسنل‌ها
     * @return پاسخ شامل تعداد رکوردهای موفق و خطاهای احتمالی
     */
    @PostMapping("/import")
    public ResponseEntity<PersonnelImportResponse> importPersonnel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new PersonnelImportResponse(0, List.of("فایل ارسال شده خالی است.")));
        }

        String contentType = file.getContentType();
        if (!"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType)
            && !"application/vnd.ms-excel".equals(contentType)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new PersonnelImportResponse(0, List.of("فایل ارسال شده اکسل نیست.")));
        }

        try {
            PersonnelImportResponse response = personnelService.importPersonnelFromExcel(file);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new PersonnelImportResponse(0, List.of("خطای سروری رخ داده است: " + e.getMessage())));
        }
    }

    /**
     * صادر کردن رکوردهای فیلتر شده به صورت فایل اکسل
     *
     * @param page          شماره صفحه (پیش‌فرض: 0)
     * @param size          تعداد آیتم‌ها در هر صفحه (پیش‌فرض: 10)
     * @param sortBy        فیلدی که بر اساس آن مرتب‌سازی می‌شود (پیش‌فرض: "id")
     * @param order         نوع مرتب‌سازی (ASC یا DESC) (پیش‌فرض: "ASC")
     * @param searchForm    فرم جستجو شامل فیلدهای id، firstname، lastname، code و workLocationName
     * @return فایل اکسل حاوی رکوردهای فیلتر شده
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportFiteredRecordsToexcel(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "order", defaultValue = "ASC") String order,
            @ModelAttribute PersonnelSearchForm searchForm
    ) {
        try {
            Sort.Direction sortDirection = order.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            byte[] excelBytes = personnelService.exportFilteredRecordsToExcel(pageable, searchForm);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "personnel_records.xlsx");

            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            // در صورت بروز خطا، می‌توانید یک پاسخ مناسب بازگردانید
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
