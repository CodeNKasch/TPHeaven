package local.dotprint.tpheaven;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import local.dotprint.tpheaven.Network.HRNetwork;
import local.dotprint.tpheaven.Network.ParseableCookie;

public class HeavenHR implements Parcelable {

    private String JobId;
    private TrackingState Status;
    private String PersonId;
    private String UserId;
    private String trackingUserId;
    private String trackingCompanyId;

    public long current = 0;
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

    public boolean GetCurrentTime()
    {
        if (JobId != null && !JobId.trim().isEmpty()) {
            String body = network.GetCurrentTime(JobId);
            try {
                JSONObject jobject = new JSONObject(body);
                JSONArray data = (JSONArray) jobject.get("data");

                String start  = data.getJSONObject(0).get("start").toString().replace("T"," ").replace("Z","");
                String currentTime  = data.getJSONObject(0).get("currentTime").toString().replace("T"," ").replace("Z","");;

                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); // first example
                long d1 = format1.parse( start ).getTime();
                long d2 = format1.parse( currentTime).getTime();
                current = d2 - d1;
                return true;
            } catch (Exception e) {
                Log.e(this.getClass().getCanonicalName(), e.getMessage());
            }
        }
        return false;
    }

    public boolean GetWorkingTimes() {
        if (JobId != null && !JobId.trim().isEmpty()) {
            String body = network.GetWorkingTimes(JobId);
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
