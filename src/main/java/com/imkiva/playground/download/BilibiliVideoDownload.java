package com.imkiva.playground.download;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author kiva
 * @date 2018/8/6
 */
public class BilibiliVideoDownload {
    static class VideoID {
        String videoId;
        int videoPage;

        public VideoID(String videoId, int videoPage) {
            this.videoId = videoId;
            this.videoPage = videoPage;
        }

        @Override
        public String toString() {
            return "[VideoID] id: " + videoId + ", page: " + videoPage;
        }
    }

    static class SuperHttpConnection implements AutoCloseable {
        private HttpURLConnection connection;

        public SuperHttpConnection(HttpURLConnection connection) {
            this.connection = connection;
        }

        public HttpURLConnection getConnection() {
            return connection;
        }

        @Override
        public void close() {
            connection.disconnect();
        }
    }

    private static final String FILE_URL_PREFIX = "http://api.bilibili.com/playurl";
    private static final int QUALITY_NORMAL = 1;
    private static final int QUALITY_HIGH = 2;
    private static final int QUALITY_SUPER = 3;
    private static final int DEFAULT_QUALITY = QUALITY_SUPER;

    private static final HashMap<String, String> GET_VIDEO_URL_HEADERS = new HashMap<String, String>() {
        {
            this.put("Upgrade-Insecure-Requests", "1");
            this.put("Host", "api.bilibili.com");
            this.put("Cookie", "fts=1462458531; buvid3=ED2D2E75-9057-420C-97EB-B82831E7B26A39910infoc; rpdid=kwqpixwsmsdopmklskiiw");
        }
    };

    private static final Pattern AV_ID_PATTERN = Pattern.compile("/av(\\d*)");
    private static final Pattern AV_PAGE_PATTERN = Pattern.compile("/index_(\\d*)\\.html");

    private static VideoID getVideoId(String rawUrl) {
        Matcher idMatcher = AV_ID_PATTERN.matcher(rawUrl);
        if (!idMatcher.find()) {
            return null;
        }

        String avId = idMatcher.group(1);
        Matcher pageMatcher = AV_PAGE_PATTERN.matcher(rawUrl);
        int avPage = pageMatcher.find() ? Integer.parseInt(pageMatcher.group(1)) : 1;

        return new VideoID(avId, avPage);
    }

    private static String getVideoUrl(String videoInfoFileUrl, int quality) throws Exception {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(httpGet(videoInfoFileUrl, GET_VIDEO_URL_HEADERS))
                .getAsJsonObject();

        List<String> urlList = new ArrayList<>();

        JsonObject urls = jsonObject.getAsJsonArray("durl").get(0).getAsJsonObject();
        urlList.add(urls.get("url").getAsString());

        if (urls.has("backup_url")) {
            JsonArray backupUrls = urls.getAsJsonArray("backup_url");
            backupUrls.forEach(element -> urlList.add(element.getAsString()));
        }

        int qualityIndex = quality - 1;
        if (qualityIndex < urlList.size()) {
            return urlList.get(qualityIndex);
        }

        return urlList.get(0);
    }

    private static void download(String rawUrl, int quality, String outputFile) throws Exception {
        VideoID id = getVideoId(rawUrl);
        if (id == null) {
            throw new RuntimeException("video id is null");
        }

        String videoInfoUrl = String.format(Locale.ENGLISH,
                "%s?aid=%s&page=%d&vtype=hdmp4",
                FILE_URL_PREFIX, id.videoId, id.videoPage);
        String videoUrl = getVideoUrl(videoInfoUrl, quality);

        System.out.println("GOT video url: " + videoUrl);

        try (SuperHttpConnection holder = openConnection(videoUrl)) {
            HttpURLConnection connection = holder.getConnection();
            connection.setRequestMethod("GET");

            connection.setRequestProperty("Referer", "https://www.bilibili.com/");
            connection.setRequestProperty("Origin", "https://www.bilibili.com/");

            connection.connect();
            if (connection.getResponseCode() == 200) {
                InputStream inputStream = connection.getInputStream();
                byte[] buffer = new byte[2048];
                int read = 0;

                try (FileOutputStream os = new FileOutputStream(outputFile)) {
                    while ((read = inputStream.read(buffer)) > 0) {
                        os.write(buffer, 0, read);
                    }
                }
            }
        }
    }

    private static SuperHttpConnection openConnection(String url) throws Exception {
        return new SuperHttpConnection((HttpURLConnection) new URL(url).openConnection());
    }

    private static String httpGet(String url, HashMap<String, String> headers) throws Exception {
        try (SuperHttpConnection holder = openConnection(url)) {
            HttpURLConnection connection = holder.getConnection();
            connection.setRequestMethod("GET");

            if (headers != null) {
                headers.forEach(connection::setRequestProperty);
            }

            connection.connect();
            if (connection.getResponseCode() == 200) {
                InputStream is = connection.getInputStream();
                byte[] bytes = new byte[is.available()];
                if (is.read(bytes) > 0) {
                    return new String(bytes, StandardCharsets.UTF_8);
                }
            }
        }
        throw new RuntimeException("HTTP GET failed!");
    }

    public static void main(String[] args) throws Exception {
        download("https://www.bilibili.com/video/av65457052",
                DEFAULT_QUALITY,
                "/Users/kiva/Desktop/av65457052.mp4");
    }
}
