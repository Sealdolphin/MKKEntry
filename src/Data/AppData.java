package Data;

import Control.EntryProfile;
import Window.MainWindow;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AppData implements Serializable {
    /*
    FIELDS:
        LIST OF ENTRIES
        STATISTICS ON DEMAND
        PROGRAM'S ACTIVE STATE
    FUNCTIONS:
     */
    /**
     * The currently active profile
     */
    private EntryProfile activeProfile;

    /**
     * The list of the loaded profiles
     */
    private List<EntryProfile> profileList = new ArrayList<>();

    //private List<Entry> entries;

    /**
     * New option file creation with default settings
     */
    public AppData() {
        /*
        LOAD PROFILES FROM FILE...
        CREATE EVENT HANDLER
         */

//        try {
//            //Loading activeProfile
//            loadProfiles();
//        } catch (IOException | ParseException e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(new JFrame(), ui.getUIStr("ERR","PROFILE_LOAD_FAILED")+ "\n"+
//                    ui.getUIStr("ERR","PROFILE_JSON_MISSING") + "\n" + e.getMessage(), ui.getUIStr("ERR","HEADER"),JOptionPane.ERROR_MESSAGE);
//            System.exit(0);
//        }


        System.out.println("a new AppData has been constructed");
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

    public void updateView(MainWindow mainWindow) {

    }
}
