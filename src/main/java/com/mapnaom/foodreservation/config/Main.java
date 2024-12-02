package com.mapnaom.foodreservation.config;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        // Testing getFlatStructure with the Row class
        Map<String, Type> flatStructure = getFlatStructure(Row.class);
        flatStructure.forEach((name, type) -> System.out.println(name + ": " + type.getTypeName()));
    }


    private static class Workbook {
        private Integer id;
        private String name;
        private Sheet[] sheets;
    }

    private static class Sheet {
        private Integer id;
        private String name;
        private Row[] rows;
    }

    private static class Row {
        private Integer id;
        private Style style;
        private Cell[] cells;  // We will not include this field in the map itself
    }
    private static class Style {
        private Integer id;
        private String name;
    }

    private static class Cell {
        private Integer id;
        private String value;
        private String type; // "string", "number", "boolean", "date", "formula"
    }

    /**
     * Returns all fields of a class in a flat structure. For fields of parameterized type,
     * it retrieves fields recursively and returns them as a flat map (parent and child fields).
     *
     * @param clazz the class to process
     * @return a map where keys are the field names (in dot notation for nested fields)
     * and values are the field types
     */
    private static Map<String, Type> getFlatStructure(Class<?> clazz) {
        Map<String, Type> flatFields = new LinkedHashMap<>();
        processFields(clazz, flatFields, "");
        return flatFields;
    }

    /**
     * Processes fields recursively, flattening parameterized types.
     *
     * @param clazz    the class whose fields are being processed
     * @param flatMap  the map to store flattened fields and their types
     * @param prefix   prefix for nested fields (used for representing parent-child relationships)
     */
    private static void processFields(Class<?> clazz, Map<String, Type> flatMap, String prefix) {
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            String fieldName = prefix.isEmpty() ? field.getName() : prefix + "." + field.getName();
            Class<?> fieldType = field.getType();

            // If it's a simple type (primitive, wrapper class, String, etc.), add it to the map
            if (isSimpleType(fieldType)) {
                flatMap.put(fieldName, fieldType);
            }
            // If it's a collection or array, process its elements (but skip adding the field itself to the map)
            else if (Collection.class.isAssignableFrom(fieldType) || fieldType.isArray()) {
                Type genericType = field.getGenericType();
                if (genericType instanceof ParameterizedType parameterizedType) {
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    if (actualTypeArguments.length == 1) {
                        Type actualType = actualTypeArguments[0];
                        if (actualType instanceof Class<?> actualClass) {
                            // Recursively process parameterized type
                            processFields(actualClass, flatMap, fieldName);
                        }
                    }
                } else if (fieldType.isArray()) {
                    // Handle array of objects (e.g., Cell[])
                    Class<?> arrayComponentType = fieldType.getComponentType();
                    processFields(arrayComponentType, flatMap, fieldName);
                }
            }
            // If it's not a simple type, process it (complex types)
            else {
                // Recursively process complex type fields
                processFields(fieldType, flatMap, fieldName);
            }
        }
    }

    /**
     * Determines if a given class is a simple type (e.g., primitive, wrapper, or String).
     *
     * @param clazz the class to check
     * @return true if the class is a simple type, false otherwise
     */
    private static boolean isSimpleType(Class<?> clazz) {
        return clazz.isPrimitive() ||
                clazz.equals(String.class) ||
                clazz.equals(Boolean.class) ||
                clazz.equals(Integer.class) ||
                clazz.equals(Long.class) ||
                clazz.equals(Double.class) ||
                clazz.equals(Float.class) ||
                clazz.equals(Short.class) ||
                clazz.equals(Byte.class) ||
                clazz.equals(Character.class) ||
                clazz.equals(Date.class) ||
                clazz.equals(LocalDate.class) ||
                clazz.equals(LocalTime.class) ||
                clazz.equals(LocalDateTime.class);
    }
}
