package Window;

public interface ProgramStateListener {
    void stateChanged(boolean stateChanged, String headerName);
    void renewState();
    void readBarCode(String barCode);
}
