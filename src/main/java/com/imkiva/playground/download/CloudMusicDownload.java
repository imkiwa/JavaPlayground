package com.imkiva.playground.download;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * @author kiva
 * @date 2018/2/4
 */
public class CloudMusicDownload {
    private byte[] httpGetBytes(String url) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        InputStream is = conn.getInputStream();
        byte[] bytes = new byte[is.available()];
        is.read(bytes);
        return bytes;
    }

    private String httpGet(String url) throws Exception {
        return new String(httpGetBytes(url), StandardCharsets.UTF_8);
    }

    private int getMusicID(String musicName) throws Exception {
        String url = String.format(Locale.getDefault(),
                "http://localhost:3000/search?type=1&keywords=%s",
                URLEncoder.encode(musicName, "utf-8"));

        JsonObject jsonObject = new JsonParser().parse(httpGet(url)).getAsJsonObject();
        JsonArray resultArray = jsonObject.getAsJsonObject("result").getAsJsonArray("songs");
        if (resultArray.size() == 0) {
            return -1;
        }

        JsonElement element = resultArray.get(0);
        return element.getAsJsonObject().get("id").getAsInt();
    }

    private String getMusicURL(String musicName) throws Exception {
        int musicId = getMusicID(musicName);
        if (musicId <= 0) {
            return null;
        }

        String url = String.format(Locale.getDefault(),
                "http://localhost:3000/song/url?id=%d", musicId);
        return new JsonParser()
                .parse(httpGet(url))
                .getAsJsonObject()
                .getAsJsonArray("data")
                .get(0)
                .getAsJsonObject()
                .getAsJsonPrimitive("url")
                .getAsString();
    }

    private String getLyric(String musicName) throws Exception {
        int musicId = getMusicID(musicName);
        if (musicId <= 0) {
            return null;
        }

        String url = String.format(Locale.getDefault(),
                "http://localhost:3000/lyric?id=%d", musicId);

        return new JsonParser()
                .parse(httpGet(url))
                .getAsJsonObject()
                .getAsJsonObject("lrc")
                .getAsJsonPrimitive("lyric")
                .getAsString();
    }

    private void downloadLyric(String artist, String name, String lyric) throws Exception {
        String fileName = String.format(Locale.CHINA, "%s - %s.lrc", artist, name);
        File outFile = new File("/Users/kiva/Desktop/lyc/ok", fileName);

        try (FileOutputStream os = new FileOutputStream(outFile)) {
            os.write(lyric.getBytes(StandardCharsets.UTF_8));
            os.flush();
        }
    }

    private void downloadMusic(String url, String artist, String name) throws Exception {
        String fileName = String.format(Locale.CHINA, "%s - %s.mp3", artist, name);
        File outFile = new File("/Users/kiva/song/", fileName);

        var client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .version(HttpClient.Version.HTTP_2)
                .build();

        var request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofFile(outFile.toPath()));
    }

    private void start() throws Exception {
        BufferedReader reader = new BufferedReader(
                new FileReader("/Users/kiva/list.txt"));
        String line;
        while ((line = reader.readLine()) != null) {
            try {
                String[] array = line.split(" - ", 2);
                String artist = array[0];
                String name = array[1];
                System.out.println("下载歌曲: " + artist + " 的 " + name);
                String url = getMusicURL(artist + " " + name);
                if (url != null) {
                    downloadMusic(url, artist, name);
                    System.out.println("成功: " + url);
                }
            } catch (Throwable e) {
                e.printStackTrace();
                System.out.println("未找到歌曲");
            }
        }
    }

    public static void main(String... args) throws Exception {
        new CloudMusicDownload().start();
    }
}
