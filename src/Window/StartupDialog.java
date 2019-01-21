package Window;

import Control.EntryProfile;
import Control.Utility.ExtensionFilter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;

import java.io.*;
import java.util.List;

import static Control.Application.ui;
import static javax.swing.JOptionPane.ERROR_MESSAGE;

public class StartupDialog {

    private EntryProfile activeProfile;

    public StartupDialog(List<EntryProfile> loadedProfiles) throws Exception{
        boolean menu = loadedProfiles.isEmpty();
        Object[] options = new Object[]{
                ui.getUIStr("UI","PROFILE_FROM_WIZARD"),
                ui.getUIStr("UI","PROFILE_FROM_JSON")
        };
        //Create JSON filter
        ExtensionFilter jsonFilter = new ExtensionFilter(
                new String[]{"json","txt"},
                "Java Script Object Notation fájlok"
        );
        final JFileChooser chooser = new JFileChooser();
        chooser.addChoosableFileFilter(jsonFilter);
        chooser.setApproveButtonText("Megnyitás");
        chooser.setDialogTitle("Profilfájl betöltése");
        //Open menu
        do{
            //Choose an option
            String menuResult = (String) JOptionPane.showInputDialog(
                    null,
                    ui.getUIStr("ERR","NO_PROFILE")+ "\nVálassz a lehetőségek közül.",
                    "Új profil létrehozása",
                    JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);

            if(menuResult == null) throw new Exception(ui.getUIStr("ERR","NO_PROFILE"));
            else if (menuResult.equals(options[0])){
                //Creating new wizard
                String msg = ui.getUIStr("ERR","NOT_IMPLEMENTED");
                JOptionPane.showMessageDialog(null,msg,ui.getUIStr("ERR","HEADER"),ERROR_MESSAGE);
            } else if (menuResult.equals(options[1])){
                //Open JSON file dialog
                int fileDialog = chooser.showOpenDialog(null);

                if(fileDialog == JFileChooser.APPROVE_OPTION){
                    File jsonProfiles = chooser.getSelectedFile();
                    System.out.println("File selected: " + jsonProfiles);
                    JSONObject profileObj = null;
                    //Load profiles from Json
                    try {
                        profileObj = (JSONObject) new JSONParser().parse(new BufferedReader(new InputStreamReader(new FileInputStream(jsonProfiles))));
                        EntryProfile.loadProfilesFromJson(profileObj,loadedProfiles);
                        menu = false;
                    } catch (IOException | ParseException ex){
                        JOptionPane.showMessageDialog(null,
                                ui.getUIStr("ERR","IO_FAIL")+ "\n" + ex.toString(),
                                ui.getUIStr("ERR","HEADER"),ERROR_MESSAGE);
                    }
                }
            }

        } while (menu);
        activeProfile = loadedProfiles.get(0);
    }

    public EntryProfile getProfile() {
        return activeProfile;
    }
}
