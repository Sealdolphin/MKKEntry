package Control;

import Control.Utility.EntryFilter;
import Window.ProgramStateListener;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import java.awt.*;
import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static Control.Utility.EntryFilter.FilterType.TOMBOLA;
import static Control.Utility.EntryFilter.separator;


public class EventHandler {

    /**
     * The Defaults of the command strings
     */
    private static String[] defaults = {
            "entry_code","leave_code","delete_code","food_sale"
    };

    private boolean programState = false;
    private String saveFileName = "";
    private EntryController controller;

    private List<ProgramStateListener> listenerList = new ArrayList<>();

    private final JFileChooser fileDialog = new JFileChooser(System.getProperty("user.home"));

    EventHandler(EntryController c){
        controller = c;
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
            controller.exportList(resultFilter);
        }
    }

    public void renewState() {
        String question = "A munkád nincs még elmentve\n" +
                "és ha továbblépsz törlésre kerül.\n" +
                "Folytatod a műveletet?";
        if(programState && ConfirmAction(question) == JOptionPane.YES_OPTION){
            for (ProgramStateListener listener :
                    listenerList) {
                listener.renewState();
            }
        }
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
                List<Entry> imported = parseEntryImportFile(entryFile);
                controller.importEntries(imported);

            } catch (ParseException ex){
                JOptionPane.showMessageDialog(new JFrame(),
                        "Értelmezési hiba történt a fájl olvasása közben.\n" +
                        "Hiba pozicíója: " + ex.getErrorOffset() + "\n" +
                        "Hiba részletei:\n" + ex.getMessage(),
                        "Hiba", JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    private static List<Entry> parseEntryImportFile(File file) throws ParseException{
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
                importList.add(createEntryFromString(line,lineNumber));
            }

        } catch (FileNotFoundException fnf) {
            throw new ParseException("A fájl nem létezik.",lineNumber);
        } catch (IOException io) {
            throw new ParseException("Nem várt olvasási hiba:\n" + io.getMessage(),lineNumber);
        }

        if(importList.isEmpty()){
            JOptionPane.showMessageDialog(new JFrame(),"A megnyitott fájl üres","Figyelem",JOptionPane.WARNING_MESSAGE);
        }


        return importList;
    }

    /**
     * Creates a new Entry class from an input string
     * @param entryString the input string
     * @param offset the line where the input is found in a file
     * @return a new Entry with correct attributes
     * @throws ParseException if the parsing of the string fails
     */
    private static Entry createEntryFromString(String entryString, int offset) throws ParseException{
        String[] props = entryString.split(separator);
        String uid, name,enter = null ,leave = null;
        boolean entered = false;

        if(props.length < 1) throw new ParseException("A fájl sérült, vagy hibás",offset);
        try {
            uid = props[0];
        } catch (NumberFormatException format){
            throw new ParseException("Egyedi azonosító sérült, vagy érvénytelen",offset);
        }
        if(props.length > 1)
            name = props[1];
        else name = "Ismeretlen";

        if(props.length > 2) {
            enter = props[2];
            entered = true;
        }
        if(props.length > 3) {
            leave = props[3];
            entered = false;
        }

        return new Entry(uid,name,enter,leave,entered);
    }

    void changeState(boolean stateChanged) {
        programState = stateChanged;
        for (ProgramStateListener listener :
                listenerList) {
            listener.stateChanged(programState, saveFileName);
        }
    }

    /**
     * Adds a Programlistener to the listener list
     * @param l the new listener
     */
    void addListener(ProgramStateListener l){
        listenerList.add(l);
    }

    public void save(boolean saveAs) {
        changeState(false);
    }

    private static int ConfirmAction(String message){
        Object[] dialogOptions = {"Igen","Nem","Mégse"};
        return JOptionPane.showOptionDialog(
                new JFrame(),
                message,
                "Figyelem",
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
    void saveFile(String[] lines) {
        int result = openFileDialog(new JFrame(),"Fájl mentése","Mentés",true,null);
        if(result == JFileChooser.APPROVE_OPTION){
            File file = fileDialog.getSelectedFile();
            if(!file.getName().endsWith(".csv"))
                if (file.renameTo(new File(file + ".csv")))
                    JOptionPane.showMessageDialog(new JFrame(),"A fájlformátumot nem sikerült átírni","Figyelem",JOptionPane.WARNING_MESSAGE);

            try {
                PrintWriter fileWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file)));
                for (String line : lines) {
                    fileWriter.println(line);
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

    /**
     * Creates the default command words from an existing options.ini file
     * If the file does not exist, the creates one
     * If it cannot create the new options.ini file, then proceeds without creation
     * @return a HashMap with the different command words. For reference see defaults.
     */
    static HashMap<String,String> SetDefaultCommands(){
        BufferedReader optionsReader;
        HashMap<String,String> commands = new HashMap<>();
        try {
            optionsReader = new BufferedReader(new InputStreamReader(new FileInputStream("options.ini")));
            String line;
            while (true) {
                line = optionsReader.readLine();
                if(line == null) break;
                String[] strings = line.split(" = ");
                commands.put(strings[0],strings[1]);
            }

        } catch (FileNotFoundException ex) {
            commands.put(defaults[0],"MKK");
            commands.put(defaults[1],"GL");
            commands.put(defaults[2],"MOD");
            commands.put(defaults[3],"FOOD_SALE");
            JOptionPane.showMessageDialog(new JFrame(),"Az options.ini fájl nem található.\n" +
                    "Új fájl létrehozása alapméretezett paraméterekkel","Figyelem",JOptionPane.WARNING_MESSAGE);
            File optionsFile = new File("options.ini");
            try{
                PrintWriter optionsWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(optionsFile)));
                for (String commandString : defaults) {
                    optionsWriter.println(commandString + " = " + commands.get(commandString));
                }
            } catch (FileNotFoundException ex2) {
                JOptionPane.showMessageDialog(new JFrame(),"Az options.ini fájl nem hozható létre.\n" +
                        "A program nem volt képes inicializálni a változókat","Hiba",JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException io) {
            JOptionPane.showMessageDialog(new JFrame(),"Az options.ini fájl sérült.\n" +
                    "Fájlművelet közben hiba történt:\n" +
                    io.getMessage(),"Figyelem",JOptionPane.WARNING_MESSAGE);
        }
        return commands;
    }


}
