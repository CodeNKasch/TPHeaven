package local.dotprint.tpheaven;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import local.dotprint.tpheaven.Network.HRNetwork;
import local.dotprint.tpheaven.Network.ParseableCookie;

public class HeavenHR implements Parcelable {

    private String JobId;
    private TrackingState Status;
    private String PersonId;
    private String UserId;
    private String trackingUserId;
    private String trackingCompanyId;

    public int total = 0;
    public int approved = 0;
    public int requested = 0;

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

    public boolean Login(String username, String password) {
        boolean success = network.Login(username, password);
        if (success) {
            UserData = network.Authenticate();
            return true;
        }
        return false;
    }

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
                String total  = data.getJSONObject(0).get("total").toString();
                this.total = Integer.parseInt(total);
                return true;
            } catch (Exception e) {
            }
        }
        return false;
    }

    public boolean WorkingTimeSummery()
    {
        if (JobId != null && !JobId.trim().isEmpty()) {
            String body = network.WorkingTimeSummery(JobId);

            try {
                // no task running /*"data":[{"status":"APPROVED","total":485},{"status":"REQUESTED","total":0}]*/
                JSONObject jobject = new JSONObject(body);
                JSONArray data = (JSONArray) jobject.get("data");
                String status1 = data.getJSONObject(0).get("status").toString();
                approved = data.getJSONObject(0).getInt("total");
                String status2 = data.getJSONObject(1).get("status").toString();
                requested = data.getJSONObject(1).getInt("total");
                return true;
            } catch (Exception e) {
            }
        }
        return false;
    }

    public boolean CheckSessionIsExpired()
    {
        try {/*{"links":[],"data":[{"isSessionExpired":false}]}*/
            String body = network.CheckSessionIsExpired();
            JSONObject jobject = new JSONObject(body);
            JSONArray data = (JSONArray) jobject.get("data");
            Boolean status1 = data.getJSONObject(0).getBoolean("isSessionExpired");
            return status1;
        }
        catch (Exception e){}
        return true;
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
