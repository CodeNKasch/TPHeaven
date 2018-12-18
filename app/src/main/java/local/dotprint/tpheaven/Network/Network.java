package local.dotprint.tpheaven.Network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Network {

    private OkHttpClient client;
    private TPCookieJar cookieJar;

    public Network(){
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
                .addHeader("Cookie", CookieString())
                .post(body)
                .build();
        return client.newCall(request1).execute();
    }

    public Response Option(String url) throws IOException
    {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Cookie", CookieString())
                .method("OPTION",null)
                .build();
        return client.newCall(request).execute();
    }

    public String CookieString() throws UnsupportedEncodingException {
        String cookieString = "";
        for (Cookie cookie : GetCookies()) {
            cookieString = cookieString + cookie.name() + "=" + URLEncode(cookie.value()) + ";";
        }
        return cookieString;
    }

    public List<Cookie> GetCookies(){
        return cookieJar.loadForRequest(null);
    }

    public void SetCookies(List<Cookie> cookies){
        cookieJar.saveFromResponse(null ,cookies);
    }

    public ParseableCookie[] GetParseableCookies(){
        List<Cookie> cookies = GetCookies();
        ParseableCookie[] parseableCookies = new ParseableCookie[cookies.size()];
        for (Cookie cookie : cookies) {
            parseableCookies[cookies.indexOf(cookie)] = new ParseableCookie(cookie);
        }
        return parseableCookies;
    }

    public void SetParseableCookies(ParseableCookie[] parseableCookies){
        List<Cookie> cookies = new ArrayList<>();
        for (ParseableCookie parcel : parseableCookies) {
            cookies.add(parcel.cookie());
        }
        SetCookies(cookies);
    }

    public String URLEncode(String raw) {
        try {
            return URLEncoder.encode(raw, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            return "";
        }
    }
}
