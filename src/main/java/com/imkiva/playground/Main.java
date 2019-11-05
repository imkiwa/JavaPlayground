package com.imkiva.playground;

import com.imkiva.playground.reflection.core.Reflector;

/**
 * @author kiva
 * @date 2019-10-09
 */
public class Main {
    private void fuck() {
    }

    private static Object make(int base) {
        return new Object() {
            public int add(int x) {
                return x + base;
            }
        };
    }

    public static void main(String[] args) throws Throwable {
        var x = make(10);
        int a = Reflector.of(x).call("add", 1).get();
        System.out.println(a);
    }
}
