package control;

import control.modifier.Discount;
import control.utility.devices.BarCodeReaderListenerFactory;
import control.utility.file.EntryFilter;
import control.utility.network.NetworkController;
import data.AppData;
import com.fazecast.jSerialComm.SerialPort;
import data.DataModel;
import data.Entry;
import data.EntryProfile;
import view.StatisticsWindow;
import view.main.LoadingScreen;
import view.main.ReadFlagListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static control.Application.uh;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.OK_OPTION;
import static javax.swing.JOptionPane.WARNING_MESSAGE;

/**
 * The main manager class of the program.
 * It soress data and distributes the tasks among other classes
 * @author Mihalovits Márk
 */
public class AppController implements ProgramStateListener {

    /**
     * The model for the entries in the program
     */
    private AppData model;

    /**
     * The model for the profiles in the program
     */
    private DataModel<EntryProfile> profiles;

    /**
     * The currently active profile
     */
    private EntryProfile activeProfile;

    /**
     * Default option for choosing
     */
    private static final String DEFAULT_OPTION = uh.getUIStr("UI","CHOOSE_ONE");

    /**
     * The selected port for the barcode reader (optional)
     */
    private SerialPort selectedPort;

    /**
     * The listeners who are receiving the flag changing signals
     * Such an event occur every time when reading mode is changed. (Leave or Delete)
     */
    private List<ReadFlagListener> listenerList = new ArrayList<>();

    /**
     * The current reading flag
     */
    private ReadingFlag readingFlag = ReadingFlag.FL_DEFAULT;

    /**
     * Whether a menu is opened.
     * This enables / disables operation.
     */
    private boolean menuOpen = false;

    /**
     * The statistics window of the program
     */
    private StatisticsWindow statWindow;

    /**
     * The net controller for online mode
     */
    private NetworkController netController;

    AppController(AppData model, DataModel<EntryProfile> pData){
        this.model = model;
        profiles = pData;
        activeProfile = pData.getSelectedData();
        while(activeProfile == null)
            changeProfile(chooseProfile(),true);
    }

    public void addListener(ReadFlagListener l){
        listenerList.add(l);
    }

    public void scanPorts(JComboBox<String> cbSelections){
        System.out.println("[INFO]: Scanning for ports...");
        cbSelections.removeAllItems();
        for (SerialPort port : SerialPort.getCommPorts()) {
            cbSelections.addItem(port.getSystemPortName());
            System.out.println("[INFO]: Scanned " + port.getSystemPortName());
        }
        if(cbSelections.getItemCount() == 0)
            cbSelections.addItem(DEFAULT_OPTION);
    }

    public void checkPort(ItemEvent event, JLabel label){
        if(event.getStateChange() == ItemEvent.SELECTED){
            if(selectedPort != null) selectedPort.closePort();
            String portSelected = event.getItem().toString();
            selectedPort = SerialPort.getCommPort(portSelected);
            if(portSelected.equals(DEFAULT_OPTION)) selectedPort = null;
            if(selectedPort != null && selectedPort.openPort()){
                BarCodeReaderListenerFactory.connectSerialPort(selectedPort);
                BarCodeReaderListenerFactory.generateReader(this::receiveBarCode,"",false);
                //DEPRECATED
//                System.out.println("[INFO]: Device connected at " + portSelected);
//                BarcodeReader reader = new BarcodeReader();
//                reader.addListener(this);
//                selectedPort.addDataListener(reader);


                label.setBackground(Color.GREEN);
                label.setText(uh.getUIStr("UI", "PORT_ACTIVE"));
            } else {
                if(selectedPort != null) System.out.println("Could not connect to selected port!");
                label.setBackground(Color.RED);
                label.setText(uh.getUIStr("UI", "PORT_INACTIVE"));
            }
        }
    }

    public String getProfileName(){
        if(activeProfile == null) return null;
        return activeProfile.toString();
    }

    public JPanel getSidePanel(){
        return activeProfile.getSidePanel();
    }

    /**
     * A user can execute a list operation on a record
     * @param flag the operation flag (Enter, Leave or Delete)
     * @param entry the entry the operation is executed on. (For thread-safe measures (see: online-mode))
     */
    public void flagOperationOnEntry(ReadingFlag flag, Entry entry){
        readingFlag = flag;
        //TODO: this is not safe
        readEntryCode(entry.get(0));
    }

