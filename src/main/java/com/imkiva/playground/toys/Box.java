package com.imkiva.playground.toys;

import java.util.Random;

/**
 * @author kiva
 * @date 2019-05-01
 */
public class Box {
    enum Color {
        RED, BLUE
    }

    public static void main(String... args) {
        Random random = new Random();
        Color[][] boxes = new Color[][]{
                {Color.RED, Color.RED},                        // 红 红
                {Color.BLUE, Color.BLUE},                      // 蓝 蓝
                {Color.RED, Color.BLUE}                        // 红 蓝
        };

        int firstRed = 0;                                      // 第一次摸出的球红色
        int secondRed = 0;                                     // 剩下的球是红色

        for (int i = 0; i < 300000000; i++) {                  // 三亿次
            int boxId = random.nextInt(3);              // 随便选一个盒子
            int ballId = random.nextInt(2);             // 随便摸一个球

            if (boxes[boxId][ballId] == Color.RED) {           // 如果摸出的球是红色
                ++firstRed;
                if (boxes[boxId][1 - ballId] == Color.RED) {   // 如果盒子剩下的球是红色
                    ++secondRed;
                }
            }
        }

        System.out.println(1.0 * secondRed / firstRed);
    }
}
