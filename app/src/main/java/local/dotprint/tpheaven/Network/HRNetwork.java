package local.dotprint.tpheaven.Network;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HRNetwork extends Network {

    public HRNetwork() {
        super();
    }

    /**
     * Login request for HeavenHR. Only successful if it the server returns a 500.
     *
     * @param username
     * @param password
     * @return boolean if login was successful
     */
    public boolean Login(String username, String password) {
        String url = "https://www.heavenHR.com/login_check";
        RequestBody body = RequestBody.create(
                MediaType.parse("application/x-www-form-urlencoded"),
                "_username=" + URLEncode(username) + "&_password=" + URLEncode(password)
        );
        try {
            Response response1 = Option(url);
            Response response = Post(url, body);
            return response.code() == 500;
        } catch (IOException e) {
        }
        return false;
    }


    /**
     * Authentication request, which is needed after @Login to authorize and get specific data about
     * the users capabilities
     *
     * @return Umapted Json string.
     */
    public String Authenticate() {
        String url = "https://api.heavenhr.com/api/v1/users/authenticate";
        try {
            Response response = Get(url);
            return response.body().string();
        } catch (IOException e) {
        }
        return "";
    }


    public String GetWorkingTimes(String JobID) {
        String url = "https://www.heavenhr.com/api/v1/workingtimes/overtime/saldo/summary/employee/" + JobID + "?endDate=2018-11-30";
        try {
            Response response = Get(url);
            return response.body().string();
        } catch (IOException e) {
        }
        return "";
    }

    /**
     * Sends a toggle Pause request to resume or pause the current tracking.
     * Empty body required.
     *
     * @param JobID
     * @return unparsed Json.
     */
    public String TogglePause(String JobID) {
        String url = "https://www.heavenhr.com/time-tracking/ajax/stopwatch/job/" + JobID + "/pause";
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), "");
        try {
            Response response = Post(url, body);
            if (response.code() == 200) {
                return response.body().string();
            }
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * Sends a toogle Start/Stop to initialize/stop the current tracking.
     * Empty body required.
     *
     * @param JobID
     * @return Unparsed json.
     */
    public String ToggleStartStop(String JobID) {
        String url = "https://www.heavenhr.com/time-tracking/ajax/stopwatch/job/" + JobID + "/toggle";
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), "");
        try {
            Response response = Post(url, body);
            if (response.code() == 200) {
                return response.body().string();
            }
        } catch (Exception e) {
            e.getCause();
        }
        return "";
    }

    /**
     * Get Information about the Tracking status.
     *
     * @param JobId
     * @return
     */
    public String TimeTracking(String JobId) {
        String url = "https://www.heavenhr.com/time-tracking/ajax/stopwatch/job/" + JobId + "/current";
        try {
            Response response = Get(url);
            if (response.code() == 200) {
                return response.body().string();
            }
        } catch (Exception e) {
            e.getCause();
        }
        return "";
    }


    public String WorkingTimeSummery(String JobId) {
        String url = "https://api.heavenhr.com/api/v1/workingtimes/summary/%s?startDate=%s&endDate=%s";
        String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        String uri = String.format(url, JobId, date,date);
        try {
            Response response = Get(uri);
            if (response.code() == 200) {
                return response.body().string();
            }
        } catch (Exception e) {
            e.getCause();
        }
        return "";
    }

    public String CheckSessionIsExpired(){
        String url = "https://api.heavenhr.com/api/v1/users/checkSessionIsExpired";
        try {
            Response response = Get(url);
            if (response.code() == 200) {
                return response.body().string();
            }
        } catch (Exception e) {
            e.getCause();
        }
        return "";
    }
}

/* GET https://www.heavenhr.com/api/v1/workingtimes/overtime/updates/?employeeJobId=<JOBID>&page=0&pageSize=25&sortBy=-compensationDate

GET 2x https://api.heavenhr.com/api/v1/users/checkSessionIsExpired

GET https://api.heavenhr.com/api/v1/workingtimes/summary/<JOBID>?startDate=2018-12-18&endDate=2018-12-18
https://api.heavenhr.com/api/v1/jobs/_LUvMkrT5ogdK5NWq6HQ5Lg_/workingtimes/?pageSize=25&jobId=_LUvMkrT5ogdK5NWq6HQ5Lg_&workingTimeDate=[*,*]
 */
