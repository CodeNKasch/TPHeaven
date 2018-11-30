package local.dotprint.tpheaven;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
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

    public String UserData = "";

    private TPCookieJar cookieJar;

    public HeavenHR() {
        cookieJar = new TPCookieJar();
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


        OkHttpClient client1 = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();
        try {
            String urlUsername = URLEncoder.encode(username, StandardCharsets.UTF_8.toString());
            String urlPassword = URLEncoder.encode(password, StandardCharsets.UTF_8.toString());
            String content = "_username=" + urlUsername + "&_password=" + urlPassword;

            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody body1 = RequestBody.create(mediaType, content);

            Request request1 = new Request.Builder()
                    .url(BASE_URL + "login_check")
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .post(body1)
                    .build();
            for (int i = 0; i < 2; i++) {

                Response response1 = client1.newCall(request1).execute();
                boolean login1 = response1.body().string().contains("Login");
            }
            return Authenticate();
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
                UserData = response.body().string();

                return true;
            }
        } catch (Exception e) {
        }
        return false;
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
}
