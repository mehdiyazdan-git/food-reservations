package com.mapnaom.foodreservation.services;

import com.mapnaom.foodreservation.dtos.WorkLocationDto;
import com.mapnaom.foodreservation.dtos.WorkLocationImportResponse;
import com.mapnaom.foodreservation.entities.WorkLocation;
import com.mapnaom.foodreservation.exceptions.ResourceNotFoundException;
import com.mapnaom.foodreservation.mappers.WorkLocationMapper;
import com.mapnaom.foodreservation.repositories.v1.WorkLocationRepository;
import com.mapnaom.foodreservation.searchForms.WorkLocationSearchForm;
import com.mapnaom.foodreservation.specifications.WorkLocationSpecification;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkLocationService {
    private final WorkLocationRepository workLocationRepository;
    private final WorkLocationMapper workLocationMapper;

    /**
     * پیدا کردن تمام محل‌های کاری به صورت صفحه‌بندی شده با شرایط جستجو
     *
     * @param searchForm فرم جستجو شامل فیلدهای id و workLocationName
     * @param pageable   اطلاعات صفحه‌بندی و مرتب‌سازی
     * @return صفحه‌ای از WorkLocationDto
     */
    @Transactional(readOnly = true) // استفاده صحیح از @Transactional
    public Page<WorkLocationDto> findAll(WorkLocationSearchForm searchForm, Pageable pageable) {
        Specification<WorkLocation> specification = WorkLocationSpecification.getWorkLocationSpecification(searchForm);
        Page<WorkLocation> workLocationPage = workLocationRepository.findAll(specification, pageable);
        List<WorkLocationDto> workLocationDtos = workLocationPage.stream()
                .map(workLocationMapper::toDto)
                .collect(Collectors.toList());
        return new PageImpl<>(workLocationDtos, pageable, workLocationPage.getTotalElements());
    }

    /**
     * پیدا کردن یک محل کاری بر اساس شناسه
     *
     * @param id شناسه محل کاری
     * @return WorkLocationDto
     * @throws ResourceNotFoundException اگر محل کاری با شناسه داده شده یافت نشود
     */
    @Transactional(readOnly = true)
    public WorkLocationDto findById(Long id) {
        WorkLocation workLocation = workLocationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkLocation not found with id: " + id));
        return workLocationMapper.toDto(workLocation);
    }

    /**
     * ایجاد یک محل کاری جدید
     *
     * @param workLocationDto داده‌های محل کاری جدید
     * @return WorkLocationDto ایجاد شده
     */
    @Transactional
    public WorkLocationDto create(WorkLocationDto workLocationDto) {
        WorkLocation workLocation = workLocationMapper.toEntity(workLocationDto);
        WorkLocation savedWorkLocation = workLocationRepository.save(workLocation);
        return workLocationMapper.toDto(savedWorkLocation);
    }

    /**
     * به‌روزرسانی یک محل کاری موجود
     *
     * @param id              شناسه محل کاری مورد نظر برای به‌روزرسانی
     * @param workLocationDto داده‌های جدید برای به‌روزرسانی
     * @return WorkLocationDto به‌روز شده
     * @throws ResourceNotFoundException اگر محل کاری با شناسه داده شده یافت نشود
     */
    @Transactional
    public WorkLocationDto update(Long id, WorkLocationDto workLocationDto) {
        WorkLocation existingWorkLocation = workLocationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkLocation not found with id: " + id));

        workLocationMapper.partialUpdate(workLocationDto, existingWorkLocation);
        WorkLocation updatedWorkLocation = workLocationRepository.save(existingWorkLocation);
        return workLocationMapper.toDto(updatedWorkLocation);
    }

    /**
     * حذف یک محل کاری بر اساس شناسه
     *
     * @param id شناسه محل کاری مورد نظر برای حذف
     * @throws ResourceNotFoundException اگر محل کاری با شناسه داده شده یافت نشود
     */
    @Transactional
    public void delete(Long id) {
        if (!workLocationRepository.existsById(id)) {
            throw new ResourceNotFoundException("WorkLocation not found with id: " + id);
        }
        workLocationRepository.deleteById(id);
    }

    /**
     * واردسازی مکان‌های کاری از طریق فایل اکسل
     *
     * @param file فایل اکسل حاوی اطلاعات مکان‌های کاری
     * @return پاسخ شامل تعداد رکوردهای موفق و خطاهای احتمالی
     */
    @Transactional
    public WorkLocationImportResponse importWorkLocationsFromExcelFile(MultipartFile file) {
        int successCount = 0;
        List<String> errorMessages = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0); // فرض بر این است که اطلاعات در اولین شیت قرار دارد
            Iterator<Row> rows = sheet.iterator();

            // فرض بر این است که اولین ردیف شامل عنوان ستون‌ها است
            if (rows.hasNext()) {
                rows.next(); // رد کردن ردیف عنوان
            }

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                try {
                    Cell workLocationNameCell = currentRow.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                    // خواندن مقدار سلول
                    String workLocationName = getCellValueAsString(workLocationNameCell).trim();

                    // اعتبارسنجی مقدار
                    List<String> rowErrors = new ArrayList<>();

                    if (workLocationName.isEmpty()) {
                        rowErrors.add("نام مکان کاری نمی‌تواند خالی باشد.");
                    } else if (workLocationName.length() < 2 || workLocationName.length() > 100) {
                        rowErrors.add("نام مکان کاری باید بین 2 تا 100 کاراکتر باشد.");
                    } else {
                        // کنترل یکتایی (در صورت نیاز)
                        if (workLocationRepository.existsByWorkLocationName(workLocationName)) {
                            rowErrors.add("نام مکان کاری '" + workLocationName + "' قبلاً وارد شده است.");
                        }
                    }

                    if (!rowErrors.isEmpty()) {
                        String errorMessage = "ردیف " + (currentRow.getRowNum() + 1) + ": " + String.join(", ", rowErrors);
                        errorMessages.add(errorMessage);
                        continue;
                    }

                    // ایجاد موجودیت WorkLocation
                    WorkLocation workLocation = new WorkLocation();
                    workLocation.setWorkLocationName(workLocationName);

                    workLocationRepository.save(workLocation);
                    successCount++;

                } catch (Exception e) {
                    String errorMessage = "ردیف " + (currentRow.getRowNum() + 1) + ": " + e.getMessage();
                    errorMessages.add(errorMessage);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("خطا در پردازش فایل اکسل: " + e.getMessage());
        }

        return new WorkLocationImportResponse(successCount, errorMessages);
    }

    /**
     * تبدیل مقدار سلول به رشته
     *
     * @param cell سلول مورد نظر
     * @return مقدار سلول به صورت رشته
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    // تبدیل تاریخ به فرمت YYYY-MM-DD (در صورت نیاز)
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                // برای ساده‌سازی، فرض می‌کنیم که فرمول نتیجه‌ای به رشته دارد
                return cell.getStringCellValue();
            case BLANK:
            case _NONE:
            case ERROR:
            default:
                return "";
        }
    }
}
