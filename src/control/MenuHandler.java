package control;

import control.utility.file.EntryFilter;

import javax.swing.*;
import java.io.*;

import static control.Application.uh;
import static javax.swing.JOptionPane.ERROR_MESSAGE;


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
        EntryFilter[] filters = controller.filterTypes;
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

        File expFile = fileDialog.getSelectedFile();
        if(!expFile.getName().endsWith(".txt")) {
            String newname = expFile.getPath() + ".txt";
            expFile = new File(newname);
        }


        try (PrintWriter exportWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(expFile)))) {
            controller.exportList(exportWriter, resultFilter);
            JOptionPane.showMessageDialog(null,uh.getUIStr("MSG","EXPORT_DONE"),uh.getUIStr("MSG","DONE"),JOptionPane.INFORMATION_MESSAGE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage(), uh.getUIStr("ERR", "HEADER"), ERROR_MESSAGE);
        }

    }

    public void importEntries(){
        EntryFilter importFilter = new EntryFilter("Dummy",null);
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


}
