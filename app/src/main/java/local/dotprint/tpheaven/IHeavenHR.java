package local.dotprint.tpheaven;

public interface IHeavenHR {
    boolean Login(String username, String password);
    void Pause();
    void Start();
    void Stop();
    StopWatchStatus GetStatus();
}
