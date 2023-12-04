package control;

import com.fazecast.jSerialComm.SerialPort;
import control.utility.devices.BarCodeReaderListenerFactory;
import control.utility.file.EntryFilter;
import control.utility.network.NetworkController;
import data.*;
import data.modifier.Discount;
import data.util.ReadingFlag;
import view.StatisticsWindow;
import view.main.LoadingScreen;
import view.main.interactive.EnableWatcher;
import view.main.interactive.ReadFlagListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static control.Application.uh;
import static javax.swing.JOptionPane.*;

/**
 * The main manager class of the program.
 * It soress data and distributes the tasks among other classes
 * @author Mihalovits Márk
 */
public class AppController implements ProgramStateListener, EntryCodeReader {

    /**
     * The model for the entries in the program
     */
    private final AppData model;

    /**
     * The model for the profiles in the program
     */
    private final DataModel<EntryProfile> profiles;

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
    private final List<ReadFlagListener> listenerList = new ArrayList<>();

    private final List<EnableWatcher> actionListWatcher = new ArrayList<>();

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

    /**
     * A queue for user actions
     */
    private final Deque<UserAction> actionQueue = new ArrayDeque<>();

    AppController(AppData model, DataModel<EntryProfile> pData){
        this.model = model;
        profiles = pData;
        activeProfile = pData.getSelectedData();
        while(activeProfile == null)
            changeProfile(chooseProfile(),true);
    }

    public void addActionWatcher(EnableWatcher watcher) {
        actionListWatcher.add(watcher);
    }

    public void scanPorts(JComboBox<String> cbSelections){
        System.out.println("[INFO]: Scanning for ports...");
        cbSelections.removeAllItems();
        for (String portName : BarCodeReaderListenerFactory.refreshSerialPorts()) {
            cbSelections.addItem(portName);
            System.out.println("[INFO]: Scanned " + portName);
        }
        if(cbSelections.getItemCount() == 0)
            cbSelections.addItem(DEFAULT_OPTION);
    }

