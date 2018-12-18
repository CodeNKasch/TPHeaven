package local.dotprint.tpheaven;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import local.dotprint.tpheaven.Network.HRNetwork;
import local.dotprint.tpheaven.Network.ParseableCookie;

public class HeavenHR implements IHeavenHR, Parcelable {

    private String JobId;
    private TrackingState Status;
    private String PersonId;
    private String UserId;
    private String trackingUserId;
    private String trackingCompanyId;

    public String UserData = "";

    private HRNetwork network;

    public HeavenHR() {
        network = new HRNetwork();
        Status = TrackingState.CLOSED;
    }

    protected HeavenHR(Parcel in) {
        network = new HRNetwork();
        UserData = in.readString();
        Status = TrackingState.valueOf(in.readString());
        network.SetParseableCookies(in.createTypedArray(ParseableCookie.CREATOR));
        JobId = GetJobId();
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
        boolean success = network.Login(username, password);
        if (success) {
            UserData = network.Authenticate();
            return true;
        }
        return false;
    }

    @Override
    public boolean Pause() {
        if (JobId != null && !JobId.trim().isEmpty()) {
            String body = network.TogglePause(JobId);
            try {
                JSONObject jobject = new JSONObject(body);
                JSONArray data = (JSONArray) jobject.get("data");
                Status = TrackingState.valueOf(data.getJSONObject(0).get("status").toString());
            } catch (Exception e) {

            }
            return true;
        } else
            return false;
    }

    @Override
    public boolean Start() {
        if (JobId != null && !JobId.trim().isEmpty()) {
            String body = network.ToggleStartStop(JobId);
            try {
                JSONObject jobject = new JSONObject(body);
                JSONArray data = (JSONArray) jobject.get("data");
                Status = TrackingState.valueOf(data.getJSONObject(0).get("status").toString());
            } catch (Exception e) {

            }
            return true;
        } else
            return false;
    }

    @Override
    public TrackingState Track() {
        if (JobId != null && !JobId.trim().isEmpty()) {
            String body = network.TimeTracking(JobId);
            try {
                JSONObject jobject = new JSONObject(body);
                JSONArray data = (JSONArray) jobject.get("data");
                Status = TrackingState.valueOf(data.getJSONObject(0).get("status").toString());

            } catch (Exception e) {
                return TrackingState.CLOSED;
            }

        }
        return Status;
    }

    @Override
    public boolean GetWorkingTimes() {
        if (JobId != null && !JobId.trim().isEmpty()) {
            String body = network.GetWorkingTimes(JobId);
            /*
            TODO find correlation
            saldo 13:55
            beantragt 0
            bewilligt 8:05
            gearbeitet 6:05
            abwesend  0:0
            soll 8:00
            freizeitausgleich 0:0
            {"links":[],"data":[
            {
            "saldo":1133,
            "absence":11520, / 12 / 60 (abwesend pro 30 tage)
            "actual":101004,
            "expected":111840,
            "updated":449,
            "compensated":0,
            "jobId":113127
            }]}
             */
            try {
                JSONObject jobject = new JSONObject(body);
                JSONArray data = (JSONArray) jobject.get("data");
                //Status = TrackingState.valueOf(data.getJSONObject(0).get("status").toString());
                return true;
            } catch (Exception e) {
            }
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(UserData);
        dest.writeString(Status.text);
        dest.writeTypedArray(network.GetParseableCookies(), 0);
    }

    public String GetJobId() {
        try {
            JSONObject jobject = new JSONObject(UserData);
            JSONArray data = (JSONArray) jobject.get("data");
            String jobId = data.getJSONObject(0).get("jobId").toString();
            return jobId;
        } catch (Exception e) {

        }
        return "";
    }

    public TrackingState Status() {
        return Status;
    }

    public enum TrackingState {
        CLOSED("CLOSED"),
        RUNNING("RUNNING"),
        PAUSED("PAUSED");

        private final String text;

        /**
         * @param text
         */
        TrackingState(final String text) {
            this.text = text;
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return text;
        }
    }
}
