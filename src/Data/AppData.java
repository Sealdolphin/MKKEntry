package Data;

import Control.EntryProfile;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AppData implements Serializable {
    /*
    FIELDS:
        LOADED PROFILES
        ACTIVE PROFILE
        LIST OF ENTRIES
        STATISTICS ON DEMAND
        PROGRAM'S ACTIVE STATE
    FUNCTIONS:
        SERIALIZATION (JSON, EXPORT, ELSE)
        DE-SERIALIZATION (JSON, IMPORT, ELSE)
     */
    /**
     * The currently active profile
     */
    private EntryProfile activeProfile;

    /**
     * The list of the loaded profiles
     */
    private List<EntryProfile> profileList = new ArrayList<>();

    /**
     * New option file creation with default settings
     */
    public AppData() {
        /*
        LOAD PROFILES FROM FILE...
        CREATE EVENT HANDLER
         */
        System.out.println("AppData constructed");
        activeProfile = new EntryProfile();
        profileList.add(activeProfile);
    }

    /*##################################Implemented for java.io.Serializable##########################################*/

    private void writeObject(java.io.ObjectOutputStream out) throws IOException{
        out.writeObject(activeProfile);
        out.writeObject(profileList);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException{
        activeProfile = (EntryProfile) in.readObject();
        profileList = new ArrayList<>();
        List profileListObject = (ArrayList) in.readObject();
        for (Object obj: profileListObject) {
            profileList.add((EntryProfile) obj);
        }
    }
}
