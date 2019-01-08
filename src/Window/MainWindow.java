package Window;

import Control.EntryController;
import Control.EntryProfile;
import com.fazecast.jSerialComm.SerialPort;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static Control.EntryController.DEFAULT_OPTION;

/**
 * The class of the main program window
 * It contains two panels: A header and a body
 * The header is responsible for the selection of the port / device
 * The Body is responsible for the guest list.
 * It also has a bottom info panel which displays the state of the reading
 * @author Márk Mihalovits
 */
public class MainWindow extends JFrame implements ProgramStateListener{

    /**
     * The list of the selectable Serial Ports in a combo box
     */
    private JComboBox<String> cbSelectPort = new JComboBox<>();
    /**
     * A label indicating whether the selected port / device is active
     */
    private JLabel lbDeviceActive;
    /**
     * The panel which contains the different discounts (and their respective barcodes) assosiated with the active profile
     */
    private JPanel panelDiscount;
    /**
     * The listView of the entries.
     */
    private JTable entryView;
    /**
     * The currently active profile
     */
    private EntryProfile activeProfile;
    /**
     * All the available profiles
     */
    private List<EntryProfile> profiles;
    /**
     * Indicates whether the discount panel is on or off
     */
    private boolean discountPanelState = false;
    /**
     * The EntryController
     * It is responsible for operative decisions
     */
    private EntryController controller;

    /*
    ================= FUNCTIONS =================
     */

    /**
     * Main Constructor
     * Builds the main window of the program
     */
    MainWindow(){
        //Loading activeProfile
        try {
            loadProfiles();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(new JFrame(),"Nem tudtam betölteni a profilokat!\n"+
                    "A profiles.json fájl hiányzik, vagy sérült.","Hiba",JOptionPane.ERROR_MESSAGE);
            return;
        }

        //Setting Layout for header and body
        setLayout(new BorderLayout());

        //Creating EntryController
        renewState();
        //Setting up default parameters
        setMinimumSize(new Dimension(640,200));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("MKK Beléptető rendszer");

        panelDiscount = activeProfile.createSideMenu();
        add(createHeader(),BorderLayout.NORTH);
        add(createBody(),BorderLayout.CENTER);

        setJMenuBar(MainMenu.createMenu(controller.getDefaultEventHandler()));


        //Running basic event routines
        eventRefreshPorts();

        //Pack window
        pack();

    }

    /**
     * Loads the different profiles from the default json file
     * @throws IOException if the reading fails
     * @throws ParseException if cannot parse the file (damaged, or missing JSON property)
     */
    private void loadProfiles() throws IOException, ParseException {

        profiles = new ArrayList<>();

        JSONParser parser = new JSONParser();
        JSONObject obj;
        obj = (JSONObject) parser.parse(new BufferedReader(new InputStreamReader(new FileInputStream("profiles.json"))));
        JSONArray jsonProfiles = (JSONArray)obj.get("profiles");
        for (Object p: jsonProfiles) {
            profiles.add(EntryProfile.parseProfileFromJson((JSONObject) p));
        }
        String active = obj.get("active").toString();
        for (EntryProfile profile : profiles) {
            if(active.equals(profile.getName())){
                activeProfile = profile;
            }
        }

    }

    /**
     * Activates or deactivates the label indicating the active state of the selected port / device
     * @param active is the state of the port / device
     */
    private void onClickBtnRefreshPorts(boolean active) {
        if(active){
            lbDeviceActive.setText("Aktív");
            lbDeviceActive.setBackground(Color.green);
        } else {
            lbDeviceActive.setText("Inaktív");
            lbDeviceActive.setBackground(Color.red);
        }
    }

    /**
     * Creates the header of the application
     * @return a JPanel containing the header components
     */
    private JPanel createHeader(){
        //Header Components
        JPanel panelHeader = new JPanel();

        //Device state label
        lbDeviceActive = new JLabel();
        lbDeviceActive.setOpaque(true);
        lbDeviceActive.setBackground(Color.RED);

        //Device refresher button
        JButton btnRefreshPorts = new JButton("Frissíts");
        btnRefreshPorts.addActionListener(e -> eventRefreshPorts());
        //Device Combo Box
        cbSelectPort.addItemListener(controller);
        //SideBar button
        JButton btnOpenSideBar = new JButton("Kedvezmények");
        //Adding sidePanel
        btnOpenSideBar.addActionListener(e -> {
            discountPanelState = !discountPanelState;
            if(discountPanelState)
                add(panelDiscount,BorderLayout.EAST);
            else{
                remove(panelDiscount);
            }
            revalidate();
        });

        //Assembling components
        panelHeader.add(new Label("PROFIL: " + activeProfile.getName()));
        panelHeader.add(new JLabel("Vonalkód olvasó:"));
        panelHeader.add(cbSelectPort);
        panelHeader.add(lbDeviceActive);
        panelHeader.add(btnRefreshPorts);
        panelHeader.add(btnOpenSideBar);

        return panelHeader;
    }

