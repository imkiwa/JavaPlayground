package com.imkiva.playground.toys;

import java.util.Arrays;

/**
 * @author kiva
 * @date 2018/9/30
 */
public class XX {
    public static void main(String[] args) throws Exception {
        String s = "amF2YS5pby5GaWxl";
        String name = new String(java.util.Base64.getDecoder().decode(s));
        Class cls = Class.forName(name);
        java.lang.reflect.Constructor ctor = cls.getConstructor(new Class[]{String.class});
        Object o = ctor.newInstance("/sdcard/imkiva.txt");
        cls.getDeclaredMethod("createNewFile").invoke(o);
    }

    public static boolean nothingToDo() {
        return true;
    }
}
