package local.dotprint.tpheaven;

import java.util.ArrayList;
import java.util.List;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class TPCookieJar implements CookieJar {

    private ArrayList<Cookie> mCookies = new ArrayList<>();

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
}
