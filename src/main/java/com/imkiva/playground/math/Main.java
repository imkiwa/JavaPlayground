package com.imkiva.playground.math;

/**
 * @author kiva
 * @date 2019-09-09
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        Matrix lhs = new Matrix(new double[][]{
                {3, 4, 1, 0},
                {4, -3, 0, 1},
                {0, 0, 2, 0},
                {0, 0, 2, 2},
        });

        Matrix square = lhs.multiply(lhs);
        Matrix result = square.multiply(square);

        System.out.println(result);
    }
}
