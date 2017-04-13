package com.exmertec.yaz.util;

import java.lang.reflect.Field;

public class ReflectionUtils {
    public static Class<?> getFieldType(Class<?> fromClass, String targetField) {
        for (Class<?> cls = fromClass; cls != null; cls = cls.getSuperclass()) {
            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals(targetField)) {
                    return field.getDeclaringClass();
                }
            }
        }

        throw new IllegalArgumentException("No field " + targetField + " on class " + fromClass);
    }
}
