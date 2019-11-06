package com.imkiva.playground.reflection;

import com.imkiva.playground.reflection.core.Reflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author kiva
 */
public class MirrorInvocationHandler<T> implements InvocationHandler {
    private Class<T> targetClass;
    private Reflect reflect;

    public MirrorInvocationHandler(Class<T> mirrorClass) {
        this.targetClass = mirrorClass;
        Mirror mirrorClassTag = targetClass.getAnnotation(Mirror.class);
        if (mirrorClassTag == null) {
            throw new IllegalArgumentException(mirrorClass.getName()
                    + " is not a mirror");
        }
        this.reflect = Reflect.of(mirrorClassTag.value());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println(method.getName());
        Constructor constructorTag = method.getAnnotation(Constructor.class);
        if (constructorTag != null) {
            this.reflect = reflect.instance(args);
        } else {
            throw new UnsupportedOperationException(targetClass.getName()
                    + "." + method.getName());
        }
        return null;
    }
}
