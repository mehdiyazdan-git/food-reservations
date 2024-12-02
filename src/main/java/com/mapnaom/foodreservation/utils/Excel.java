package com.mapnaom.foodreservation.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to specify how a class should be mapped to an Excel sheet.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Excel {
    String name();                // The sheet name or the name of the data section
    boolean useTitleRow() default false;  // Whether the first row is a header row
    ExcelStrategy strategy();     // The strategy for how to map the class (flat or composite)
    Class<?> parent() default Object.class; // The parent class if the field is nested (for composite structure)
}
