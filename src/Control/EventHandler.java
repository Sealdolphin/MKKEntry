package Control;

import Control.EntryModifier.TicketType;
import Control.Utility.EntryFilter;
import Window.ProgramStateListener;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import java.awt.*;
import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static Control.Utility.EntryFilter.FilterType.TOMBOLA;
import static Control.Utility.EntryFilter.separator;
import static javax.swing.JOptionPane.*;
import static Window.Main.options;


public class EventHandler {

    private boolean programState = false;
    private String saveFileName = "";
    private String activeProfile;
    private Object[] profileNames;

    private ProgramStateListener listener;

    private final JFileChooser fileDialog = new JFileChooser(System.getProperty("user.home"));

    EventHandler(String profile, Object[] profiles){
        activeProfile = profile;
        profileNames = profiles;
    }

    public void exportEntries() {
        Object[] filters = EntryFilter.filterTypes;
        String resultFilter = (String)JOptionPane.showInputDialog(
                new JFrame(),
                "Válassz az exportálási lehetőségek közül:",
                "Exportálás",
                JOptionPane.QUESTION_MESSAGE,
                null,
                filters,
                filters[TOMBOLA.ordinal()]);

        if(resultFilter != null && resultFilter.length() > 0){
            //Let the export begin
            listener.getController().exportList(resultFilter);
        }
    }

    public void renewState() {
        if(!programState) {
            String question = "A munkád nincs még elmentve\n" +
                    "és ha továbblépsz törlésre kerül.\n" +
                    options.getUIStr("MSG","CONFIRM_ACTION");
            if (ConfirmAction(question) != YES_OPTION) return;
        }
        listener.renewState();
    }

    public void loadState() {

    }

    public void importEntries(){
        FileFilter[] filters = {new EntryFilter()};
        int choice = openFileDialog(new JFrame(),"Lista importálása","Import",false,filters);

        if(choice == JFileChooser.APPROVE_OPTION){
            File entryFile = fileDialog.getSelectedFile();
            System.out.println("Opening " + entryFile);

            try {
                List<Entry> imported = parseEntryImportFile(entryFile,listener.getProfile());
                //If parsing fails exception is thrown and the import is cancelled all together
                listener.getController().importEntries(imported);

            } catch (ParseException ex){
                JOptionPane.showMessageDialog(new JFrame(),
                        options.getUIStr("ERR","IMPORT_PARSE_FAIL") +"\n" +
                                options.getUIStr("ERR","POSITION") + ": " + ex.getErrorOffset() + "\n" +
                                options.getUIStr("ERR","DETAILS") + ":\n" + ex.getMessage(),
                        options.getUIStr("ERR","HEADER"), JOptionPane.ERROR_MESSAGE);
                System.out.println(ex.getMessage());
            }

        }
    }

    public void changeProfile(){
        Object[] profiles = profileNames;
        String result = (String)JOptionPane.showInputDialog(
                new JFrame(),
                "Válassz az profilok közül:",
                "Profil módosítása",
                JOptionPane.QUESTION_MESSAGE,
                null,
                profiles,
                activeProfile);
        String question = "Ha új profilra váltasz, " +
                "a jelenlegi munkádat nem folytathatod tovább.\n" +
                "Minden elmentetlen munkád elvész.\n" +
                options.getUIStr("MSG","CONFIRM_ACTION");
        if(result != null && ConfirmAction(question) == YES_OPTION) {
            listener.changeProfile(result);
        }
    }

    private static List<Entry> parseEntryImportFile(File file, EntryProfile profile) throws ParseException{
        List<Entry> importList = new ArrayList<>();
        int lineNumber = 0;
        try {
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

            boolean eof = false;
            while (!eof){
                lineNumber++;
                String line = fileReader.readLine();
                if(line == null) {
                    eof = true;
                    continue;
                }
                importList.add(createEntryFromString(line,profile,lineNumber));
            }

        } catch (FileNotFoundException fnf) {
            throw new ParseException(options.getUIStr("ERR","FILE_MISSING"),lineNumber);
        } catch (IOException io) {
            throw new ParseException(options.getUIStr("ERR","IO_FAIL") + ":\n" + io.getMessage(),lineNumber);
        }

        if(importList.isEmpty()){
            JOptionPane.showMessageDialog(new JFrame(),
                    options.getUIStr("ERR","FILE_EMPTY"),
                    options.getUIStr("MSG","WARNING"),JOptionPane.WARNING_MESSAGE);
        }


        return importList;
    }

