package local.dotprint.tpheaven;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HeavenHR implements IHeavenHR, Parcelable {

    private String HEAVEN_SESSION = "";
    private String CONSTANTS = "";

    private static final String BASE_URL = "https://www.heavenhr.com/";
    private static final String HOST = "www.heavenhr.com";

    private ArrayList<Cookie> mCookies = new ArrayList<>();

    public HeavenHR() {

    }

    protected HeavenHR(Parcel in) {

    }

    public static final Creator<HeavenHR> CREATOR = new Creator<HeavenHR>() {
        @Override
        public HeavenHR createFromParcel(Parcel in) {
            return new HeavenHR(in);
        }

        @Override
        public HeavenHR[] newArray(int size) {
            return new HeavenHR[size];
        }
    };

    @Override
    public boolean Login(String username, String password) {
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        for (Cookie cookie : cookies) {
                            if (!mCookies.contains(cookies))
                                mCookies.add(cookie);
                        }
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        return mCookies;
                    }
                })
                .build();

        try {
            String urlUsername = URLEncoder.encode(username, StandardCharsets.UTF_8.toString());
            String urlPassword = URLEncoder.encode(password, StandardCharsets.UTF_8.toString());
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            String content = "_username=" + urlUsername + "&_password=" + urlPassword+"&signIn=&_target_path=login_entry";

            RequestBody body = RequestBody.create(mediaType, content);
            Request request = new Request.Builder()
                    .url(BASE_URL + "login_check")
                    .addHeader("Host","www.heavenhr.com")
                    .addHeader("Accept"," text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .addHeader("Accept-Encoding","deflate, br")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("cache-control", "no-cache")
                    .post(body)
                    .build();

            Response response = client
                    .newCall(request)
                    .execute();
            String responseBody = response.body().string();
            if (response.code() == 200)
                return Authenticate();
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void Pause() {

    }

    @Override
    public void Start() {

    }

    @Override
    public void Stop() {

    }

    @Override
    public StopWatchStatus GetStatus() {
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO : save object
    }

    private boolean Authenticate() {
        String cookieString = "";
        for (Cookie cookie : mCookies) {
            cookieString = cookieString + cookie.name() + "=" + cookie.value() + ";";
        }

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(BASE_URL + "api/v1/users/authenticate")
                .addHeader("Host","api.heavenhr.com")
                .addHeader("Accept","application/json, text/plain, */*")
                .addHeader("Accept-Encoding","deflate, br")
                .addHeader("Cookie", cookieString)
                .addHeader("cache-control", "no-cache")
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();
            return response.code() == 200;
        } catch (Exception e) {
        }
        return false;
    }
}
