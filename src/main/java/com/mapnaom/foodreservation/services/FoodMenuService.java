package com.mapnaom.foodreservation.services;

import com.github.mfathi91.time.PersianDate;
import com.mapnaom.foodreservation.dtos.DailyFoodOptionsDto;
import com.mapnaom.foodreservation.dtos.FoodPlanImportResponse;
import com.mapnaom.foodreservation.entities.DailyFoodOptions;
import com.mapnaom.foodreservation.entities.Food;
import com.mapnaom.foodreservation.exceptions.ResourceNotFoundException;
import com.mapnaom.foodreservation.mappers.DailyFoodOptionsMapper;
import com.mapnaom.foodreservation.repositories.v1.DailyFoodOptionsRepository;
import com.mapnaom.foodreservation.repositories.v1.FoodRepository;
import com.mapnaom.foodreservation.searchForms.FoodPlanSearchForm;
import com.mapnaom.foodreservation.specifications.FoodPlanSpecification;
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
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodPlanService {
    private final DailyFoodOptionsRepository dailyFoodOptionsRepository;
    private final DailyFoodOptionsMapper foodPlanMapper;
    private final FoodRepository foodRepository;

    /**
     * پیدا کردن تمام برنامه‌های غذایی به صورت صفحه‌بندی شده با شرایط جستجو
     *
     * @param searchForm فرم جستجو
     * @param pageable   اطلاعات صفحه‌بندی و مرتب‌سازی
     * @return صفحه‌ای از DailyFoodOptionsDto
     */
    @Transactional(readOnly = true)
    public Page<DailyFoodOptionsDto> findAll(FoodPlanSearchForm searchForm, Pageable pageable) {
        Specification<DailyFoodOptions> specification = FoodPlanSpecification.getFoodPlanSpecification(searchForm);
        Page<DailyFoodOptions> foodPlanPage = dailyFoodOptionsRepository.findAll(specification, pageable);
        List<DailyFoodOptionsDto> dailyFoodOptionsDtos = foodPlanPage.stream()
                .map(foodPlanMapper::toDto)
                .collect(Collectors.toList());
        return new PageImpl<>(dailyFoodOptionsDtos, pageable, foodPlanPage.getTotalElements());
    }

    /**
     * پیدا کردن یک برنامه غذایی بر اساس شناسه
     *
     * @param id شناسه برنامه غذایی
     * @return DailyFoodOptionsDto
     * @throws ResourceNotFoundException اگر برنامه غذایی با شناسه داده شده یافت نشد
     */
    @Transactional(readOnly = true)
    public DailyFoodOptionsDto findById(Long id) {
        DailyFoodOptions dailyFoodOptions = dailyFoodOptionsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DailyFoodOptions not found with id: " + id));
        return foodPlanMapper.toDto(dailyFoodOptions);
    }

    /**
     * ایجاد یک برنامه غذایی جدید
     *
     * @param dailyFoodOptionsDto داده‌های برنامه غذایی جدید
     * @return DailyFoodOptionsDto ایجاد شده
     */
    @Transactional
    public DailyFoodOptionsDto create(DailyFoodOptionsDto dailyFoodOptionsDto) {
        DailyFoodOptions dailyFoodOptions = foodPlanMapper.toEntity(dailyFoodOptionsDto);
        DailyFoodOptions savedDailyFoodOptions = dailyFoodOptionsRepository.save(dailyFoodOptions);
        return foodPlanMapper.toDto(savedDailyFoodOptions);
    }

    /**
     * به‌روزرسانی یک برنامه غذایی موجود
     *
     * @param id          شناسه برنامه غذایی مورد نظر برای به‌روزرسانی
     * @param dailyFoodOptionsDto داده‌های جدید برای به‌روزرسانی
     * @return DailyFoodOptionsDto به‌روز شده
     * @throws ResourceNotFoundException اگر برنامه غذایی با شناسه داده شده یافت نشد
     */
    @Transactional
    public DailyFoodOptionsDto update(Long id, DailyFoodOptionsDto dailyFoodOptionsDto) {
        DailyFoodOptions existingDailyFoodOptions = dailyFoodOptionsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DailyFoodOptions not found with id: " + id));

        foodPlanMapper.partialUpdate(dailyFoodOptionsDto, existingDailyFoodOptions);
        DailyFoodOptions updatedDailyFoodOptions = dailyFoodOptionsRepository.save(existingDailyFoodOptions);
        return foodPlanMapper.toDto(updatedDailyFoodOptions);
    }

    /**
     * حذف یک برنامه غذایی بر اساس شناسه
     *
     * @param id شناسه برنامه غذایی مورد نظر برای حذف
     * @throws ResourceNotFoundException اگر برنامه غذایی با شناسه داده شده یافت نشد
     */
    @Transactional
    public void delete(Long id) {
        if (!dailyFoodOptionsRepository.existsById(id)) {
            throw new ResourceNotFoundException("DailyFoodOptions not found with id: " + id);
        }
        dailyFoodOptionsRepository.deleteById(id);
    }

    /**
     * واردسازی گزینه‌های غذایی از طریق فایل اکسل با استفاده از Map<String, List<String>>
     *
     * @param file فایل اکسل حاوی اطلاعات گزینه‌های غذایی
     * @return پاسخ شامل تعداد رکوردهای موفق و خطاهای احتمالی
     */
    @Transactional
    public FoodPlanImportResponse importFoodPlanFromExcel(MultipartFile file) {
        int successCount = 0;
        List<String> errorMessages = new ArrayList<>();

        Map<String, List<String>> dateToFoodOptionsMap = new HashMap<>();

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
                    Cell dateCell = currentRow.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    Cell foodNameCell = currentRow.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                    // خواندن مقادیر سلول‌ها
                    String dateStr = getCellValueAsString(dateCell).trim();
                    String foodName = getCellValueAsString(foodNameCell).trim();

                    // اعتبارسنجی مقادیر ضروری
                    List<String> rowErrors = new ArrayList<>();

                    if (dateStr.isEmpty()) {
                        rowErrors.add("تاریخ نمی‌تواند خالی باشد.");
                    }

                    if (foodName.isEmpty()) {
                        rowErrors.add("نام غذا نمی‌تواند خالی باشد.");
                    }

                    LocalDate date = null;
                    if (!dateStr.isEmpty()) {
                        try {
                            date = convertJalaliToGregorian(dateStr);
                        } catch (Exception e) {
                            rowErrors.add("تاریخ نامعتبر است. فرمت مورد انتظار: YYYY/MM/DD.");
                        }
                    }

                    if (!rowErrors.isEmpty()) {
                        String errorMessage = "ردیف " + (currentRow.getRowNum() + 1) + ": " + String.join(", ", rowErrors);
                        errorMessages.add(errorMessage);
                        continue;
                    }

                    // افزودن گزینه غذایی به نقشه
                    String dateKey = date.toString(); // می‌توانید از فرمت دیگری نیز استفاده کنید
                    dateToFoodOptionsMap.computeIfAbsent(dateKey, k -> new ArrayList<>()).add(foodName);

                } catch (Exception e) {
                    String errorMessage = "ردیف " + (currentRow.getRowNum() + 1) + ": " + e.getMessage();
                    errorMessages.add(errorMessage);
                }
            }

            // پردازش نقشه و ایجاد موجودیت‌ها
            for (Map.Entry<String, List<String>> entry : dateToFoodOptionsMap.entrySet()) {
                String dateStr = entry.getKey();
                List<String> foodNames = entry.getValue();

                // اعتبارسنجی تعداد گزینه‌های غذایی (مثلاً باید دقیقاً 3 گزینه باشد)
                if (foodNames.size() != 3) {
                    errorMessages.add("تاریخ " + dateStr + ": تعداد گزینه‌های غذایی باید دقیقاً 3 باشد. تعداد فعلی: " + foodNames.size());
                    continue;
                }

                LocalDate date = LocalDate.parse(dateStr);

                // بررسی یکتایی ترکیب Food و Date
                boolean exists = dailyFoodOptionsRepository.existsByLocalDate(date);
                if (exists) {
                    errorMessages.add("تاریخ " + dateStr + ": گزینه‌های غذایی برای این تاریخ قبلاً وارد شده است.");
                    continue;
                }

                List<Food> foods = new ArrayList<>();
                boolean hasError = false;
                for (String foodName : foodNames) {
                    try {
                        Food food = foodRepository.findByName(foodName)
                                .orElseThrow(() -> new ResourceNotFoundException("غذا با نام '" + foodName + "' یافت نشد."));
                        foods.add(food);
                    } catch (ResourceNotFoundException e) {
                        errorMessages.add("تاریخ " + dateStr + ": " + e.getMessage());
                        hasError = true;
                        break;
                    }
                }

                if (hasError) {
                    continue;
                }

                // ایجاد و ذخیره موجودیت DailyFoodOptions
                DailyFoodOptions dailyFoodOptions = new DailyFoodOptions();
                dailyFoodOptions.setLocalDate(date);
                dailyFoodOptions.setFoodList(foods);
                dailyFoodOptionsRepository.save(dailyFoodOptions);
                successCount++;
            }

        } catch (Exception e) {
            throw new RuntimeException("خطا در پردازش فایل اکسل: " + e.getMessage());
        }

        return new FoodPlanImportResponse(successCount, errorMessages);
    }

    /**
     * تبدیل تاریخ جلالی به LocalDate میلادی
     *
     * @param jalaliDateStr رشته تاریخ جلالی به فرمت "yyyy/MM/dd"
     * @return LocalDate میلادی
     * @throws IllegalArgumentException اگر فرمت تاریخ نامعتبر باشد
     */
    private LocalDate convertJalaliToGregorian(String jalaliDateStr) {
        String[] parts = jalaliDateStr.split("/");
        if (parts.length != 3) {
            throw new IllegalArgumentException("فرمت تاریخ نامعتبر است.");
        }

        int jalaliYear = Integer.parseInt(parts[0]);
        int jalaliMonth = Integer.parseInt(parts[1]);
        int jalaliDay = Integer.parseInt(parts[2]);

        return PersianDate.of(jalaliYear, jalaliMonth, jalaliDay).toGregorian();
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
                    // تبدیل تاریخ به فرمت "yyyy/MM/dd"
                    LocalDate date = cell.getLocalDateTimeCellValue().toLocalDate();
                    PersianDate persianDate = PersianDate.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
                    return persianDate.getYear() +
                           "/" + String.format("%02d", persianDate.getMonthValue())
                           + "/" + String.format("%02d", persianDate.getDayOfMonth());
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
