package com.imkiva.playground;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author kiva
 * @date 2019-10-09
 */
public class Main {
    public static <A, B> Function<A, B> y(Function<Function<A, B>, Function<A, B>> ff) {
        return ff.apply(t -> y(ff).apply(t));
    }

    public static <T> Consumer<T> yCon(Function<Consumer<T>, Consumer<T>> ff) {
        return ff.apply(t -> yCon(ff).accept(t));
    }

    /**
     * A possible implementation of Task
     */
    static class Task implements Runnable {
        boolean canceled = false;

        void cancel() {
            canceled = true;
        }

        public boolean isCanceled() {
            return canceled;
        }

        @Override
        public void run() {
        }
    }

    /**
     * A possible implementation of runTask()
     * @param task
     * @param duration
     */
    private static void runTask(Consumer<Task> task, int duration) {
        Task t = new Task();
        while (!t.isCanceled()) {
            task.accept(t);

            try {
                Thread.sleep(duration);
            } catch (InterruptedException ignored) {
            }
        }
    }

    public static void main(String[] args) {
        runTask((task -> {
            Random random = new Random();

            // simulate real-world situation
            if (random.nextInt(100) > 30) {
                System.out.println("Task continue");
            } else {
                task.cancel();
            }
        }), 1000);

        runTask(yCon(taskConsumer -> task -> {
            Random random = new Random();

            // simulate real-world situation
            if (random.nextInt(100) > 30) {
                System.out.println("Task continue");
            } else {
                System.out.println("Task canceled");
                task.cancel();
            }
        }), 1000);
    }
}
