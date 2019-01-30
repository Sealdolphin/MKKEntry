package control;

import control.modifier.Discount;
import control.utility.BarcodeReader;
import control.utility.EntryFilter;
import data.AppData;
import com.fazecast.jSerialComm.SerialPort;
import data.DataModel;
import data.Entry;
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
        while(activeProfile == null)
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
        if(activeProfile == null) return null;
        return activeProfile.toString();
    }

    public JPanel getSidePanel(){
        return activeProfile.createDiscountMenu();
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
            model.clearData();
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
        try {
            if(!(barCode.length() > 0)) return;
            //Checking for command codes


            //Checking for discount codes
            Entry lastSelection = model.getSelectedData();
            if(lastSelection != null){
                Discount discount = activeProfile.identifyDiscountMeta(barCode);
                if(discount != null) {
                    lastSelection.applyDiscount(discount);
                    return;
                }
            }

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

    public void readEntryCode(String text) {
        readBarCode(activeProfile.codeStart + text);
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
//    private List<String> discountMetaData;
//
//    /**
//     * A list of command strings
//     */
//    private HashMap<String,readCodeFlag> commandList = new HashMap<>();
//
//
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
