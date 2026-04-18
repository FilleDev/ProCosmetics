/*
 * This file is part of ProCosmetics - https://github.com/FilleDev/ProCosmetics
 * Copyright (C) 2025-2026 FilleDev and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package se.filledev.procosmetics.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtil {

    public static Object getHandle(Class<?> clazz, Object object) {
        try {
            return clazz.getMethod("getHandle").invoke(object);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getHandle(Object object) {
        return getHandle(object.getClass(), object);
    }

    public static Class<?> getClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Class<?> getNMSClass(String classPath) {
        return getClass("net.minecraft." + classPath);
    }

    public static Class<?> getBukkitClass(String classPath) {
        return getClass("org.bukkit.craftbukkit." + classPath);
    }

    public static Method getMethod(Class<?> clazz, String name, Class<?>... args) {
        if (name == null) {
            return null;
        }
        for (Method m : clazz.getMethods()) {
            if (m.getName().equals(name) && (args.length == 0 || classList(args, m.getParameterTypes()))) {
                m.setAccessible(true);
                return m;
            }
        }
        return null;
    }

    public static Method getDeclaredMethod(Class<?> clazz, String name, Class<?>... args) {
        if (name == null) {
            return null;
        }
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getName().equals(name) && (args.length == 0 || classList(args, m.getParameterTypes()))) {
                m.setAccessible(true);
                return m;
            }
        }
        return null;
    }

    public static Field getDeclaredField(Class<?> clazz, String name) {
        if (name == null) {
            return null;
        }
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Field getDeclaredField(Class<?> clazz, String name, Class<?> type) {
        if (name == null) {
            return null;
        }
        try {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getName().equals(name) && field.getType().equals(type)) {
                    field.setAccessible(true);
                    return field;
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Field getField(Class<?> clazz, String name) {
        if (name == null) {
            return null;
        }
        try {
            Field field = clazz.getField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... parameterTypes) {
        if (clazz == null) {
            return null;
        }
        for (Constructor<?> constructor : clazz.getConstructors()) {
            if (matchesParameters(constructor.getParameterTypes(), parameterTypes)) {
                return constructor;
            }
        }
        return null;
    }

    private static boolean matchesParameters(Class<?>[] methodParams, Class<?>[] providedParams) {
        if (providedParams.length == 0) {
            return methodParams.length == 0;
        }
        if (methodParams.length != providedParams.length) {
            return false;
        }
        for (int i = 0; i < methodParams.length; i++) {
            if (!methodParams[i].equals(providedParams[i]) && !methodParams[i].isAssignableFrom(providedParams[i])) {
                return false;
            }
        }
        return true;
    }

    private static boolean classList(Class<?>[] l1, Class<?>[] l2) {
        boolean equal = true;

        if (l1.length != l2.length) {
            return false;
        }
        for (int i = 0; i < l1.length; i++) {
            if (l1[i] != l2[i]) {
                equal = false;
                break;
            }
        }
        return equal;
    }
}
