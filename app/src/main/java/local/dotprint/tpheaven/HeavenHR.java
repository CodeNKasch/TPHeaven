package local.dotprint.tpheaven;

import android.os.Parcel;
import android.os.Parcelable;

import local.dotprint.tpheaven.Network.HRNetwork;

public class HeavenHR implements IHeavenHR, Parcelable {

    private String HEAVEN_SESSION = "";
    private String CONSTANTS = "";

    public String UserData = "";

    private HRNetwork network;

    public HeavenHR() {
        network = new HRNetwork();
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
        boolean success = false;
        for (int i = 0; i < 2; i++) {
            success = network.Login(username, password);
            if (success)
                break;
        }
        if (success)
        {
            UserData = network.Authenticate();
            return true;
        }
        return false;
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




}
