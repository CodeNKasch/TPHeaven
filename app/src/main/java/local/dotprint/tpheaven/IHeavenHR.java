package local.dotprint.tpheaven;

public interface IHeavenHR {
    boolean Login(String username, String password);
    boolean Pause();
    boolean Start();
    HeavenHR.TrackingState Track();
    boolean GetWorkingTimes();
    boolean WorkingTimeSummery();
}
