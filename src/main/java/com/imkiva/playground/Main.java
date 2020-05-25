package com.imkiva.playground;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
     *
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

    public static void main(String[] args) throws Exception {
        var client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .proxy(ProxySelector.of(new InetSocketAddress("localhost", 10809)))
                .version(HttpClient.Version.HTTP_2)
                .build();
        var request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.youtube.com/watch?v=HbgzrKJvDRw"))
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36")
                .GET()
                .build();

        String htmlContent = client.send(request,
                HttpResponse.BodyHandlers.ofString()).body();

        var document = Jsoup.parse(htmlContent);
        System.out.println(document.body().toString());
    }
}
