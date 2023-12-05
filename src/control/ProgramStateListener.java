package control;

import control.utility.file.EntryFilter;
import data.EntryProfile;
import data.entry.Entry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public interface ProgramStateListener {

    EntryFilter[] filterTypes = {
            new EntryFilter("Tombola", new int[]{0}),
            new EntryFilter("Alapméretezett", null)
    };

    /**
     * An event for profile change
     * @return the name of the new active profile
     */
    String changeProfile(EntryProfile newProfile, boolean restart);

    void exportList(PrintWriter writer, EntryFilter filter);

    void importList(BufferedReader reader, EntryFilter importFilter) throws IOException;

    void updateEntry(String id, Entry newEntry);

    void clearData();
}
