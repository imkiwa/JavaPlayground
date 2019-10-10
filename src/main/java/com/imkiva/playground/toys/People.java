package com.imkiva.playground.toys;

/**
 * @author kiva
 * @date 2019-07-15
 */
public class People {
    public static People findMySelf() {
        return new People();
    }

    public boolean nothingToDo() {
        return true;
    }

    public void learnComputerScience() {
        System.out.println("I love Computer Science");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
