package com.imkiva.playground.toys;

/**
 * @author kiva
 * @date 2019-07-15
 */

public class SummerHoliday {
    public static void main(String[] args) {
        // Find myself
        People me = People.findMySelf();

        // Check if I am felling bored
        while (me.nothingToDo()) {
            // Yes!
            // Let's learn Computer Science
            // Together!
            me.learnComputerScience();
        }
    }
}
