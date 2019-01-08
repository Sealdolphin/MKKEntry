package Control;

import Control.EntryModifier.TicketType;
import Control.Utility.BarcodeReader;
import Control.Utility.EntryFilter;
import Control.Utility.ExportFilter;
import Control.Utility.TombolaFilter;
import Window.MainWindow;
import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import Window.ReadingFlagListener;
import Window.ProgramStateListener;

import static Control.Entry.Member.M_ENTERED;
import static Control.Entry.Member.M_UID;
import static Control.Utility.EntryFilter.parseFilterType;

/**
 * The responsible unit for the operative decisions
 * It contains the List of entries and also the selected port
 * It also contains a default selection of commands
 * These commands are read at the start of the program from config.ini
 * If options.ini file is missing or corrupted than the default command strings are loaded
 */
public class EntryController implements ItemListener {

    /**
     *  For the different reading operations
     */
    public enum readCodeFlag{
        FL_IS_LEAVING,
        FL_IS_DELETE,
        FL_DEFAULT
    }

    public static String ENTRY_CODE;                               //Entry code: a short string that indicates valuable data
    public static final String DEFAULT_OPTION = "Válassz egyet";    //String for null option

    private JTable tableView;
    private EventHandler defaultEventHandler;
    private Entry lastEntry = null;
    private ReadingFlagListener infoBar;


    /**
     * Indicates what the next reading operation means
     */
    private readCodeFlag readingFlag = readCodeFlag.FL_DEFAULT;

    private List<String> discountMetaData;

    /**
     * A list of command strings
     */
    private HashMap<String,readCodeFlag> commandList = new HashMap<>();

    /**
     * The list of entries
     */
    private EntryTable entryList;

    /**
     * The selected serial port of the device
     */
    private SerialPort selectedPort;

    /**
     * Default Constructor for the controller
     * Relies on eventhandler to set up defaults
     * @param profile the active profile
     * @param handler the active EventHandler (can be null)
     * @param profileNames all the loaded profile names
     * @param infoBar the applications information bar
     */
    public EntryController(EntryProfile profile,EventHandler handler, Object[] profileNames, ReadingFlagListener infoBar){
        //Setting up command list
        profile.setController(this);

        this.infoBar = infoBar;
        System.out.println("ENTRY = " + ENTRY_CODE);
        System.out.println(commandList);

        //Connecting with EventHandler if it does not exist then create one
        defaultEventHandler = Objects.requireNonNullElseGet(handler, () -> new EventHandler(profile.getName(), profileNames));
        entryList = new EntryTable(TicketType.defaultType);
        infoBar.flagChange(readingFlag);
    }

    /**
     * Checks the state of the serial port or device
     * @return true if the selected port is not null false otherwise
     */
    public boolean portIsActive(){
        return selectedPort != null;
    }

    /**
     * Receives a command code from the device through the serial port
     * Depending on the code it may activate a feature
     * If the code is unknown then disregards it
     * @param code the received code
     */
    public void receiveCode(String code) {
        System.out.println("Code received: " + code);
        String errorMsg = "";
        if(code.toUpperCase().startsWith(ENTRY_CODE)) {
            System.out.println("Entry code detected!");
            //Handling entry code correct to the current read operation
            try {
                String codeNumber = code.replaceFirst(ENTRY_CODE, "").trim();
                Entry guest = entryList.stream().filter(e -> e.getValue(M_UID.ordinal()).equals(codeNumber)).findAny().orElse(null);
                switch (readingFlag) {
                    //New entry code
                    default:
                    case FL_DEFAULT:
                        System.out.println("New entry: " + code);
                        if(guest == null) {
                            guest = new Entry(codeNumber,TicketType.defaultType);
                            entryList.addEntry(guest);
                            guest.Enter();
                            lastEntry = guest;
                        } else if (guest.getValue(M_ENTERED.ordinal()).equals(false)){
                            guest.Enter();
                        } else errorMsg = "Ez a vendég már egyszer belépett!";
                        break;
                        //Leave code
                    case FL_IS_LEAVING:
                        if(guest != null){
                            guest.Leave();
                        } else errorMsg = "Ez a vendég még nem lépett be!";
                        break;
                        //Delete code
                    case FL_IS_DELETE:
                        entryList.removeEntry(guest);
                        break;
                }
                refreshViewModel();

            } finally {
                readingFlag = readCodeFlag.FL_DEFAULT;
                if(!errorMsg.equals("")) {
                    System.out.println("ERROR: " + errorMsg);
                    JOptionPane.showMessageDialog(new JFrame(), errorMsg, "Figyelem", JOptionPane.WARNING_MESSAGE);
                }
            }
        } else {
            //Checking for Command codes
            for (String commandFlag :
                    commandList.keySet()) {
                if (code.toUpperCase().contains(commandFlag)) {
                    System.out.println("Command code detected!");
                    readingFlag = commandList.get(commandFlag);
                }
            }
            //Command for discounts
            if(lastEntry != null) {
                for (String command : discountMetaData) {
                    if (code.equals(command)) {
                        System.out.println("Discount detected");
                        readingFlag = readCodeFlag.FL_DEFAULT;
                    }
                }
            }
        }
        infoBar.flagChange(readingFlag);

    }

    private void refreshViewModel() {
        //Refresh data model
        entryList.fireTableDataChanged();
        if(tableView != null) {
            tableView.setModel(entryList);
            tableView.setRowSorter(new TableRowSorter<>(entryList));
        }
        defaultEventHandler.changeState(true);
    }

    public void setProgramStateListener(ProgramStateListener l){
        defaultEventHandler.setListener(l);
    }

    public void setTable(JTable entryView) {
        tableView = entryView;
        refreshViewModel();
    }

    public EventHandler getDefaultEventHandler() {
        return defaultEventHandler;
    }

    void importEntries(List<Entry> imported) {
        for (Entry e :
                imported) {
            entryList.addEntry(e);
        }
        refreshViewModel();
    }

    void exportList(String resultFilter) {
        ExportFilter filter;
        switch (parseFilterType(resultFilter)){
            default:
            case DEFAULT:
                filter = new EntryFilter();
                break;
            case TOMBOLA:
                filter = new TombolaFilter();
                break;
        }
        defaultEventHandler.saveFile(entryList.stream().map(filter::applyFilter).toArray());
    }

    void setMetaData(String entryCode, List<String> discountMeta, String[] defaultMeta) {
        ENTRY_CODE = entryCode;

        commandList.put(defaultMeta[0],readCodeFlag.FL_IS_LEAVING);
        commandList.put(defaultMeta[1],readCodeFlag.FL_IS_DELETE);
        this.discountMetaData = discountMeta;
    }

    /**
     * Implemented from ItemListener (JComboBoxListener)
     * Changes the selected port
     * @param e ItemEvent of the selected item
     */
    @Override
    public void itemStateChanged(ItemEvent e) {
        if(selectedPort != null) selectedPort.closePort();
        String portSelected = e.getItem().toString();
        selectedPort = SerialPort.getCommPort(portSelected);
        if(portSelected.equals(DEFAULT_OPTION)) selectedPort = null;
        if(portIsActive()){
            System.out.println("Device connected at " + portSelected);
            BarcodeReader reader = new BarcodeReader();
            reader.addListener((MainWindow) SwingUtilities.getWindowAncestor((Component) e.getSource()));
            selectedPort.addDataListener(reader);
            selectedPort.openPort();
        }
    }

}