    /**
     * Creates the body of the application
     * @return a JPanel containing the body components
     */
    private JPanel createBody(){
        JPanel panelBody = new JPanel();
        panelBody.setLayout(new BorderLayout());

        JTextField tfInputField = new JTextField(32);
        entryView = new JTable();
        entryView.getTableHeader().setReorderingAllowed(false);
        controller.setTable(entryView);

        JButton btnSendCommand = new JButton("Küldés");
        btnSendCommand.addActionListener(e -> {
            if(!tfInputField.getText().isEmpty()) {
                controller.receiveCode(tfInputField.getText());
            }
        });

        JScrollPane spTable = new JScrollPane(entryView);
        spTable.setVerticalScrollBar(spTable.createVerticalScrollBar());
        spTable.setWheelScrollingEnabled(true);

        //Assembling body components
        JPanel panelBodySearchBar = new JPanel();   //This is the search bar in the bottom
        panelBodySearchBar.add(tfInputField);
        panelBodySearchBar.add(btnSendCommand);
        panelBody.add(panelBodySearchBar,BorderLayout.SOUTH);
        panelBody.add(spTable,BorderLayout.CENTER);

        return panelBody;
    }

    /**
     * Refreshes the available ports from the system
     */
    private void eventRefreshPorts() {
        SerialPort ports[] = SerialPort.getCommPorts();
        cbSelectPort.removeAllItems();
        for (SerialPort port : ports) {
            cbSelectPort.addItem(port.getSystemPortName());
        }
        if (cbSelectPort.getItemCount() > 0) {
            onClickBtnRefreshPorts(controller.portIsActive());
        } else {
            onClickBtnRefreshPorts(false);
            cbSelectPort.addItem(DEFAULT_OPTION);
        }
        pack();
    }

    /*
    ================= INHERITED FUNCTIONS =================
     */

    /**
     * Inherited from ProgramListener
     * Changes the program's save state if the content changes
     * @param stateChanged the program's new save state
     * @param headerName the application's name appearing on the header
     */
    @Override
    public void stateChanged(boolean stateChanged, String headerName) {
        if(stateChanged)
            setTitle(headerName + "*");
        else
            setTitle(headerName);
    }

    /**
     * Inherited from ProgramStateListener
     * Creates a new file and clears all data
     * Also used upon startup
     */
    @Override
    public void renewState() {
        InfoPanel infoPanel = new InfoPanel("");
        controller = new EntryController(activeProfile,profiles.stream().map(EntryProfile::getName).toArray(),infoPanel);
        controller.addProgramStateListener(this);
        controller.setTable(entryView);
        add(infoPanel,BorderLayout.SOUTH);
    }

    /**
     * Inherited from ProgramStateListener
     * Receives a string data from a BarCode reader
     * @param barCode the string data received
     */
    @Override
    public void readBarCode(String barCode) {
        controller.receiveCode(barCode);
    }

    @Override
    public void changeProfile(String profileName) {
        activeProfile = profiles.stream().filter(p -> p.getName().equals(profileName)).findAny().orElse(activeProfile);
        //TODO: Take renewState to consideration
    }

    /*
    ================= INNER CLASSES =================
     */

    /**
     * An information panel at the bottom of the screen.
     * It reacts to the barcode reading operations and behaves correctly.
     * Shows the current state of reading, with color codes.
     */
    private class InfoPanel extends JPanel implements ReadingFlagListener{

        private JLabel lbInfo;

        InfoPanel(String defaultString){
            setLayout(new BoxLayout(this,BoxLayout.LINE_AXIS));
            lbInfo = new JLabel(defaultString);
            lbInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

            add(Box.createGlue());
            add(lbInfo);
            add(Box.createGlue());
        }

        @Override
        public void flagChange(EntryController.readCodeFlag flag) {
            switch (flag){
                default:
                case FL_DEFAULT:
                    lbInfo.setText("Belépésre vár");
                    setBackground(Color.GREEN);
                    break;
                case FL_IS_LEAVING:
                    lbInfo.setText("Kilépésre vár");
                    setBackground(Color.YELLOW);
                    break;
                case FL_IS_DELETE:
                    lbInfo.setText("Törlésre vár");
                    setBackground(Color.RED);
                    break;
            }
        }
    }
}