    public void checkPort(ItemEvent event, JLabel label){
        if(event.getStateChange() == ItemEvent.SELECTED){
            if(selectedPort != null) selectedPort.closePort();
            String portSelected = event.getItem().toString();
            if(!portSelected.equals(DEFAULT_OPTION)){
                BarCodeReaderListenerFactory.connectSerialPort(portSelected);
                BarCodeReaderListenerFactory.generateReader(this::receiveBarCode,"",false);

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
     * 
     * @param flag the operation flag (Enter, Leave or Delete)
     * @param entry the entry the operation is executed on. (For thread-safe measures (see: online-mode))
     */
    public void flagOperationOnEntry(ReadingFlag flag, Entry entry){
        readingFlag = flag;
        readEntryCode(entry.get(0)); //TODO: this is not safe (should use ID.ordinal() instead of magic constant) OR should use the entry itself for the operation
    }

    /**
     * A user can execute a discount application or removal in a record
     * 
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
        Object[] profileObjs = new Object[profiles.getSize()];
        for (int i = 0; i < profileObjs.length; i++) {
            profileObjs[i] = profiles.getElementAt(i);
        }
        return (EntryProfile) JOptionPane.showInputDialog(
                null,
                "Válassz egyet",
                "Aktív profil kiválasztása",
                JOptionPane.QUESTION_MESSAGE, null,
                profileObjs, profileObjs[0]);
    }

    @Override
    public void setReadingFlag(ReadingFlag newFlag){
        readingFlag = newFlag;
        for (ReadFlagListener l: listenerList) {
            l.readingFlagChanged(readingFlag);
        }
    }

    @Override
    public void readEntryCode(String text) {
        receiveBarCode(activeProfile.getEntryCode() + text);
    }

    public void editProfile(JFrame main, JLabel label) {
        menuOpen = true;
        EntryProfile editedProfile = EntryProfile.createProfileFromWizard(main, new EntryProfile(activeProfile));
        if(editedProfile != null) {
            boolean restartNeeded = EntryProfile.isRestartNeeded(activeProfile, editedProfile);
            boolean confirm = true;
            //Remove active profile
            if (restartNeeded)
                confirm = JOptionPane.showConfirmDialog(null, "Ezzel törölsz minden adatot a rendszerből\n" + uh.getUIStr("MSG","CONFIRM"), uh.getUIStr("MSG","WARNING"),
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
            if(confirm){
                try {
                    profiles.replaceData(activeProfile, editedProfile);
                    label.setText(changeProfile(editedProfile, restartNeeded));
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
        statWindow = new StatisticsWindow(activeProfile, model);
        statWindow.setVisible(true);
    }

    public void switchOnlineMode() {
        try {
            netController = new NetworkController(activeProfile,this,"localhost",5503);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String changeProfile(EntryProfile newProfile, boolean restart){
        if (newProfile != null && newProfile != activeProfile) {
            activeProfile = newProfile;
            profiles.setSelection(activeProfile);
            System.out.println("[INFO]: Profile selected: " + activeProfile);
            JOptionPane.showMessageDialog(null, "Profil aktiválva:\n" + activeProfile, uh.getUIStr("MSG","DONE"), JOptionPane.INFORMATION_MESSAGE);
            if(restart) clearData();
        }
        return getProfileName();
    }

    @Override
    public void exportList(PrintWriter writer, EntryFilter filter) {
        LoadingScreen progress = new LoadingScreen();
        progress.setTasks(model.getSize());
        for (int i = 0; i < model.getSize(); i++) {
            progress.setProgress("Rekordok exportálása: 1" + i + "/" + model.getSize());
            Entry data = model.getElementAt(i);
            writer.println(data.applyFilter(filter));
        }
        progress.done("Az Exportálás befejeződött!");
    }

    @Override
    public void importList(BufferedReader reader, EntryFilter filter) throws IOException{
        StringBuilder stackTrace = new StringBuilder();
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
                stackTrace.append(allLines).append(". sor: ").append(ex.getMessage()).append("\n");
            }
        } while (true);
        String trace = stackTrace.toString();
        progress.done(lines + " rekord a " + allLines + " rekordból importálva!");
        if (!trace.equals("")) {
            JTextArea text = new JTextArea(trace);
            text.setEditable(false);
            text.setOpaque(false);
            text.setPreferredSize(new Dimension(300,400));
            JOptionPane.showMessageDialog(null, new JScrollPane(text), uh.getUIStr("MSG","WARNING"), JOptionPane.WARNING_MESSAGE);
        }

    }

    @Override
    public void updateEntry(String id, Entry newData) {
        try {
            model.replaceData(model.getElementById(id), newData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearData() {
        model.clearData();
    }

    /**
     * Ez a fontos függvény!!!
     * @param barCode a kód, amit a soros portról kapunk
     */
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
            switch (readingFlag) {
                case FL_DEFAULT -> {
                    Entry entry;
                    if (model.getSelectedData() != null &&
                            activeProfile.enteringModifiesEntry(model.getSelectedData().getID())) {                     // Check if Entry Profile modifies ID upon entering (and selected ID matches the required mask)
                        Entry existing = model.getElementById(entryID);                                                    // If true, check if the new entry ID exists already!
                        if (existing != null) {
                            entry = existing;                                                                           // If it does, continue with the existing record!!
                        } else {
                            entry = activeProfile.generateFromEntry(model.getSelectedData(), entryID);                  // If not, the ID is new, so generate a new entry from the selected record and modify it's ID
                            saveLastAction(model.getSelectedData(), entry);                                             // Save the action to the ActionQueue
                            model.replaceData(model.getSelectedData(), entry);                                          // Replace the old Entry with the newly generated Entry!
                        }
                    } else {                                                                                            // If Entry Profile does NOT modify ID, or selected ID does not match the mask:
                        entry = model.getElementById(entryID);                                                             // Search for the Entry based on the entered ID
                        if (entry == null) {
                            entry = activeProfile.generateNewEntry(entryID);                                            // If entered ID does not exist, create a new Entry with said ID
                            if (entry == null)
                                break;                                                                   // If generating fails interrupt execution
                        }
                    }
                    model.addData(entry);                                                                               // Add new Entry to the model (select if already exists)
                    entry.Enter();                                                                                      // Enter selected Entry
                }
                case FL_IS_DELETE -> {
                    if (JOptionPane.showConfirmDialog(null, uh.getUIStr("MSG", "CONFIRM"), uh.getUIStr("MSG", "DELETE"), JOptionPane.OK_CANCEL_OPTION, WARNING_MESSAGE) == OK_OPTION)
                        model.removeData(model.getElementById(entryID));
                }
                case FL_IS_LEAVING -> {
                    Entry leaving = model.getElementById(entryID);
                    if (leaving == null) throw new IOException(uh.getUIStr("ERR", "NO_MATCH"));
                    leaving.Leave();
                }
                case FL_GENERATE_NEW -> receiveBarCode(model.generateNewID());
            }
        } catch (IOException ex){
            JOptionPane.showMessageDialog(null, ex.getMessage(),"Figyelem", JOptionPane.WARNING_MESSAGE);
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

    @Override
    public void addReadingFlagListener(ReadFlagListener listener) {
        listenerList.add(listener);
    }

    public void saveLastAction(Entry previousEntry, Entry nextEntry) {
        actionQueue.push(new UserAction(nextEntry, previousEntry));
        if (actionQueue.size() > activeProfile.getMaxActionCount()) {
            actionQueue.removeLast();
        }
        actionListWatcher.forEach(w -> w.updateEnabled(!actionQueue.isEmpty()));
    }

    public void undoLastAction() {
        System.out.println("Undo Action");
        UserAction action = actionQueue.pop();
        if(action != null) {
            model.setSelection(action.undo());
            int index = model.getSelectedIndex();
            model.fireTableRowsUpdated(index, index);
        }
        System.out.println(actionQueue.size() + " actions remaining");
        actionListWatcher.forEach(w -> w.updateEnabled(!actionQueue.isEmpty()));
    }

}
