package control;

import control.utility.EntryFilter;
import control.utility.TombolaFilter;

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

}
