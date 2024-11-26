package com.mapnaom.foodreservation.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mapnaom.foodreservation.dtos.EmployeeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class EmployeeScheduler {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // URL سرور خارجی
    private static final String EMPLOYEE_API_URL = "https://external-server.com/api/employees";

    public EmployeeScheduler(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    // متدی که هر شب ساعت 9 اجرا می‌شود
    @Scheduled(cron = "0 0 21 * * ?") // کرون برای ساعت 9 شب
    public void fetchActiveEmployees() {
        try {
            log.info("Fetching active employees from external server...");

            // فراخوانی API سرور خارجی
            String response = restTemplate.getForObject(EMPLOYEE_API_URL, String.class);

            // تبدیل JSON به لیست EmployeeDto
            List<EmployeeDto> employees = objectMapper.readValue(response, new TypeReference<List<EmployeeDto>>() {});

            // فیلتر کردن کارمندان فعال
            List<EmployeeDto> activeEmployees = filterActiveEmployees(employees);

            // چاپ لیست کارمندان فعال
            log.info("Active Employees: {}", activeEmployees);

        } catch (Exception e) {
            log.error("Error fetching employees: ", e);
        }
    }

    // متدی برای فیلتر کردن کارمندان فعال
    private List<EmployeeDto> filterActiveEmployees(List<EmployeeDto> employees) {
        if (employees == null) {
            return new ArrayList<>();
        }
        return employees.stream()
                .filter(EmployeeDto::isActive) // فقط کارمندان فعال
                .toList();
    }
}