    /**
     * A user can execute a discount application or removal in a record
     * @param entry the entry the discount is applied to or removed from. (For thread-safe measures (see: online-mode))
     */
    public void discountOperationOnEntry(Entry entry){
        //Open the discount menu (JList)
        Discount[] discounts = activeProfile.getDiscounts();
        if(discounts.length == 0){
            JOptionPane.showMessageDialog(null,uh.getUIStr("ERR","NO_DISCOUNT"),uh.getUIStr("ERR","HEADER"),ERROR_MESSAGE);
            return;
        }

        Discount result = (Discount) JOptionPane.showInputDialog(null,
                "Melyik kedvezményt módosítod?",
                "Vendég módosítása",
                JOptionPane.PLAIN_MESSAGE,null,discounts,discounts[0]);
        //after selection is not null
        if (result != null){
            model.setSelection(entry); //This is thread-safe...
            receiveBarCode(result.getMeta());
        }
    }

    public EntryProfile chooseProfile(){
        //Choosing profile
        Object[] profileObjs = new Object[profiles.getDataSize()];
        for (int i = 0; i < profileObjs.length; i++) {
            profileObjs[i] = profiles.getDataByIndex(i);
        }
        return (EntryProfile) JOptionPane.showInputDialog(
                null,
                "Válassz egyet",
                "Aktív profil kiválasztása",
                JOptionPane.QUESTION_MESSAGE, null,
                profileObjs, profileObjs[0]);
    }

    public void setReadingFlag(ReadingFlag newFlag){
        readingFlag = newFlag;
        for (ReadFlagListener l: listenerList) {
            l.readingFlagChanged(readingFlag);
        }
    }

    public void readEntryCode(String text) {
        receiveBarCode(activeProfile.getEntryCode() + text);
    }

    public void editProfile(JFrame main, JLabel label) {
        menuOpen = true;
        EntryProfile editedProfile = EntryProfile.createProfileFromWizard(main,new EntryProfile(activeProfile));
        if(editedProfile != null) {
            boolean restartNeeded = EntryProfile.isRestartNeeded(activeProfile,editedProfile);
            boolean confirm = true;
            //Remove active profile
            if (restartNeeded)
                confirm = JOptionPane.showConfirmDialog(null, "Ezzel törölsz minden adatot a rendszerből\n" + uh.getUIStr("MSG","CONFIRM"), uh.getUIStr("MSG","WARNING"),
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
            if(confirm){
                try {
                    profiles.replaceData(activeProfile,editedProfile);
                    label.setText(changeProfile(editedProfile,restartNeeded));
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null,"HIBA: " + ex.getMessage(),uh.getUIStr("ERR","HEADER"),ERROR_MESSAGE);
                }
            }
        }
        menuOpen = false;
    }

    public void addProfile(EntryProfile newProfile) throws IOException {
        profiles.addData(newProfile);
    }

    public void createStatistics() {
        if(statWindow != null)
            if(statWindow.isVisible())
                statWindow.dispose();
        statWindow = new StatisticsWindow(activeProfile,model);
        statWindow.setVisible(true);
    }

