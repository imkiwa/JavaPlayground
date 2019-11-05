package com.imkiva.playground.reflection.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author kiva
 */

public class Reflector {
    enum TargetType {
        /**
         * We are operating an {@link Object}, except {@link Class}
         */
        OBJECT,

        /**
         * We are operating an {@link Class}
         */
        CLASS
    }

    /**
     * Which object we are operating.
     */
    private TargetType targetType;

    /**
     * The reflection target.
     */
    private Object target;

    private Reflector(Object target) {
        this.target = target;
        this.targetType = TargetType.OBJECT;
    }

    private Reflector(Class clazz) {
        this.target = clazz;
        this.targetType = TargetType.CLASS;
    }

    /**
     * Wrap an object and return its reflector.
     * Helpful especially when you want to access instance fields and methods of any {@link Object}
     *
     * @param object The object to be wrapped
     * @return Reflector
     */
    public static Reflector of(Object object) {
        return new Reflector(object);
    }

    /**
     * Create reflector from given class type.
     * Helpful especially when you want to access static fields.
     *
     * @param clazz Given class type
     * @return Reflector
     */
    public static Reflector of(Class<?> clazz) {
        return new Reflector(clazz);
    }

    /**
     * Create reflector from class name.
     *
     * @param className Full class name
     * @return Reflector
     * @throws ReflectionException If any error occurs
     * @see #of(Class)
     */
    public static Reflector of(String className) {
        return of(className, Reflector.class.getClassLoader());
    }

    /**
     * Create reflector from class name using given class loader.
     *
     * @param className   Full class name
     * @param classLoader Given class loader
     * @return Reflector
     * @throws ReflectionException If any error occurs
     * @see #of(Class)
     */
    public static Reflector of(String className, ClassLoader classLoader) {
        return of(ReflectionHelper.forName(className, classLoader));
    }

    /**
     * Create an instance using its default constructor.
     *
     * @return Reflector to the return value of the method
     * @throws ReflectionException If any error occurs
     */
    public Reflector instance() throws ReflectionException {
        return instance(new Object[0]);
    }

    /**
     * Create an instance by parameters.
     *
     * @param args Parameters
     * @return Reflector to the return value of the method
     * @throws ReflectionException If any error occurs
     */
    public Reflector instance(Object... args) throws ReflectionException {
        Class<?> targetClass = getTargetClass();
        Class<?>[] parameterTypes = ReflectionHelper.convertParameterTypes(args);
        try {
            Constructor<?> constructor = ReflectionHelper.lookupConstructor(targetClass, parameterTypes);
            constructor = ReflectionHelper.makeAccessible(constructor);

            Object instance = constructor.newInstance(args);
            return of(instance);

        } catch (Exception e) {
            throw new ReflectionException(e);
        }
    }

    /**
     * Call a method by name without parameters.
     *
     * @param methodName Method name
     * @return Reflector to the return value of the method
     * @throws ReflectionException If any error occurs
     */
    public Reflector call(String methodName) throws ReflectionException {
        return call(methodName, new Object[0]);
    }

    /**
     * Call a method by name and parameters.
     *
     * @param methodName Method name
     * @param args       Parameters
     * @return Reflector to the return value of the method
     * @throws ReflectionException If any error occurs
     */
    public Reflector call(String methodName, Object... args) throws ReflectionException {
        Class<?> targetClass = getTargetClass();
        Class<?>[] parameterTypes = ReflectionHelper.convertParameterTypes(args);

        try {
            Method method = ReflectionHelper.lookupMethod(targetClass, methodName, parameterTypes);
            method = ReflectionHelper.makeAccessible(method);

            Class<?> returnType = method.getReturnType();

            if (ReflectionHelper.wrapType(returnType) == Void.class) {
                method.invoke(target, args);
                return of(targetClass);

            } else {
                Object result = method.invoke(target, args);
                return of(result);
            }

        } catch (Exception e) {
            throw new ReflectionException(e);
        }
    }

    /**
     * Set a field to given value.
     *
     * @param fieldName Field name
     * @param value     New value
     * @return Reflector
     * @throws ReflectionException If any error occurs
     */
    public Reflector set(String fieldName, Object value) throws ReflectionException {
        Field field = lookupField(fieldName);
        try {
            Object realValue = value;
            if (value instanceof Reflector) {
                realValue = ((Reflector) value).get();
            }

            field.set(target, realValue);
            return this;
        } catch (IllegalAccessException e) {
            throw new ReflectionException(e);
        }
    }

    /**
     * Get the value of given field
     *
     * @param fieldName Field name
     * @param <T>       The type of value
     * @return Value
     * @throws ReflectionException If any error occurs
     */
    public <T> T get(String fieldName) throws ReflectionException {
        return field(fieldName).get();
    }

    /**
     * Get field by name.
     *
     * @param fieldName Field name
     * @return {@link Field}
     * @throws ReflectionException If any error occurs
     */
    public Reflector field(String fieldName) throws ReflectionException {
        try {
            return of(lookupField(fieldName).get(target));

        } catch (IllegalAccessException e) {
            throw new ReflectionException(e);
        }
    }

    /**
     * Create a dynamic proxy based of the given type.
     * If we are maintaining a Map and error occurs when calling methods,
     * we will return value from Map as return value.
     * Helpful especially when creating default data handlers.
     *
     * @param fakingType The type we are faking
     * @return Faked Object
     */
    @SuppressWarnings("unchecked")
    public <F> F fake(Class<F> fakingType) {
        if (fakingType == null) {
            return null;
        }
        if (!fakingType.isInterface()) {
            throw new IllegalArgumentException("Only interfaces can be faked.");
        }

        return (F) Proxy.newProxyInstance(fakingType.getClassLoader(),
                new Class[]{fakingType}, new FakeInvocationHandler(target));
    }

    private Field lookupField(String fieldName) throws ReflectionException {
        Class<?> targetClass = getTargetClass();
        try {
            Field field = ReflectionHelper.lookupField(targetClass, fieldName);
            return ReflectionHelper.makeAccessible(field);

        } catch (Exception e) {
            throw new ReflectionException(e);
        }
    }

    /**
     * Get the real object that reflector operates.
     *
     * @param <T> The type of the real object.
     * @return The real object.
     */
    @SuppressWarnings("unchecked")
    public <T> T get() {
        return (T) target;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return target.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Reflector && target.equals(((Reflector) obj).get());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return target.toString();
    }

    /**
     * Get the class type of the real object that reflector operates.
     *
     * @see Object#getClass()
     */
    public Class<?> getTargetClass() {
        switch (targetType) {
            case CLASS:
                return (Class<?>) target;
            case OBJECT:
                return target.getClass();
        }
        throw new ReflectionException("target type is not supported");
    }
}
