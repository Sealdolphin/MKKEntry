package control;

import control.utility.BarcodeListener;
import control.utility.file.EntryFilter;
import data.EntryProfile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public interface ProgramStateListener extends BarcodeListener {

    EntryFilter[] filterTypes = {
            new EntryFilter("Tombola", new int[]{0})
    };

    void updateView();

    /**
     * An event for profile change
     * @return the name of the new active profile
     */
    String changeProfile(EntryProfile newProfile);

    void exportList(PrintWriter writer, EntryFilter filter);

    void importList(BufferedReader reader, EntryFilter importFilter) throws IOException;
}
