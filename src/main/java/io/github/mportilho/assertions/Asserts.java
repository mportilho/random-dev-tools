package io.github.mportilho.assertions;

import java.util.Collection;

public final class Asserts {

    public static boolean isEmpty(String value) {
        return value == null || value.isBlank();
    }

    public static <T> boolean isEmpty(T[] value) {
        return value == null || value.length == 0;
    }

    public static boolean isEmpty(Collection<?> value) {
        return value == null || value.isEmpty();
    }

    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

    public static <T> boolean isNotEmpty(T[] value) {
        return !isEmpty(value);
    }

    public static boolean isNotEmpty(Collection<?> value) {
        return !isEmpty(value);
    }

    public static void assertNotEmpty(String value, String message) {
        if (isEmpty(value)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void assertNotEmpty(String value) {
        if (isEmpty(value)) {
            throw new IllegalArgumentException();
        }
    }

    public static <T> void assertNotEmpty(T[] value, String message) {
        if (isEmpty(value)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static <T> void assertNotEmpty(T[] value) {
        if (isEmpty(value)) {
            throw new IllegalArgumentException();
        }
    }

    public static void assertNotEmpty(Collection<?> value, String message) {
        if (isEmpty(value)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void assertNotEmpty(Collection<?> value) {
        if (isEmpty(value)) {
            throw new IllegalArgumentException();
        }
    }

}
