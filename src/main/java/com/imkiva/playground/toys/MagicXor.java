package com.imkiva.playground.toys;

import java.util.Scanner;

/**
 * @author kiva
 * @date 2019/12/2
 */
public class MagicXor {
    private static boolean verify(String s) {
        var ref = new Object() {
            int index = 0;
        };

        int x = s.chars()
                .limit(17)
                .map((c) -> (1 << (18 - ref.index++ - 1)) % 11 * (c - '0'))
                .reduce(0, Integer::sum);

        x = (12 - (x % 11)) % 11;
        return (x + '0') == s.charAt(17);
    }

    public static void main(String[] args) {
        String s = new Scanner(System.in).nextLine();
        if (verify(s)) {
            System.out.println("OK");
        } else {
            System.out.println("FUCK");
        }
    }
}
