package com.example.demo.utils;

import java.lang.reflect.Field;

public class TestUtil {

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
