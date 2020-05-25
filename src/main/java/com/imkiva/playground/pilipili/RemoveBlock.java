package com.imkiva.playground.pilipili;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RemoveBlock {
    public static void main(String[] args) throws IOException, InterruptedException {
        var http = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();

        var url = URI.create(
                "http://api.live.bilibili.com/banned_service/v1/Silent/del_room_block_user"
        );

        var data = "id=4783802&roomid=129215&csrf_token=33fbf4f5a30089e083098544411572e4&csrf=33fbf4f5a30089e083098544411572e4&visit_id=";

        var req = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(data))
                .uri(url)
                .header("content-type", "application/x-www-form-urlencoded")
                .header("cookie", "_uuid=96A78F21-0891-0FA8-3041-3C08EB5CCB2C54088infoc; buvid3=E7DD05B6-1031-46B9-9954-2382DEF78201155820infoc; sid=85u8j7gu; LIVE_BUVID=AUTO5415826877953287; CURRENT_FNVAL=16; rpdid=|(J~luRm|)u~0J'ul)kY)mmYR; im_notify_type_14540141=0; DedeUserID=14540141; DedeUserID__ckMd5=e65a6160e8b2ee9f; SESSDATA=85df706e%2C1600836791%2C4430d*31; bili_jct=33fbf4f5a30089e083098544411572e4; CURRENT_QUALITY=116; Hm_lvt_8a6e55dbd2870f0f5bc9194cddf32a02=1587742375,1587886384,1588085399,1588667363; bp_t_offset_14540141=385831360472862002; PVID=12; _dfcaptcha=94b46dcd9a7e22877a23028fdd2ca24d")
                .build();

        var res = http.send(req, HttpResponse.BodyHandlers.ofString()).body();
        System.out.println(res);
    }
}
