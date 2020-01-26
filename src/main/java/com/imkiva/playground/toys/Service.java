package com.imkiva.playground.toys;

/**
 * @author kiva
 * @date 2020/1/9
 */
public class Service {
    private static final LazySingleton<Service> sInstance = new LazySingleton<>(Service::new);

    private static Service get() {
        return sInstance.getInstance();
    }

    private String fuck() {
        return "";
    }

    private Service() {
        String love = fuck();

        new Thread(() -> {
            System.out.println(this.getClass().getName());
        }).start();
    }
}
