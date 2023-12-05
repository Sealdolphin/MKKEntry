package view;

import control.utility.file.ExtensionFilter;
import data.entryprofile.EntryProfile;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static control.Application.uh;
import static javax.swing.JOptionPane.ERROR_MESSAGE;

public class StartupDialog {

    private EntryProfile activeProfile;

    public StartupDialog(List<EntryProfile> loadedProfiles) throws Exception{
        boolean menu = loadedProfiles.isEmpty();
        Object[] options = new Object[]{
                uh.getUIStr("UI","PROFILE_FROM_WIZARD"),
                uh.getUIStr("UI","PROFILE_FROM_JSON")
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
        chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

        //Open menu
        do{
            //Choose an option
            String menuResult = (String) JOptionPane.showInputDialog(
                    null,
                    uh.getUIStr("ERR","NO_PROFILE")+ "\nVálassz a lehetőségek közül.",
                    "Új profil létrehozása",
                    JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);

            if(menuResult == null) throw new Exception(uh.getUIStr("ERR","NO_PROFILE"));
            else if (menuResult.equals(options[0])){
                //Creating new wizard
                activeProfile = EntryProfile.createProfileFromWizard(null,null);
                if(activeProfile != null) {
                    loadedProfiles.add(activeProfile);
                    menu = false;
                }

            } else if (menuResult.equals(options[1])){
                //Open JSON file dialog
                int fileDialog = chooser.showOpenDialog(null);

                if(fileDialog == JFileChooser.APPROVE_OPTION){
                    File jsonProfiles = chooser.getSelectedFile();
                    System.out.println("File selected: " + jsonProfiles);
                    //Load profiles from Json
                    try {
                        JSONObject profileObj = (JSONObject) new JSONParser().parse(new BufferedReader(new InputStreamReader(new FileInputStream(jsonProfiles), StandardCharsets.UTF_8)));
                        EntryProfile.loadProfilesFromJson(profileObj,loadedProfiles);
                        String active = profileObj.get("active").toString();
                        activeProfile = loadedProfiles.stream().filter(profile -> profile.toString().equals(active)).findAny().orElse(null);
                        menu = false;
                    } catch (IOException | ParseException ex){
                        JOptionPane.showMessageDialog(null,
                                uh.getUIStr("ERR","IO_FAIL")+ "\n" + ex,
                                uh.getUIStr("ERR","HEADER"),ERROR_MESSAGE);
                    }
                }
            }

        } while (menu);
    }

    public EntryProfile getProfile() {
        return activeProfile;
    }
}
