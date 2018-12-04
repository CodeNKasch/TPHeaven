package local.dotprint.tpheaven;

public interface IHeavenHR {
    boolean Login(String username, String password);
    boolean Pause();
    boolean Start();

    StopWatchStatus GetStatus();
}
