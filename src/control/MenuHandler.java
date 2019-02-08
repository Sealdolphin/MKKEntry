package control;

import control.utility.file.EntryFilter;

import javax.swing.*;
import java.io.*;

import static control.Application.uh;
import static javax.swing.JOptionPane.ERROR_MESSAGE;

//
//import Control.TicketModifier.TicketType;
//import Control.Utility.EntryFilter;
//import Window.ProgramStateListener;
//
//import javax.swing.*;
//import javax.swing.filechooser.FileFilter;
//
//import java.awt.*;
//import java.io.*;
//import java.text.ParseException;
//import java.util.ArrayList;
//import java.util.List;
//
//import static Control.Utility.EntryFilter.FilterType.TOMBOLA;
//import static Control.Utility.EntryFilter.separator;
//import static javax.swing.JOptionPane.*;
//import static Window.Main.uh;
//
//
public class MenuHandler {

    private ProgramStateListener controller;
    private final JFileChooser fileDialog = new JFileChooser(System.getProperty("user.home"));

    public MenuHandler(ProgramStateListener listener){
        controller = listener;
    }

    public void notImplemented(){
        String msg = uh.getUIStr("ERR","NOT_IMPLEMENTED");
        JOptionPane.showMessageDialog(null,msg, uh.getUIStr("ERR","HEADER"),ERROR_MESSAGE);
    }


    public void exportEntries() {
        Object[] filters = controller.filterTypes;
        EntryFilter resultFilter = (EntryFilter)JOptionPane.showInputDialog(
                new JFrame(),
                "Válassz az exportálási lehetőségek közül:",
                "Exportálás",
                JOptionPane.QUESTION_MESSAGE,
                null,
                filters,
                filters[0]);

        if(resultFilter == null){
            System.out.println("Export has been cancelled by user : No filter");
            return;
        }
        //Let the export begin
        fileDialog.resetChoosableFileFilters();
        fileDialog.addChoosableFileFilter(resultFilter);
        int expRes = fileDialog.showSaveDialog(null);
        if(expRes != JFileChooser.APPROVE_OPTION){
            System.out.println("Export has been cancelled by user : No file");
            return;
        }
        try (PrintWriter exportWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fileDialog.getSelectedFile())))) {
            controller.exportList(exportWriter, resultFilter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage(), uh.getUIStr("ERR", "HEADER"), ERROR_MESSAGE);
        }

    }
//
//    public void renewState() {
//        if(!programState) {
//            String question = "A munkád nincs még elmentve\n" +
//                    "és ha továbblépsz törlésre kerül.\n" +
//                    uh.getUIStr("MSG","CONFIRM_ACTION");
//            if (ConfirmAction(question) != YES_OPTION) return;
//        }
//        listener.renewState();
//    }
//
//    public void loadState() {
//
//    }
//
    public void importEntries(){
        EntryFilter importFilter = new EntryFilter();
        fileDialog.setDialogTitle("Lista importálása");
        fileDialog.setApproveButtonText("Import");
        fileDialog.resetChoosableFileFilters();
        fileDialog.addChoosableFileFilter(importFilter);
        fileDialog.setAcceptAllFileFilterUsed(false);

        int choice = fileDialog.showOpenDialog(null);


        if(choice == JFileChooser.APPROVE_OPTION){
            File entryFile = fileDialog.getSelectedFile();
            System.out.println("Opening " + entryFile);
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(entryFile)));
                controller.importList(reader,importFilter);

            } catch (IOException ex){
                JOptionPane.showMessageDialog(new JFrame(),
                        uh.getUIStr("ERR","IMPORT_PARSE_FAIL") +"\n" +
                                uh.getUIStr("ERR","DETAILS") + ":\n" + ex.getMessage(),
                        uh.getUIStr("ERR","HEADER"), JOptionPane.ERROR_MESSAGE);
                System.out.println(ex.getMessage());
            }

        }
    }
//
//    public void changeProfile(){
//        Object[] profiles = profileNames;
//        String result = (String)JOptionPane.showInputDialog(
//                new JFrame(),
//                "Válassz az profilok közül:",
//                "Profil módosítása",
//                JOptionPane.QUESTION_MESSAGE,
//                null,
//                profiles,
//                activeProfile);
//        String question = "Ha új profilra váltasz, " +
//                "a jelenlegi munkádat nem folytathatod tovább.\n" +
//                "Minden elmentetlen munkád elvész.\n" +
//                uh.getUIStr("MSG","CONFIRM_ACTION");
//        if(result != null && ConfirmAction(question) == YES_OPTION) {
//            listener.changeProfile(result);
//        }
//    }
//
//    public void editProfile(){
//        listener.getProfile().getProfileWizard().setVisible(true);
//    }
//
//    String checkEntryID(String code) throws IOException {
//        return listener.getProfile().validateCode(code);
//    }
//

