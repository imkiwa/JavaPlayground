package com.imkiva.playground.toys;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author kiva
 * @date 2019-09-17
 */
public class Reflection {
    public static void main(String[] args) throws Exception {
        Class<?> klass = Class.forName("com.imkiva.playground.toys.PrivateClass");
        Constructor ctor = klass.getDeclaredConstructor();
        ctor.setAccessible(true);
        Object object = ctor.newInstance();
        Method method = klass.getDeclaredMethod("sayHello");
        method.setAccessible(true);
        method.invoke(object);
    }
}
