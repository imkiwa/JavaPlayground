package com.imkiva.playground.toys;

import java.util.function.Supplier;

/**
 * @author kiva
 * @date 2020/1/9
 */
public final class LazySingleton<T> {
    private Supplier<T> supplier;
    private T instance;

    public LazySingleton(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T getInstance() {
        if (instance == null) {
            synchronized (this) {
                if (instance == null) {
                    instance = supplier.get();
                }
            }
        }
        return instance;
    }
}
