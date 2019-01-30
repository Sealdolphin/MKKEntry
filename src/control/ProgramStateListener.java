package control;

import control.utility.file.EntryFilter;
import control.utility.file.TombolaFilter;

import javax.swing.filechooser.FileFilter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public interface ProgramStateListener {

    EntryFilter[] filterTypes = {
            new TombolaFilter()
    };

    void updateView();

    /**
     * An event for incoming barcode for outer Barcode reader device
     * @param barCode the read data
     */
    void readBarCode(String barCode);

    /**
     * An event for profile change
     * @return the name of the new active profile
     */
    String changeProfile();

    void exportList(PrintWriter writer, EntryFilter filter);

    void importList(BufferedReader reader, EntryFilter importFilter) throws IOException;
}
