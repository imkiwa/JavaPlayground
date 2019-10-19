package com.imkiva.playground.luck;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author kiva
 * @date 2019/10/19
 */
public class FindLuckyDog {
    private static final String LUCKY_URL = "http://bbs.covariant.cn/d/11";

    public static void main(String[] args) {
        new FindLuckyDog().find();
    }

    private void find() {
        var candidates = obtainCandidates(LUCKY_URL);
        var luckyNumber = rollNumber(candidates.size());

        System.out.println(":: All Candidate:");
        candidates.forEach(System.out::println);

        System.out.println();
        System.out.println(":: The Lucky Dog:");
        System.out.println(candidates.get(luckyNumber));
    }

    private int rollNumber(int size) {
        var random = new Random();
        int randTimes = random.nextInt(size);
        int loopTimes = 2 * randTimes + random.nextInt(size);

        // Discard some results according to candidate count
        for (int i = 0; i < loopTimes; i++) {
            random.nextInt(size);
        }
        return random.nextInt(size);
    }

    /**
     * Obtain the candidate list from forum page.
     * @param url The URL of forum page.
     * @return The candidate list.
     */
    private List<Candidate> obtainCandidates(String url) {
        var client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .version(HttpClient.Version.HTTP_2)
                .build();
        var request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            String content = client.send(request,
                    HttpResponse.BodyHandlers.ofString()).body();
            return parseCandidates(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return List.of();
    }

    /**
     * Extract all valid candidates from HTML.
     * @param htmlContent HTML content
     * @return List of validated candidates
     */
    private List<Candidate> parseCandidates(String htmlContent) {
        var document = Jsoup.parse(htmlContent);
        var posts = document.body()
                .select("div[id=app]")
                .select("main[class=App-content]")
                .select("noscript")
                .select("div[class=container]")
                .select("div")
                .next() // skip "使用更先进的浏览器访问效果更佳。"
                .next() // skip "两种代码，你偏向哪种？（有奖）"
                .select("div");

        return posts.stream()
                .map(post -> new Candidate(obtainUsername(post), obtainReason(post)))
                .filter(this::filterValidCandidate)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Obtain username from HTML element.
     * @param element HTML element containing a post
     * @return Username text.
     */
    private String obtainUsername(Element element) {
        return element.select("h3").text();
    }

    /**
     * Obtain reason from HTML element.
     * @param element HTML element containing a post
     * @return Reply plain text
     */
    private String obtainReason(Element element) {
        return element.select("div[class=Post-body]").text();
    }

    /**
     * Check whether the candidate is validated.
     * Note: Sponsors(mikecovlee and imkiva) do not participate in this draw.
     * @param candidate The candidate to check
     * @return true if validated
     */
    private boolean filterValidCandidate(Candidate candidate) {
        return !candidate.getUserName().isEmpty()
                && !candidate.getReason().isBlank()
                && candidate.isNot("mikecovlee")
                && candidate.isNot("imkiva");
    }

    private static class Candidate {
        private String userName;
        private String reason;

        Candidate(String userName, String reason) {
            this.userName = userName;
            this.reason = reason;
        }

        String getUserName() {
            return userName;
        }

        String getReason() {
            return reason;
        }

        boolean isNot(String userName) {
            return !Objects.equals(userName, getUserName());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Candidate candidate = (Candidate) o;

            // We only compare usernames because we need to use the distinct method
            // in the candidate stream which is totally determined by equals().
            return Objects.equals(getUserName(), candidate.getUserName());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getUserName());
        }

        @Override
        public String toString() {
            return String.format("[%s]: %s", getUserName(), getReason());
        }
    }
}
