package com.imkiva.playground.math;

import java.math.BigInteger;

public class Big {
    static BigInteger[] cache = new BigInteger[1001];

    static {
        cache[0] = new BigInteger("0");
        cache[1] = new BigInteger("1");
    }

    static BigInteger fib(int n) {
        if (cache[n] != null) {
            return cache[n];
        }
        cache[n] = fib(n - 1).add(fib(n - 2));
        return cache[n];
    }

    public static void main(String[] args) {
        System.out.println("hello world");
        System.out.println(fib(1000));
    }
}
