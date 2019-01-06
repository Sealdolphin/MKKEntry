package Window;

import Control.EntryProfile;

public interface ProgramStateListener {
    void stateChanged(boolean stateChanged, String headerName);
    void renewState();
    void readBarCode(String barCode);
    void changeProfile(String profileName);
}