    /**
     * Creates a new Entry class from an input string
     * The input string must follow the default Filter format which is:
     * 0: UID - required,
     * 1: NAME - required,
     * 2: TYPE_NAME - required,
     * 3: ENTRY_DATE - optional,
     * 4: LEAVE_DATE - optional
     * @param entryString the input string
     * @param profile the currently active profile
     * @param offset the line where the input is found in a file
     * @return a new Entry with correct attributes
     * @throws ParseException if the parsing of the string fails
     */
    private static Entry createEntryFromString(String entryString, EntryProfile profile, int offset) throws ParseException{
        String[] props = entryString.split(separator);
        String uid, name,enter = null ,leave = null;
        boolean entered = false;
        TicketType type;

        //Throw it if the array is empty
        if(props.length < 1) throw new ParseException("A rekord sérült, vagy hibás",offset);
        //Setting ID
        uid = props[0];

        //Looking for required fields
        //Alert if does not meet the number of fields required
        if(props.length < 3) {
            throw new ParseException("A rekordból hiányoznak argumentumok",offset);
            //TODO: needs implementing
            //fillDefault = fillOptionIsDefault(offset);
        }

        name = props[1];
        type = profile.identifyTicketType(props[2]);

        //Looking for optional fields
        //Setting ENTRY date (optional)
        if(props.length > 3) {
            enter = props[3];
            entered = true;
        }
        //Setting leave date (optional)
        if(props.length > 4) {
            leave = props[4];
            entered = false;
        }

        return new Entry(uid,type,name,enter,leave,entered);
    }

    void changeState(boolean stateChanged) {
        programState = stateChanged;
        listener.stateChanged(programState, saveFileName);
    }

    /**
     * Adds a Programlistener to the listener list
     * @param l the new listener
     */
    void setListener(ProgramStateListener l){
        listener = l;
    }

    public void save(boolean saveAs) {
        changeState(false);
    }

    private static int ConfirmAction(String message){
        Object[] dialogOptions = {"Igen","Nem","Mégse"};
        return JOptionPane.showOptionDialog(
                new JFrame(),
                message,
                options.getUIStr("MSG","WARNING"),
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                dialogOptions,
                dialogOptions[0]);
    }

    /**
     * Opens a file dialog, with which the user can interact.
     * @param parent the parent component
     * @param title the title of the dialog
     * @param approveBtn the text of the OK button
     * @param acceptAll whether All files(*.*) is an option for file filter
     * @param filters additional file filters
     * @return the result of the dialog
     */
    private int openFileDialog(Component parent, String title, String approveBtn, boolean acceptAll, FileFilter[] filters){
        fileDialog.resetChoosableFileFilters();
        if(filters != null)
            for (FileFilter filter :
                    filters) {
                fileDialog.addChoosableFileFilter(filter);
            }
        fileDialog.setAcceptAllFileFilterUsed(acceptAll);
        fileDialog.setDialogTitle(title);
        fileDialog.setApproveButtonText(approveBtn);

        return fileDialog.showOpenDialog(parent);
    }

    /**
     * Saves a file to a specific location
     * @param lines the lines of the file
     */
    void saveFile(Object[] lines) {
        int result = openFileDialog(new JFrame(),"Fájl mentése","Mentés",true,null);
        if(result == JFileChooser.APPROVE_OPTION){
            File file = fileDialog.getSelectedFile();
            //File file2 = null;
            if(!file.getName().contains(".")) {
                file = new File(file.getPath() + ".txt");
            }
            //JOptionPane.showMessageDialog(new JFrame(),"A fájlformátumot nem sikerült átírni","Figyelem",JOptionPane.WARNING_MESSAGE);

            try {
                PrintWriter fileWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file)));
                for (Object line : lines) {
                    fileWriter.println(line.toString());
                }
                fileWriter.close();

                JOptionPane.showMessageDialog(new JFrame(),"Lista exportálva","Kész",JOptionPane.INFORMATION_MESSAGE);

            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(new JFrame(),
                        "A fájl mentése nem sikerült\n"+
                                "Részletek: " + e.getMessage(),
                        "Hiba",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }


    /*
    private static boolean fillOptionIsDefault(int offset) throws ParseException {
        //Detecting anomaly: There are not enough required fields
        String message = "A rekord egyes argumentumai hiányoznak.\n" +
                "Hogyan szeretnéd őket kitölteni?";
        Object[] recordOptions = {"Alapméretezett értékekkel","Majd én kitöltöm őket"};
        //Request smart input from user
        Object r = JOptionPane.showInputDialog(new JFrame(),message,"Figyelem",WARNING_MESSAGE,null,recordOptions,recordOptions[0]);
        if(r == null){
            int res = JOptionPane.showConfirmDialog(new JFrame(),options.getUIStr("MSG","CANCEL_ACTION")+"\n"+
                    "Válassz igent, ha meg akarod szakítani az importálást","Figyelem",YES_NO_OPTION,WARNING_MESSAGE);
            if(res == YES_OPTION) throw new ParseException("Az importálás kézileg lett megszakítva",offset);
        } else return r.equals(recordOptions[0]);
        return true;
    }
    */

}
