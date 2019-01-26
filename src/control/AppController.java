package control;

import control.modifier.TicketType;
import control.utility.BarcodeReader;
import control.utility.EntryFilter;
import data.AppData;
import com.fazecast.jSerialComm.SerialPort;
import data.DataModel;
import view.main.ReadFlagListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static control.Application.uh;


public class AppController implements ProgramStateListener {

    private AppData model;
    private DataModel<EntryProfile> profiles;
    private EntryProfile activeProfile;
    private static final String DEFAULT_OPTION = uh.getUIStr("UI","CHOOSE_ONE");
    private SerialPort selectedPort;
    private List<ReadFlagListener> listenerList = new ArrayList<>();
    private ReadingFlag readingFlag = ReadingFlag.FL_DEFAULT;

    AppController(AppData model, DataModel<EntryProfile> pData){
        this.model = model;
        profiles = pData;
        activeProfile = pData.getSelectedData();
        if(activeProfile == null)
            changeProfile();
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
            if(selectedPort != null){
                System.out.println("[INFO]: Device connected at " + portSelected);
                BarcodeReader reader = new BarcodeReader();
                reader.addListener(this);
                selectedPort.addDataListener(reader);
                selectedPort.openPort();
                label.setBackground(Color.GREEN);
                label.setText(uh.getUIStr("UI","PORT_ACTIVE"));
            } else {
                label.setBackground(Color.RED);
                label.setText(uh.getUIStr("UI","PORT_INACTIVE"));
            }
        }
    }

    public String getProfileName(){
        return activeProfile.toString();
    }

    @Override
    public String changeProfile(){
        //Choosing profile
        Object[] profileObjs = new Object[profiles.getDataSize()];
        for (int i = 0; i < profileObjs.length; i++) {
            profileObjs[i] = profiles.getDataByIndex(i);
        }
        EntryProfile newProfile = (EntryProfile) JOptionPane.showInputDialog(
                null,
                "Válassz egyet",
                "Aktív profil kiválasztása",
                JOptionPane.QUESTION_MESSAGE, null,
                profileObjs, profileObjs[0]);
        if (newProfile != null && newProfile != activeProfile) {
            activeProfile = newProfile;
            profiles.setSelection(activeProfile);
            System.out.println("[INFO]: Profile selected: " + activeProfile);
            JOptionPane.showMessageDialog(null,"Profil aktiválva:\n" + activeProfile,"Kész",JOptionPane.INFORMATION_MESSAGE);
            //TODO: Profile change requires restart...
        }
        return getProfileName();
    }

    @Override
    public void exportList(PrintWriter writer, EntryFilter filter) {
        //TODO: needs implementation
        System.out.println("[INFO]: Exporting list...");
    }

    @Override
    public void updateView() {

    }

    public void setReadingFlag(ReadingFlag newFlag){
        readingFlag = newFlag;
        for (ReadFlagListener l: listenerList) {
            l.readingFlagChanged(readingFlag);
        }
    }

    @Override
    public void readBarCode(String barCode) {
        System.out.println("[INFO]: Code received: " + barCode);
        //Checking for command codes
        try {
            if(!(barCode.length() > 0)) return;
            //Validate as Entry code
            String entryID = activeProfile.validateCode(barCode);

            //Check for reading flag
            switch (readingFlag){
                default:
                case FL_DEFAULT:
                    Entry entry = activeProfile.generateNewEntry(entryID,null);
                    //Add data if correct
                    model.addData(entry);
                    entry.Enter();
                    break;
                case FL_IS_DELETE:
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
            readingFlag = ReadingFlag.FL_DEFAULT;
            for (ReadFlagListener l: listenerList) {
                l.readingFlagChanged(readingFlag);
            }
            model.fireTableDataChanged();
        }

    }

    /**
     *  For the different reading operations
     */
    public enum ReadingFlag{
        FL_IS_LEAVING,
        FL_IS_DELETE,
        FL_DEFAULT
    }
//
//
//    private JTable tableView;
//    private MenuHandler defaultEventHandler;
//    private Entry lastEntry = null;
//    private ReadingFlagListener infoBar;
//
//
//    /**
//     * Indicates what the next reading operation means
//     */
//    private readCodeFlag readingFlag = readCodeFlag.FL_DEFAULT;
//
//    private List<String> discountMetaData;
//
//    /**
//     * A list of command strings
//     */
//    private HashMap<String,readCodeFlag> commandList = new HashMap<>();
//
//    /**
//     * The list of entries
//     */
//    private EntryTable entryList;
//
//    /**
//     * The selected serial port of the device
//     */
//    private SerialPort selectedPort;
//
//
//    /**
//     * Checks the state of the serial port or device
//     * @return true if the selected port is not null false otherwise
//     */
//    public boolean portIsActive(){
//        return selectedPort != null;
//    }
//
//    /**
//     * Receives a command code from the device through the serial port
//     * Depending on the code it may activate a feature
//     * If the code is unknown then disregards it
//     * @param code the received code
//     */
//    public void receiveCode(String code) {
//        System.out.println("Code received: " + code);
//        String errorMsg = "";
//        if(code.toUpperCase().startsWith(ENTRY_CODE)) {
//            System.out.println("Entry code detected!");
//            //Handling entry code correct to the current read operation
//            try {
//                String entryId = defaultEventHandler.checkEntryID(code);
//                Entry guest = entryList.stream().filter(e -> e.getValue(M_UID.ordinal()).equals(entryId)).findAny().orElse(null);
//                switch (readingFlag) {
//                    //New entry code
//                    default:
//                    case FL_DEFAULT:
//                        System.out.println("New entry: " + code);
//                        if(guest == null) {
//                            guest = new Entry(entryId,TicketType.defaultType);
//                            entryList.addEntry(guest);
//                            guest.Enter();
//                            lastEntry = guest;
//                        } else if (guest.getValue(M_ENTERED.ordinal()).equals(false)){
//                            guest.Enter();
//                        } else errorMsg = "Ez a vendég már egyszer belépett!";
//                        break;
//                        //Leave code
//                    case FL_IS_LEAVING:
//                        if(guest != null){
//                            guest.Leave();
//                        } else errorMsg = "Ez a vendég még nem lépett be!";
//                        break;
//                        //Delete code
//                    case FL_IS_DELETE:
//                        entryList.removeEntry(guest);
//                        lastEntry = null;
//                        break;
//                }
//                refreshViewModel();
//
//            } catch (IOException e) {
//                errorMsg = uh.getUIStr("ERR","CODE_FORMAT") + "\n" + e.getMessage();
//            } finally {
//                readingFlag = readCodeFlag.FL_DEFAULT;
//                if(!errorMsg.equals("")) {
//                    System.out.println("ERROR: " + errorMsg);
//                    JOptionPane.showMessageDialog(new JFrame(), errorMsg, uh.getUIStr("MSG","WARNING"), JOptionPane.WARNING_MESSAGE);
//                }
//            }
//        } else {
//            //Checking for Command codes
//            for (String commandFlag :
//                    commandList.keySet()) {
//                if (code.toUpperCase().contains(commandFlag)) {
//                    System.out.println("Command code detected!");
//                    readingFlag = commandList.get(commandFlag);
//                }
//            }
//            //Command for discounts
//            if(lastEntry != null) {
//                for (String command : discountMetaData) {
//                    if (code.equals(command)) {
//                        System.out.println("Discount detected");
//                        readingFlag = readCodeFlag.FL_DEFAULT;
//                    }
//                }
//            }
//        }
//        infoBar.flagChange(readingFlag);
//
//    }
//
//    private void refreshViewModel() {
//        //Refresh data model
//        entryList.fireTableDataChanged();
//        if(tableView != null) {
//            tableView.setModel(entryList);
//            tableView.setRowSorter(new TableRowSorter<>(entryList));
//        }
//        defaultEventHandler.changeState(true);
//    }
//
//
//    public void setTable(JTable entryView) {
//        tableView = entryView;
//        if(tableView != null) {
//            tableView.getSelectionModel().addListSelectionListener(e -> {
//                if(!(tableView.getSelectedRow() == -1))
//                    lastEntry = entryList.getEntryById(tableView.getValueAt(tableView.getSelectedRow(), 0).toString());
//            });
//        }
//        refreshViewModel();
//    }
//
//
//    void importEntries(List<Entry> imported) {
//        for (Entry e :
//                imported) {
//            entryList.addEntry(e);
//        }
//        refreshViewModel();
//    }
//
//    void exportList(String resultFilter) {
//        ExportFilter filter;
//        switch (parseFilterType(resultFilter)){
//            default:
//            case DEFAULT:
//                filter = new EntryFilter();
//                break;
//            case TOMBOLA:
//                filter = new TombolaFilter();
//                break;
//        }
//        defaultEventHandler.saveFile(entryList.stream().map(filter::applyFilter).toArray());
//    }
//
//    void setMetaData(String entryCode, List<String> discountMeta, String[] defaultMeta) {
//        ENTRY_CODE = entryCode;
//
//        commandList.put(defaultMeta[0],readCodeFlag.FL_IS_LEAVING);
//        commandList.put(defaultMeta[1],readCodeFlag.FL_IS_DELETE);
//        this.discountMetaData = discountMeta;
//    }
//

}
