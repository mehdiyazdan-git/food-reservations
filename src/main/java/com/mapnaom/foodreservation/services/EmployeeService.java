package com.mapnaom.foodreservation.services;

import com.mapnaom.foodreservation.dtos.EmployeeDto;
import com.mapnaom.foodreservation.exceptions.ResourceNotFoundException;
import com.mapnaom.foodreservation.mappers.EmployeeMapper;
import com.mapnaom.foodreservation.models.Employee;
import com.mapnaom.foodreservation.repositories.EmployeeRepository;
import com.mapnaom.foodreservation.searchForms.EmployeeSearchForm;
import com.mapnaom.foodreservation.specifications.EmployeeSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    /**
     * دریافت تمام کارکنان به صورت صفحه‌بندی شده با شرایط جستجو
     *
     * @param searchForm فرم جستجو
     * @param pageable   اطلاعات صفحه‌بندی و مرتب‌سازی
     * @return صفحه‌ای از EmployeeDto
     */
    @Transactional(readOnly = true)
    public Page<EmployeeDto> findAll(EmployeeSearchForm searchForm, Pageable pageable) {
        Specification<Employee> specification = EmployeeSpecification.getEmployeeSpecification(searchForm);
        Page<Employee> employeePage = employeeRepository.findAll(specification, pageable);
        List<EmployeeDto> employeeDtos = employeePage.stream()
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());
        return new PageImpl<>(employeeDtos, pageable, employeePage.getTotalElements());
    }

    /**
     * دریافت یک کارمند بر اساس شناسه
     *
     * @param id شناسه کارمند
     * @return EmployeeDto
     * @throws ResourceNotFoundException اگر کارمند با شناسه داده شده یافت نشد
     */
    @Transactional(readOnly = true)
    public EmployeeDto findById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("کارمند با شناسه " + id + " یافت نشد."));
        return employeeMapper.toDto(employee);
    }

    /**
     * ایجاد یک کارمند جدید
     *
     * @param employeeDto داده‌های کارمند جدید
     * @return EmployeeDto ایجاد شده
     */
    @Transactional
    public EmployeeDto create(EmployeeDto employeeDto) {
        Employee employee = employeeMapper.toEntity(employeeDto);
        Employee savedEmployee = employeeRepository.save(employee);
        return employeeMapper.toDto(savedEmployee);
    }

    /**
     * به‌روزرسانی یک کارمند موجود
     *
     * @param id          شناسه کارمند مورد نظر برای به‌روزرسانی
     * @param employeeDto داده‌های جدید برای به‌روزرسانی
     * @return EmployeeDto به‌روز شده
     * @throws ResourceNotFoundException اگر کارمند با شناسه داده شده یافت نشد
     */
    @Transactional
    public EmployeeDto update(Long id, EmployeeDto employeeDto) {
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("کارمند با شناسه " + id + " یافت نشد."));
        employeeMapper.partialUpdate(employeeDto, existingEmployee);
        Employee updatedEmployee = employeeRepository.save(existingEmployee);
        return employeeMapper.toDto(updatedEmployee);
    }

    /**
     * حذف یک کارمند بر اساس شناسه
     *
     * @param id شناسه کارمند مورد نظر برای حذف
     * @throws ResourceNotFoundException اگر کارمند با شناسه داده شده یافت نشد
     */
    @Transactional
    public void delete(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("کارمند با شناسه " + id + " یافت نشد.");
        }
        employeeRepository.deleteById(id);
    }
}
