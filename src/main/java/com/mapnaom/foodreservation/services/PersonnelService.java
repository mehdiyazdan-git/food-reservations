package com.mapnaom.foodreservation.services;

import com.mapnaom.foodreservation.dtos.PersonnelDto;
import com.mapnaom.foodreservation.dtos.PersonnelImportResponse;
import com.mapnaom.foodreservation.entities.Personnel;
import com.mapnaom.foodreservation.entities.WorkLocation;
import com.mapnaom.foodreservation.exceptions.ResourceNotFoundException;
import com.mapnaom.foodreservation.mappers.PersonnelMapper;
import com.mapnaom.foodreservation.repositories.v1.PersonnelRepository;
import com.mapnaom.foodreservation.repositories.v1.WorkLocationRepository;
import com.mapnaom.foodreservation.searchForms.PersonnelSearchForm;
import com.mapnaom.foodreservation.specifications.PersonnelSpecification;
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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonnelService {
    private final PersonnelRepository personnelRepository;
    private final PersonnelMapper personnelMapper;
    private final WorkLocationRepository workLocationRepository;

    /**
     * پیدا کردن تمام پرسنل‌ به صورت صفحه‌بندی شده با شرایط جستجو
     *
     * @param searchForm فرم جستجو شامل فیلدهای id، firstname، lastname، code و workLocationName
     * @param pageable   اطلاعات صفحه‌بندی و مرتب‌سازی
     * @return صفحه‌ای از PersonnelDto
     */
    @Transactional(readOnly = true)
    public Page<PersonnelDto> findAll(PersonnelSearchForm searchForm, Pageable pageable) {
        Specification<Personnel> specification = PersonnelSpecification.getPersonnelSpecification(searchForm);
        Page<Personnel> personnelPage = personnelRepository.findAll(specification, pageable);
        List<PersonnelDto> personnelDtos = personnelPage.stream()
                .map(personnelMapper::toDto)
                .collect(Collectors.toList());
        return new PageImpl<>(personnelDtos, pageable, personnelPage.getTotalElements());
    }

    /**
     * پیدا کردن کارمند بر اساس شناسه
     *
     * @param id شناسه کارمند
     * @return PersonnelDto
     * @throws ResourceNotFoundException اگر کارمند با شناسه داده شده یافت نشد
     */
    @Transactional(readOnly = true)
    public PersonnelDto findById(Long id) {
        Personnel personnel = personnelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Personnel not found with id: " + id));
        return personnelMapper.toDto(personnel);
    }

    /**
     * ایجاد یک کارمند جدید
     *
     * @param personnelDto داده‌های کارمند جدید
     * @return PersonnelDto ایجاد شده
     */
    @Transactional
    public PersonnelDto create(PersonnelDto personnelDto) {
        Personnel personnel = personnelMapper.toEntity(personnelDto);
        Personnel savedPersonnel = personnelRepository.save(personnel);
        return personnelMapper.toDto(savedPersonnel);
    }

    /**
     * به‌روزرسانی کارمند بر مبنای شناسه
     *
     * @param id شناسه کارمند مورد نظر برای به‌روزرسانی
     * @param personnelDto داده‌های جدید برای به‌روزرسانی
     * @return PersonnelDto به‌روز شده
     * @throws ResourceNotFoundException اگر کارمند با شناسه داده شده یافت نشد
     */
    @Transactional
    public PersonnelDto update(Long id, PersonnelDto personnelDto) {
        Personnel existingPersonnel = personnelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Personnel not found with id: " + id));

        personnelMapper.partialUpdate(personnelDto, existingPersonnel);
        Personnel updatedPersonnel = personnelRepository.save(existingPersonnel);
        return personnelMapper.toDto(updatedPersonnel);
    }

    /**
     * حذف یک پرسنل بر اساس شناسه
     *
     * @param id شناسه پرسنل مورد نظر برای حذف
     * @throws ResourceNotFoundException اگر پرسنل با شناسه داده شده یافت نشد
     */
    @Transactional
    public void delete(Long id) {
        if (!personnelRepository.existsById(id)) {
            throw new ResourceNotFoundException("Personnel not found with id: " + id);
        }
        personnelRepository.deleteById(id);
    }


    /**
     * ورود لیست پرسنل از فایل اکسل
     *
     * @param file فایل اکسل حاوی اطلاعات پرسنل
     * @return پاسخ شامل تعداد رکوردهای موفق و خطاهای احتمالی
     */
    @Transactional
    public PersonnelImportResponse importPersonnelFromExcel(MultipartFile file) {
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
                    // حذف پردازش فیلد ID
                    Cell firstnameCell = currentRow.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    Cell lastnameCell = currentRow.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    Cell codeCell = currentRow.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    Cell workLocationIdCell = currentRow.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                    // خواندن مقادیر دیگر
                    String firstname = getCellValueAsString(firstnameCell).trim();
                    String lastname = getCellValueAsString(lastnameCell).trim();
                    String code = getCellValueAsString(codeCell).trim();
                    String workLocationIdStr = getCellValueAsString(workLocationIdCell).trim();

                    // اعتبارسنجی مقادیر ضروری
                    List<String> rowErrors = new ArrayList<>();

                    if (firstname.isEmpty()) {
                        rowErrors.add("نام کوچک نمی‌تواند خالی باشد.");
                    } else if (firstname.length() < 2 || firstname.length() > 50) {
                        rowErrors.add("نام کوچک باید بین 2 تا 50 کاراکتر باشد.");
                    }

                    if (lastname.isEmpty()) {
                        rowErrors.add("نام خانوادگی نمی‌تواند خالی باشد.");
                    } else if (lastname.length() < 2 || lastname.length() > 50) {
                        rowErrors.add("نام خانوادگی باید بین 2 تا 50 کاراکتر باشد.");
                    }

                    if (code.isEmpty()) {
                        rowErrors.add("کد نمی‌تواند خالی باشد.");
                    } else if (code.length() < 4 || code.length() > 10) {
                        rowErrors.add("کد باید بین 4 تا 10 کاراکتر باشد.");
                    } else {
                        // کنترل یکتایی فیلد code
                        boolean codeExists = personnelRepository.existsByCode(code);
                        if (codeExists) {
                            rowErrors.add("کد '" + code + "' تکراری است.");
                        }
                    }

                    Long workLocationId = null;
                    if (!workLocationIdStr.isEmpty()) {
                        try {
                            workLocationId = Long.parseLong(workLocationIdStr);
                            // بررسی وجود WorkLocation با این ID
                            if (!workLocationRepository.existsById(workLocationId)) {
                                rowErrors.add("محل خدمت با شناسه " + workLocationId + " وجود ندارد.");
                            }
                        } catch (NumberFormatException e) {
                            rowErrors.add("شناسه محل خدمت باید عددی باشد.");
                        }
                    } else {
                        rowErrors.add("شناسه محل خدمت نمی‌تواند خالی باشد.");
                    }

                    if (!rowErrors.isEmpty()) {
                        String errorMessage = "ردیف " + (currentRow.getRowNum() + 1) + ": " + String.join(", ", rowErrors);
                        errorMessages.add(errorMessage);
                        continue;
                    }

                    // ایجاد موجودیت Personnel
                    Personnel personnel = new Personnel();
                    personnel.setFirstname(firstname);
                    personnel.setLastname(lastname);
                    personnel.setCode(code);

                    if (workLocationId != null) {
                        // کپی workLocationId به یک متغیر موقت نهایی
                        final Long finalWorkLocationId = workLocationId;
                        WorkLocation workLocation = workLocationRepository.findById(finalWorkLocationId)
                                .orElseThrow(() -> new ResourceNotFoundException("WorkLocation not found with id: " + finalWorkLocationId));
                        personnel.setWorkLocation(workLocation);
                    } else {
                        personnel.setWorkLocation(null);
                    }

                    personnelRepository.save(personnel);
                    successCount++;
                } catch (Exception e) {
                    String errorMessage = "ردیف " + (currentRow.getRowNum() + 1) + ": " + e.getMessage();
                    errorMessages.add(errorMessage);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("خطا در پردازش فایل اکسل: " + e.getMessage());
        }

        return new PersonnelImportResponse(successCount, errorMessages);
    }
    

    /**
     * صادر کردن رکوردهای فیلتر شده به صورت فایل اکسل
     *
     * @param pageable   اطلاعات صفحه‌بندی و مرتب‌سازی
     * @param searchForm فرم جستجو شامل فیلدهای id، firstname، lastname، code و workLocationName
     * @return بایت‌های فایل اکسل
     */
    @Transactional(readOnly = true)
    public byte[] exportFilteredRecordsToExcel(Pageable pageable, PersonnelSearchForm searchForm) {
        Specification<Personnel> specification = PersonnelSpecification.getPersonnelSpecification(searchForm);
        Page<Personnel> personnelPage = personnelRepository.findAll(specification, pageable);
        List<Personnel> personnelList = personnelPage.getContent();

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("لیست پرسنل");

            // ایجاد ردیف عنوان
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("شناسه");
            headerRow.createCell(1).setCellValue("نام");
            headerRow.createCell(2).setCellValue("نام خانوادگی");
            headerRow.createCell(3).setCellValue("کد پرسنلی");
            headerRow.createCell(4).setCellValue("محل خدمت");

            // پر کردن داده‌ها
            int rowIdx = 1;
            for (Personnel personnel : personnelList) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(personnel.getId());
                row.createCell(1).setCellValue(personnel.getFirstname());
                row.createCell(2).setCellValue(personnel.getLastname());
                row.createCell(3).setCellValue(personnel.getCode());

                String workLocationName = (personnel.getWorkLocation() != null) ? personnel.getWorkLocation().getWorkLocationName() : "";
                row.createCell(4).setCellValue(workLocationName);
            }

            // تنظیم اندازه ستون‌ها
            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("خطا در تولید فایل اکسل: " + e.getMessage());
        }
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
                    return String.valueOf((long) cell.getNumericCellValue());
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
