package local.dotprint.tpheaven;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.locks.ReentrantLock;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class TPCookieJar implements CookieJar {

    private ReentrantLock  lock = new ReentrantLock();

    private ArrayList<Cookie> mCookies = new ArrayList<>();

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies)
    {
        lock.lock();
        for(Cookie cookie : cookies)
        {
            int position = Contains(mCookies,cookie);
            if (position < 0) {
                mCookies.add(cookie);
            }
            else {
                mCookies.remove(position);
                mCookies.add(cookie);
            }

        }
        lock.unlock();
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        lock.lock();
        ArrayList<Cookie> result = mCookies;
        lock.unlock();
        return result;
    }

    private int Contains (List<Cookie> cookies, Cookie cookie)
    {
        for (Cookie listItem : cookies)
        {
            if(listItem.name().equals(cookie.name()))
                return cookies.indexOf(listItem);
        }
        return -1;
    }
}
