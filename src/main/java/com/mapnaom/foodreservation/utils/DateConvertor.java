package com.mapnaom.foodreservation.utils;

import com.github.eloyzone.jalalicalendar.DateConverter;
import com.github.eloyzone.jalalicalendar.JalaliDate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DateConvertor {

    public static LocalDate convertJalaliToGregorian(String jalaliDate) {
        DateConverter dateConverter = getDateConverter();

        String[] parts = jalaliDate.split("/");
        int jalaliYear = Integer.parseInt(parts[0]);
        int jalaliMonth = Integer.parseInt(parts[1]);
        int jalaliDay = Integer.parseInt(parts[2]);

        return dateConverter.jalaliToGregorian(jalaliYear, jalaliMonth, jalaliDay);
    }

    public static String convertGregorianToJalali(LocalDate localDate) {

        DateConverter dateConverter = getDateConverter();
        JalaliDate jalaliDate = dateConverter.gregorianToJalali(
                localDate.getYear(),
                localDate.getMonthValue(),
                localDate.getDayOfMonth()
        );

        return String.format("%d/%02d/%02d",
                jalaliDate.getYear(), jalaliDate.getMonthPersian().getValue(), jalaliDate.getDay());
    }

    private static DateConverter getDateConverter() {
        return new DateConverter();
    }
}
