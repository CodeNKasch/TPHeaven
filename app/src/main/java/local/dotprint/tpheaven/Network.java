package local.dotprint.tpheaven;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Network {

    private OkHttpClient client;
    private TPCookieJar cookieJar;

    public Network() {
        cookieJar = new TPCookieJar();
        client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();
    }

    public boolean Login(String username, String password) {
        try {

            String content =
                    "_username=" + URLEncode(username) + "&_password=" + URLEncode(password);

            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody body1 = RequestBody.create(mediaType, content);

            Request request1 = new Request.Builder()
                    .url("https://www.heavenHR.com/login_check")
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .post(body1)
                    .build();
            Response response1 = client.newCall(request1).execute();
            boolean login1 = response1.body().string().contains("Login");
            return response1.code() == 500;
        } catch (Exception e) {
            return false;
        }
    }

    public String Authenticate() {
        String url = "https://api.heavenhr.com/api/v1/users/authenticate";


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

    private String CookieString() throws UnsupportedEncodingException {
        String cookieString = "";
        for (Cookie cookie : cookieJar.loadForRequest(HttpUrl.parse(""))) {
            cookieString = cookieString + cookie.name() + "=" +
                    URLEncoder.encode(cookie.value(), StandardCharsets.UTF_8.toString())
                    + ";";
        }
        return cookieString;
    }

    private String URLEncode(String raw) {
        try {
            return URLEncoder.encode(raw, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            return "";
        }
    }
}
