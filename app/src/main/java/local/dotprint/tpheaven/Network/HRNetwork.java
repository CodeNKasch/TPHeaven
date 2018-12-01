package local.dotprint.tpheaven.Network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import local.dotprint.tpheaven.TPCookieJar;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HRNetwork {

    private OkHttpClient client;
    private TPCookieJar cookieJar;

    public HRNetwork() {
        cookieJar = new TPCookieJar();
        client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();
    }

    public Response Get(String url) throws IOException {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Cookie", CookieString())
                .get()
                .build();

        return client.newCall(request).execute();
    }

    public Response Post(String url, RequestBody body) throws IOException {
        Request request1 = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .post(body)
                .build();
        return client.newCall(request1).execute();
    }

    public boolean Login(String username, String password) {
        String content = "_username=" + URLEncode(username) + "&_password=" + URLEncode(password);
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, content);
        try {
            Response response = Post("https://www.heavenHR.com/login_check", body);
            boolean login1 = response.body().string().contains("Login");
            return response.code() == 500;
        } catch (IOException e) {
        }
        return false;
    }

    public String Authenticate() {
        String url = "https://api.heavenhr.com/api/v1/users/authenticate";
        try {
            Response response = Get(url);
            return response.body().string();
        } catch (IOException e) {
        }
        return "";
    }

    private String CookieString() throws UnsupportedEncodingException {
        String cookieString = "";
        for (Cookie cookie : cookieJar.loadForRequest(HttpUrl.parse(""))) {
            cookieString = cookieString + cookie.name() + "=" + URLEncode(cookie.value()) + ";";
        }
        return cookieString;
    }

    public String TogglePause(String JobID) {
        String url = "https://www.heavenhr.com/time-tracking/ajax/stopwatch/job/" + JobID + "/pause";
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Cookie", CookieString())
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return response.body().string();
            }
        } catch (Exception e) {
        }
        return "";
    }

    public String ToggleStartStop(String JobID) {
        String url = "https://www.heavenhr.com/time-tracking/ajax/stopwatch/job/" + JobID + "/toggle";
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Cookie", CookieString())
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return response.body().string();
            }
        } catch (Exception e) {
        }
        return "";
    }

    private String URLEncode(String raw) {
        try {
            return URLEncoder.encode(raw, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            return "";
        }
    }
}
