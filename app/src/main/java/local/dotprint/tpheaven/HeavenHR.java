package local.dotprint.tpheaven;

import android.os.Parcel;
import android.os.Parcelable;
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

public class HeavenHR implements IHeavenHR,Parcelable {

    private String HEAVEN_SESSION = "";
    private String CONSTANTS = "";

    private List<Cookie> mCookies = new ArrayList<>();

    public HeavenHR(){

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
                        mCookies = cookies;
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        return mCookies;
                    }
                })
                .build();


        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

        RequestBody body = RequestBody.create(mediaType,"_username="+username+"&_password="+password);
        Request request = new Request.Builder()
                .url("https://www.heavenhr.com/login_check")
                .post(body)
                .addHeader("Host", "www.heavenhr.com")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("cache-control", "no-cache")
                .build();
        try {
            Response response = client
                    .newCall(request)
                    .execute();
            if (response.code() == 200 )
                return Authenticate();
            return false;
        }
        catch (Exception e)
        {
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

    private boolean Authenticate()
    {
        String cookieString = "";
        for (Cookie cookie : mCookies) {
           cookieString =cookieString +  cookie.name()+"="+cookie.value()+ ";";
        }

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.heavenhr.com/api/v1/users/authenticate")
                .get()
                .addHeader("Cookie", cookieString)
                .addHeader("cache-control", "no-cache")
                .addHeader("Postman-Token", "9f737c7a-3e71-4dbc-9df7-fb2ad35ca147")
                .build();

        try {
            Response response = client.newCall(request).execute();
            return response.code() == 200;
        }
        catch (Exception e)
        {}
        return false;
    }
}
