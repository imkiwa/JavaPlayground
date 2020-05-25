package com.imkiva.playground.download;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * @author kiva
 * @date 2019/12/31
 */
public class LiveStatus {
    enum Status {
        /**
         * Unable to obtain status.
         */
        UNKNOWN,

        /**
         * Live room is now streaming.
         */
        STREAMING,

        /**
         * Live room is now looping posted videos.
         */
        LOOPING,

        /**
         * Live room is closed.
         */
        CLOSED,
    }

    private static final String URL_ROOM_INIT =
            "https://api.live.bilibili.com/room/v1/Room/room_init?id=%d";

    private static final String URL_ROOM_INFO =
            "https://api.live.bilibili.com/xlive/web-room/v1/index/getInfoByRoom?room_id=%d";

    private static String roomInitUrl(int roomId) {
        return String.format(URL_ROOM_INIT, roomId);
    }

    private static String roomInfoUrl(int realId) {
        return String.format(URL_ROOM_INFO, realId);
    }

    private static Status obtainLiveStatus(int roomId) {
        var httpClient = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
                .uri(URI.create(roomInitUrl(roomId)))
                .GET()
                .build();

        try {
            String req = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString()).body();
            JsonObject root = new JsonParser().parse(req).getAsJsonObject();

            int status = root.getAsJsonObject("data")
                    .getAsJsonPrimitive("live_status")
                    .getAsInt();

            switch (status) {
                case 0:
                    return Status.CLOSED;
                case 1:
                    return Status.STREAMING;
                case 2:
                    return Status.LOOPING;
            }
        } catch (Throwable ignored) {
        }

        return Status.UNKNOWN;
    }

    public static void main(String[] args) {
        System.out.println(obtainLiveStatus(593));
    }
}
