package com.imkiva.playground.toys;

/**
 * @author kiva
 * @date 2019-09-17
 */
public class PrivateClass {
    private PrivateClass() {}

    private void sayHello() {
        System.out.println("from PrivateClass: hello world");
    }
}