    public void switchOnlineMode() {
        try {
            netController = new NetworkController(activeProfile,this,"localhost",5503);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  For the different reading operations
     */
    public enum ReadingFlag{
        FL_IS_LEAVING("leave","Kilépésre vár",new Color(0xffcc00),Color.BLACK),
        FL_IS_DELETE("delete","Törlésre vár",new Color(0xaa0000),Color.WHITE),
        FL_DEFAULT("default","Belépésre vár",new Color(0x00aa00),Color.BLACK);

        private final String flagMeta;
        private final String labelInfo;
        private final Color labelColor;
        private final Color fgColor;

        ReadingFlag(String meta, String info, Color color, Color fg){
            flagMeta = meta;
            labelInfo = info;
            labelColor = color;
            fgColor = fg;
        }

        public String getInfo(){ return labelInfo; }
        public String getMeta(){ return flagMeta; }
        public Color getColor(){ return labelColor; }
        public Color getTextColor(){ return fgColor; }
    }

    @Override
    public String changeProfile(EntryProfile newProfile, boolean restart){
        if (newProfile != null && newProfile != activeProfile) {
            activeProfile = newProfile;
            profiles.setSelection(activeProfile);
            System.out.println("[INFO]: Profile selected: " + activeProfile);
            JOptionPane.showMessageDialog(null, "Profil aktiválva:\n" + activeProfile, uh.getUIStr("MSG","DONE"), JOptionPane.INFORMATION_MESSAGE);
            if(restart) model.clearData();
        }
        return getProfileName();
    }

    @Override
    public void exportList(PrintWriter writer, EntryFilter filter) {
        LoadingScreen progress = new LoadingScreen();
        progress.setTasks(model.getDataSize());
        for (int i = 0; i < model.getDataSize(); i++) {
            progress.setProgress("Rekordok exportálása: 1" + i + "/" + model.getDataSize());
            Entry data = model.getDataByIndex(i);
            writer.println(data.applyFilter(filter));
        }
        progress.done("Az Exportálás befejeződött!");
    }

    @Override
    public void importList(BufferedReader reader, EntryFilter filter) throws IOException{
        LoadingScreen progress = new LoadingScreen();
        progress.setTasks(-1);
        int lines = 0;
        int allLines = 0;
        do {
            String line = reader.readLine();
            if(line == null) break; //Breaks at FIRST EMPTY LINE
            allLines++;
            try{
                model.addData(Entry.importEntry(filter.parseEntry(line),activeProfile));
                lines++;
            } catch (IOException ex){
                JOptionPane.showMessageDialog(null,ex.getMessage(),uh.getUIStr("MSG","WARNING"),JOptionPane.WARNING_MESSAGE);
            }
        } while (true);
        progress.done(lines + " rekord a " + allLines + " rekordból importálva!");

    }

    @Override
    public void updateEntry(String id, Entry newData) {
        try {
            model.replaceData(model.getDataById(id),newData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveBarCode(String barCode) {
        if(menuOpen) return;
        System.out.println("[INFO]: Code received: " + barCode);
        //Checking for command codes
        ReadingFlag commandFlag = activeProfile.validateCommand(barCode);
        if(commandFlag != null){
            setReadingFlag(commandFlag);
            return;
        }

        try {
            if(!(barCode.length() > 0)) return;

            //Checking for discount codes
            Entry lastSelection = model.getSelectedData();
            Discount discount = activeProfile.identifyDiscountMeta(barCode);
            if(discount != null) {
                if(lastSelection != null) {
                    lastSelection.applyDiscount(discount);
                    return;
                } else throw new IOException(uh.getUIStr("ERR","NO_SELECTION"));
            }

            //Validate as Entry code
            String entryID = activeProfile.validateCode(barCode);

            //Check for reading flag
            switch (readingFlag){
                default:
                case FL_DEFAULT:
                    Entry entry = activeProfile.generateNewEntry(entryID);
                    //Add data if correct
                    model.addData(entry);
                    entry.Enter();
                    break;
                case FL_IS_DELETE:
                    if(JOptionPane.showConfirmDialog(null,uh.getUIStr("MSG","CONFIRM"),uh.getUIStr("MSG","DELETE"), JOptionPane.OK_CANCEL_OPTION,WARNING_MESSAGE) == OK_OPTION)
                        model.removeData(model.getDataById(entryID));
                    break;
                case FL_IS_LEAVING:
                    Entry leaving = model.getDataById(entryID);
                    if(leaving == null) throw new IOException(uh.getUIStr("ERR","NO_MATCH"));
                    leaving.Leave();
                    break;
            }
        } catch (IOException ex){
            JOptionPane.showMessageDialog(null,ex.getMessage(),"Figyelem",JOptionPane.WARNING_MESSAGE);
        } finally {
            setReadingFlag(ReadingFlag.FL_DEFAULT);
            int index = model.getSelectedIndex();
            if(index >= 0)
                model.fireTableRowsUpdated(index,index);
            if(netController != null){
                try {
                    netController.updateData(model.getSelectedData());
                } catch (IOException e) {
                    System.out.println("Networking error happened...");
                    System.out.println("Details: " + e.getMessage());
                    netController = null;
                }
            }
        }

    }

}
