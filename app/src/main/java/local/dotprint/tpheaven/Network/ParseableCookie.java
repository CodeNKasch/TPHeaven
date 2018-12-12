package local.dotprint.tpheaven.Network;

import android.os.Parcel;
import android.os.Parcelable;
import okhttp3.Cookie;

public class ParseableCookie implements Parcelable {
    private Cookie mCookie;

    public ParseableCookie(Cookie cookie) {
        mCookie = cookie;
    }

    protected ParseableCookie(Parcel in) {

        String name = in.readString();
        String value = in.readString();
        long expiresAt = in.readLong();
        String domain = in.readString();
        String path = in.readString();
        boolean secure = in.readByte() != 0;
        boolean httpOnly = in.readByte() != 0;
        boolean hostOnly = in.readByte() != 0;
        Cookie.Builder builder = new Cookie.Builder();
        builder.name(name);
        builder.value(value);
        builder.expiresAt(expiresAt);
        builder.domain(domain);
        builder.path(path);
        if (secure) builder.secure();
        if (httpOnly) builder.httpOnly();
        if (hostOnly) builder.httpOnly();

        mCookie = builder.build();
    }

    public static final Creator<ParseableCookie> CREATOR = new Creator<ParseableCookie>() {
        @Override
        public ParseableCookie createFromParcel(Parcel in) {
            return new ParseableCookie(in);
        }

        @Override
        public ParseableCookie[] newArray(int size) {
            return new ParseableCookie[size];
        }
    };

    @Override
    public int describeContents() {
        return mCookie.hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mCookie.name());
        dest.writeString(mCookie.value());
        dest.writeLong(mCookie.expiresAt());
        dest.writeString(mCookie.domain());
        dest.writeString(mCookie.path());
        dest.writeByte((byte) (mCookie.secure() ? 1 : 0));
        dest.writeByte((byte) (mCookie.httpOnly() ? 1 : 0));
        dest.writeByte((byte) (mCookie.hostOnly() ? 1 : 0));
    }

    public Cookie cookie(){
        return mCookie;
    }
}