//
//    void changeState(boolean stateChanged) {
//        programState = stateChanged;
//        listener.stateChanged(programState, saveFileName);
//    }
//
//    /**
//     * Adds a Programlistener to the listener list
//     * @param l the new listener
//     */
//    void setListener(ProgramStateListener l){
//        listener = l;
//    }
//
//    public void save(boolean saveAs) {
//        changeState(false);
//    }
//
//    private static int ConfirmAction(String message){
//        Object[] dialogOptions = {"Igen","Nem","Mégse"};
//        return JOptionPane.showOptionDialog(
//                new JFrame(),
//                message,
//                uh.getUIStr("MSG","WARNING"),
//                JOptionPane.YES_NO_CANCEL_OPTION,
//                JOptionPane.QUESTION_MESSAGE,
//                null,
//                dialogOptions,
//                dialogOptions[0]);
//    }
//
//    /**
//     * Opens a file dialog, with which the user can interact.
//     * @param parent the parent component
//     * @param title the title of the dialog
//     * @param approveBtn the text of the OK button
//     * @param acceptAll whether All files(*.*) is an option for file filter
//     * @param filters additional file filters
//     * @return the result of the dialog
//     */
//    private int openFileDialog(Component parent, String title, String approveBtn, boolean acceptAll, FileFilter[] filters){
//        fileDialog.resetChoosableFileFilters();
//        if(filters != null)
//            for (FileFilter filter :
//                    filters) {
//                fileDialog.addChoosableFileFilter(filter);
//            }
//        fileDialog.setAcceptAllFileFilterUsed(acceptAll);
//        fileDialog.setDialogTitle(title);
//        fileDialog.setApproveButtonText(approveBtn);
//
//        return fileDialog.showOpenDialog(parent);
//    }
//
//    /**
//     * Saves a file to a specific location
//     * @param lines the lines of the file
//     */
//    void saveFile(Object[] lines) {
//        int result = openFileDialog(new JFrame(),"Fájl mentése","Mentés",true,null);
//        if(result == JFileChooser.APPROVE_OPTION){
//            File file = fileDialog.getSelectedFile();
//            //File file2 = null;
//            if(!file.getName().contains(".")) {
//                file = new File(file.getPath() + ".txt");
//            }
//            //JOptionPane.showMessageDialog(new JFrame(),"A fájlformátumot nem sikerült átírni","Figyelem",JOptionPane.WARNING_MESSAGE);
//
//            try {
//                PrintWriter fileWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file)));
//                for (Object line : lines) {
//                    fileWriter.println(line.toString());
//                }
//                fileWriter.close();
//
//                JOptionPane.showMessageDialog(new JFrame(),"Lista exportálva","Kész",JOptionPane.INFORMATION_MESSAGE);
//
//            } catch (FileNotFoundException e) {
//                JOptionPane.showMessageDialog(new JFrame(),
//                        "A fájl mentése nem sikerült\n"+
//                                "Részletek: " + e.getMessage(),
//                        "Hiba",
//                        JOptionPane.ERROR_MESSAGE
//                );
//            }
//        }
//    }
//
//    /*
//    private static boolean fillOptionIsDefault(int offset) throws ParseException {
//        //Detecting anomaly: There are not enough required fields
//        String message = "A rekord egyes argumentumai hiányoznak.\n" +
//                "Hogyan szeretnéd őket kitölteni?";
//        Object[] recordOptions = {"Alapméretezett értékekkel","Majd én kitöltöm őket"};
//        //Request smart input from user
//        Object r = JOptionPane.showInputDialog(new JFrame(),message,"Figyelem",WARNING_MESSAGE,null,recordOptions,recordOptions[0]);
//        if(r == null){
//            int res = JOptionPane.showConfirmDialog(new JFrame(),options.getUIStr("MSG","CANCEL_ACTION")+"\n"+
//                    "Válassz igent, ha meg akarod szakítani az importálást","Figyelem",YES_NO_OPTION,WARNING_MESSAGE);
//            if(res == YES_OPTION) throw new ParseException("Az importálás kézileg lett megszakítva",offset);
//        } else return r.equals(recordOptions[0]);
//        return true;
//    }
//    */
//
}
