package com.example.demo;

import java.lang.reflect.Field;

public class TestUtils {

    public static void injectObject(String fieldName, Object source, Object target) {
        boolean flag = false;

        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            if (!field.isAccessible()) {
                field.setAccessible(true);
                flag = true;
            }
            field.set(target, source);

            if (flag) {
                field.setAccessible(false);
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }
}
