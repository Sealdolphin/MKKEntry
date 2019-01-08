package Window;

import Control.EntryController;
import Control.EntryProfile;

/**
 * An interface handling the Program's behavior
 * @author Márk Mihalovits
 */
public interface ProgramStateListener {
    /**
     * An event for the Application's save state change
     * @param stateChanged the Application's save state (true if it's saved)
     * @param headerName the Application's header name
     */
    void stateChanged(boolean stateChanged, String headerName);

    /**
     * An event for (re)starting the Application and clearing all data
     */
    void renewState();

    /**
     * An event for incoming barcode for outer Barcode reader device
     * @param barCode the read data
     */
    void readBarCode(String barCode);

    /**
     * An event for profile change
     * @param profileName the new active profile
     */
    void changeProfile(String profileName);

    /**
     * Returns the current entryController
     * @return the Application's control class
     */
    EntryController getController();

    EntryProfile getProfile();
}
