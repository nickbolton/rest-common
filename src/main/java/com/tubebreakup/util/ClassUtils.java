package com.tubebreakup.util;

public class ClassUtils {
    public static Boolean isInstanceOf(Object obj, Class<?> targetClazz) {
        if (obj == null || targetClazz == null) {
            return false;
        }
        try {
            Class clazz = obj.getClass().getClassLoader().loadClass(targetClazz.getName());
            return clazz.isInstance(obj);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
