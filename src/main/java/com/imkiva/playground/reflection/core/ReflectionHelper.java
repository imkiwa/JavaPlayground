package com.imkiva.playground.reflection.core;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author kiva
 */

public final class ReflectionHelper {
    /**
     * Get a class type of a class, which may cause its static-initialization
     *
     * @see Class#forName(String)
     */
    static Class<?> forName(String className, ClassLoader classLoader) {
        try {
            return Class.forName(className, true, classLoader);
        } catch (ClassNotFoundException e) {
            throw new ReflectionException(e);
        }
    }

    /**
     * Convert object arrays into elements' class type arrays.
     * If encountered {@code null}, use {@link Nothing}'s class type instead.
     *
     * @see Object#getClass()
     */
    static Class<?>[] convertParameterTypes(Object[] args) {
        if (args == null) {
            return new Class<?>[0];
        }

        Class<?>[] types = new Class<?>[args.length];
        for (int i = 0; i < args.length; ++i) {
            Object arg = args[i];
            types[i] = arg == null ? Nothing.class : arg.getClass();
        }
        return types;
    }

    /**
     * Wrap primitive class types into object class types.
     *
     * @param type Class type that may be primitive class type
     * @return Wrapped class type
     */
    public static Class<?> wrapType(Class<?> type) {
        if (type.isPrimitive()) {
            if (type == int.class) {
                return Integer.class;
            }
            if (type == void.class) {
                return Void.class;
            }
            if (type == char.class) {
                return Character.class;
            }
            if (type == long.class) {
                return Long.class;
            }
            if (type == double.class) {
                return Double.class;
            }
            if (type == float.class) {
                return Float.class;
            }
            if (type == byte.class) {
                return Byte.class;
            }
            if (type == short.class) {
                return Short.class;
            }
            if (type == boolean.class) {
                return Boolean.class;
            }
        }
        return type;
    }

    /**
     * Find a method that matches or mostly matches method name and parameter types.
     *
     * @param clazz          Class type
     * @param methodName     Method name
     * @param parameterTypes Method parameter types
     * @return Method
     * @throws NoSuchMethodException When method or similar method not found
     */
    static Method lookupMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes)
            throws NoSuchMethodException {
        try {
            return clazz.getMethod(methodName, parameterTypes);

        } catch (NoSuchMethodException e) {
            Class<?> walker = clazz;
            do {
                try {
                    return walker.getDeclaredMethod(methodName, parameterTypes);
                } catch (NoSuchMethodException ignored) {
                }

                walker = walker.getSuperclass();
            } while (walker != null);

            return lookupSimilarMethod(clazz, methodName, parameterTypes);
        }
    }

    private static Method lookupSimilarMethod(Class<?> clazz, String expectedMethodName,
                                              Class<?>[] expectedParameterTypes)
            throws NoSuchMethodException {
        Method found = lookupSimilarMethod(clazz.getMethods(), expectedMethodName, expectedParameterTypes);
        if (found != null) {
            return found;
        }

        do {
            found = lookupSimilarMethod(clazz.getDeclaredMethods(), expectedMethodName, expectedParameterTypes);
            if (found != null) {
                return found;
            }

            clazz = clazz.getSuperclass();
        } while (clazz != null);

        throw new NoSuchMethodException(expectedMethodName);
    }

    private static Method lookupSimilarMethod(Method[] candidates,
                                              String expectedMethodName,
                                              Class<?>[] expectedParameterTypes) {
        for (Method candidate : candidates) {
            if (isSimilarMethod(candidate, expectedMethodName, expectedParameterTypes)) {
                return candidate;
            }
        }
        return null;
    }

    private static boolean isSimilarMethod(Method candidate,
                                           String expectedMethodName,
                                           Class<?>[] expectedParameterTypes) {
        return candidate.getName().equals(expectedMethodName)
                && isSignatureAcceptable(candidate.getParameterTypes(), expectedParameterTypes);
    }

    /**
     * Find a constructor that matches parameter types
     *
     * @param clazz          Class type
     * @param parameterTypes Parameter types
     * @return Constructor
     * @throws NoSuchMethodException When constructor not found
     */
    static Constructor<?> lookupConstructor(Class<?> clazz, Class<?>[] parameterTypes)
            throws NoSuchMethodException {
        try {
            return clazz.getDeclaredConstructor(parameterTypes);

        } catch (NoSuchMethodException e) {
            return lookupSimilarConstructor(clazz, parameterTypes);
        }
    }

    private static Constructor<?> lookupSimilarConstructor(Class<?> clazz, Class<?>[] parameterTypes)
            throws NoSuchMethodException {
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (isSignatureAcceptable(constructor.getParameterTypes(), parameterTypes)) {
                return constructor;
            }
        }
        throw new NoSuchMethodException("constructor for " + clazz.getName());
    }

    /**
     * Find a field that matches the name
     *
     * @param clazz     Class type
     * @param fieldName Field name
     * @return Field
     * @throws NoSuchFieldException When field not found
     */
    static Field lookupField(Class<?> clazz, String fieldName)
            throws NoSuchFieldException {
        try {
            return clazz.getField(fieldName);

        } catch (NoSuchFieldException e) {
            Class<?> walker = clazz;
            do {
                try {
                    return walker.getDeclaredField(fieldName);
                } catch (NoSuchFieldException ignored) {
                }

                walker = walker.getSuperclass();
            } while (walker != null);

            throw e;
        }
    }

    private static boolean isSignatureAcceptable(Class<?>[] expected, Class<?>[] given) {
        if (expected.length != given.length) {
            return false;
        }

        for (int i = 0; i < given.length; i++) {
            if (!isSignatureAcceptable(expected[i], given[i])) {
                return false;
            }
        }

        return true;
    }

    private static boolean isSignatureAcceptable(Class<?> expected, Class<?> given) {
        return given == Nothing.class ||
                wrapType(expected).isAssignableFrom(wrapType(given));
    }

    /**
     * Make an {@link AccessibleObject} accessible.
     *
     * @param object The object to be made accessible
     * @param <T>    Type of the object
     * @return Accessible object
     */
    @SuppressWarnings("unchecked")
    public static <T extends AccessibleObject> T makeAccessible(T object) {
        if (object == null) {
            return null;
        }

        if (object instanceof Member) {
            Member member = (Member) object;

            if (Modifier.isPublic(member.getModifiers()) &&
                    Modifier.isPublic(member.getDeclaringClass().getModifiers())) {

                return object;
            }
        }

        if (!object.isAccessible()) {
            object.setAccessible(true);
        }

        return object;
    }
}
