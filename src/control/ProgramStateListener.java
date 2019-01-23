package control;

public interface ProgramStateListener {
//    /**
//     * An event for the Application's save state change
//     * @param stateChanged the Application's save state (true if it's saved)
//     * @param headerName the Application's header name
//     */
//    void stateChanged(boolean stateChanged, String headerName);
//

    void updateView();

    /**
     * An event for incoming barcode for outer Barcode reader device
     * @param barCode the read data
     */
    void readBarCode(String barCode);
//
//    /**
//     * An event for profile change
//     * @param profileName the new active profile
//     */
//    void changeProfile(String profileName);
//
//    /**
//     * Returns the current entryController
//     * @return the Application's control class
//     */
//    EntryController getController();
//
//    EntryProfile getProfile();
}
