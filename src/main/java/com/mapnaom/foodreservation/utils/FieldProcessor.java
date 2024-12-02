package com.mapnaom.foodreservation.utils;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class FieldProcessor {

    // Define supported types
    private static final Set<Class<?>> SUPPORTED_TYPES = Set.of(
            Boolean.class, Integer.class, String.class, Double.class, Float.class,
            Long.class, Short.class, Byte.class, Date.class, LocalDate.class,
            LocalTime.class, LocalDateTime.class
    );

    /**
     * Recursively retrieves fields from the given class, handling nested collections.
     *
     * @param clazz the class to process
     * @return a map of field names to Field objects
     */
    public static Map<String, Field> getFilteredFields(Class<?> clazz) {
        Map<String, Field> fieldMap = new LinkedHashMap<>();
        processClassFields(clazz, fieldMap, "");
        return fieldMap;
    }

    /**
     * Helper method to process fields recursively.
     *
     * @param clazz    the class to process
     * @param fieldMap the map to populate with filtered fields
     * @param prefix   the prefix for nested field names
     */
    private static void processClassFields(Class<?> clazz, Map<String, Field> fieldMap, String prefix) {
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            String fieldName = prefix.isEmpty() ? field.getName() : prefix + "." + field.getName();

            // Exclude fields named "id" (case-insensitive)
            if (field.getName().equalsIgnoreCase("id")) {
                continue;
            }

            Class<?> fieldType = field.getType();

            if (SUPPORTED_TYPES.contains(fieldType)) {
                fieldMap.put(fieldName, field);
            } else if (Collection.class.isAssignableFrom(fieldType)) {
                // Handle collections (e.g., List, Set)
                Type genericType = field.getGenericType();
                if (genericType instanceof ParameterizedType pt) {
                    Type[] actualTypeArguments = pt.getActualTypeArguments();
                    if (actualTypeArguments.length == 1) {
                        Type actualType = actualTypeArguments[0];
                        if (actualType instanceof Class<?> actualClass) {
                            if (!SUPPORTED_TYPES.contains(actualClass) && !actualClass.isEnum()) {
                                // Recursively process the generic type
                                processClassFields(actualClass, fieldMap, fieldName);
                            }
                        }
                    }
                }
            } else if (!fieldType.isArray() && !isComposite(fieldType)) {
                // Handle nested objects
                processClassFields(fieldType, fieldMap, fieldName);
            }
        }
    }


    /**
     * Determines if a class is a composite type.
     * Adjust this method based on your application's definition of "Composite."
     *
     * @param clazz the class to check
     * @return true if the class is considered composite; false otherwise
     */
    private static boolean isComposite(Class<?> clazz) {
        // Example: Assume Composite is an interface or superclass for composite types
        return Composite.class.isAssignableFrom(clazz);
    }
}
