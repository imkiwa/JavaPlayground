package com.imkiva.playground.toys;

/**
 * @author kiva
 * @date 2019/12/2
 */
public class MagicXor {
    public static void main(String[] args) {
        int[] ints = new int[233];
        for (int i = 0; i < 233; i++) {
            ints[i] = i;
        }

        ints[222] = 0;

        int result = 0;
        for (int i = 0; i < ints.length; i++) {
            result ^= i ^ ints[i];
        }

        System.out.println(result);
    }
}
