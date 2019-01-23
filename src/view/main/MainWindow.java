package view.main;



import control.EntryController;
import data.AppData;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import control.ProgramStateListener;

/**
 * The class of the main program window
 * It contains two panels: A header and a body
 * The header is responsible for the selection of the port / device
 * The Body is responsible for the guest list.
 * It also has a bottom info panel which displays the state of the reading
 * @author Márk Mihalovits
 */
public class MainWindow extends JFrame {
    /*
    FIELDS:
        LISTENERS (CONTROLLER / EVENT HANDLER)
     */
    private AppData model;

    /**
     * Main Constructor
     * Builds the main window of the program
     * @param model the model of the Application
     */
    public MainWindow(AppData model, EntryController controller) {
        //Setting up default fields
        this.model = model;

        //Create components
        setLayout(new BorderLayout());
        JPanel body = new JPanel();
        JPanel sidePanel = new JPanel();
        JPanel infoPanel = new JPanel();

        //Assembling panels
        add(new Header(controller),BorderLayout.NORTH);
        add(sidePanel,BorderLayout.EAST);
        add(body,BorderLayout.CENTER);
        add(infoPanel,BorderLayout.SOUTH);


    }

//
//    /**
//     * Loads the different profiles from the default json file
//     * @throws IOException if the reading fails
//     * @throws ParseException if cannot parse the file (damaged, or missing JSON property)
//     */
//    private void loadProfiles() throws IOException, ParseException {
//
//        profiles = new ArrayList<>();
//
//        JSONParser parser = new JSONParser();
//        JSONObject obj;
//        obj = (JSONObject) parser.parse(new BufferedReader(new InputStreamReader(new FileInputStream("profiles.json"))));
//
//    }
//
//    /**
//     * Activates or deactivates the label indicating the active state of the selected port / device
//     * @param active is the state of the port / device
//     */
//    private void onClickBtnRefreshPorts(boolean active) {
//        if(active){
//            lbDeviceActive.setText(uh.getUIStr("UI","PORT_ACTIVE"));
//            lbDeviceActive.setBackground(Color.green);
//        } else {
//            lbDeviceActive.setText(uh.getUIStr("UI","PORT_INACTIVE"));
//            lbDeviceActive.setBackground(Color.red);
//        }
//    }

//
//    /**
//     * Creates the body of the application
//     * @return a JPanel containing the body components
//     */
//    private JPanel createBody(){
//        JPanel panelBody = new JPanel();
//        panelBody.setLayout(new BorderLayout());
//
//        JTextField tfInputField = new JTextField("Ide írd a belépőkódot...",32);
//        tfInputField.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                tfInputField.setText("");
//            }
//        });
//
//        entryView = new JTable();
//        entryView.getTableHeader().setReorderingAllowed(false);
//        entryView.setSelectionMode(SINGLE_SELECTION);
//
//        controller.setTable(entryView);
//
//        ToggleInputButton btnToggleInput = new ToggleInputButton();
//
//        JButton btnSendCommand = new JButton(uh.getUIStr("UI","SENDCODE_BTN"));
//        btnSendCommand.addActionListener(e -> {
//            if(!tfInputField.getText().isEmpty()) {
//                String data = btnToggleInput.codeInput ? EntryController.ENTRY_CODE : "";
//                controller.receiveCode(data + tfInputField.getText());
//            }
//        });
//
//        JScrollPane spTable = new JScrollPane(entryView);
//        spTable.setVerticalScrollBar(spTable.createVerticalScrollBar());
//        spTable.setWheelScrollingEnabled(true);
//
//        //Assembling body components
//        JPanel panelBodySearchBar = new JPanel();   //This is the search bar in the bottom??
//        panelBodySearchBar.add(btnToggleInput);
//        panelBodySearchBar.add(tfInputField);
//        panelBodySearchBar.add(btnSendCommand);
//        panelBody.add(panelBodySearchBar,BorderLayout.SOUTH);
//        panelBody.add(spTable,BorderLayout.CENTER);
//
//        return panelBody;
//    }
//
//    /**
//     * Refreshes the available ports from the system
//     */
//    private void eventRefreshPorts() {
//        SerialPort ports[] = SerialPort.getCommPorts();
//        cbSelectPort.removeAllItems();
//        for (SerialPort port : ports) {
//            cbSelectPort.addItem(port.getSystemPortName());
//        }
//        if (cbSelectPort.getItemCount() > 0) {
//            onClickBtnRefreshPorts(controller.portIsActive());
//        } else {
//            onClickBtnRefreshPorts(false);
//            cbSelectPort.addItem(DEFAULT_OPTION);
//        }
//        pack();
//    }
//
//    /*
//    ================= INHERITED FUNCTIONS =================
//     */
//
//    /**
//     * Inherited from ProgramListener
//     * Changes the program's save state if the content changes
//     * @param stateChanged the program's new save state
//     * @param headerName the application's name appearing on the header
//     */
//    @Override
//    public void stateChanged(boolean stateChanged, String headerName) {
//        if(stateChanged)
//            setTitle(headerName + "*");
//        else
//            setTitle(headerName);
//    }
//
//    /**
//     * Inherited from ProgramStateListener
//     * Creates a new file and clears all data
//     * Also used upon startup
//     */
//    @Override
//    public void renewState() {
//        //Creating new infoPanel
//        InfoPanel infoPanel = new InfoPanel("");
//        //Creating new EntryController
//        EventHandler handler = null;
//        //If an event handler exists
//        if(controller != null) handler = controller.getDefaultEventHandler();
//        controller = new EntryController(activeProfile,handler,profiles.stream().map(EntryProfile::getName).toArray(),infoPanel);
//        controller.setProgramStateListener(this);
//        controller.setTable(entryView);
//        //Creating new layout / menu
//        lbProfile.setText(uh.getUIStr("UI","PROFILE_LB") + ": " + activeProfile.getName());
//        add(infoPanel,BorderLayout.SOUTH);
//    }
//
//    /**
//     * Inherited from ProgramStateListener
//     * Receives a string data from a BarCode reader
//     * @param barCode the string data received
//     */
//    @Override
//    public void readBarCode(String barCode) {
//        controller.receiveCode(barCode);
//    }
//
//    @Override
//    public void changeProfile(String profileName) {
//        activeProfile = profiles.stream().filter(p -> p.getName().equals(profileName)).findAny().orElse(activeProfile);
//        //Invoke renew state. Clear Database and create new Controller
//        renewState();
//        JOptionPane.showMessageDialog(new JFrame(), uh.getUIStr("MSG","PROFILE_CHANGED") + "\n"+
//                "Új profil: " + activeProfile.getName(),"Kész",JOptionPane.INFORMATION_MESSAGE);
//    }
//
//    @Override
//    public EntryController getController() {
//        return controller;
//    }
//
//    @Override
//    public EntryProfile getProfile(){return activeProfile;}
//
//    /*
//    ================= INNER CLASSES =================
//     */
//
//    /**
//     * An information panel at the bottom of the screen.
//     * It reacts to the barcode reading operations and behaves correctly.
//     * Shows the current state of reading, with color codes.
//     */
//    private class InfoPanel extends JPanel implements ReadingFlagListener{
//
//        private JLabel lbInfo;
//
//        InfoPanel(String defaultString){
//            setLayout(new BoxLayout(this,BoxLayout.LINE_AXIS));
//            lbInfo = new JLabel(defaultString);
//            lbInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
//
//            add(Box.createGlue());
//            add(lbInfo);
//            add(Box.createGlue());
//        }
//
//        @Override
//        public void flagChange(EntryController.readCodeFlag flag) {
//            switch (flag){
//                default:
//                case FL_DEFAULT:
//                    lbInfo.setText(Main.uh.getUIStr("UI","BOTTOM_INFO_DEF"));
//                    setBackground(Color.GREEN);
//                    break;
//                case FL_IS_LEAVING:
//                    lbInfo.setText(Main.uh.getUIStr("UI","BOTTOM_INFO_MOD"));
//                    setBackground(Color.YELLOW);
//                    break;
//                case FL_IS_DELETE:
//                    lbInfo.setText(Main.uh.getUIStr("UI","BOTTOM_INFO_DEL"));
//                    setBackground(Color.RED);
//                    break;
//            }
//        }
//    }
//
//    private class ToggleInputButton extends JButton implements ActionListener {
//
//        private boolean codeInput;
//
//        ToggleInputButton(){
//            super(Main.uh.getUIStr("UI","TOGGLECODE_BTN_1"));
//            codeInput = true;
//            addActionListener(this);
//        }
//
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            codeInput = !codeInput;
//            if(codeInput)
//                setText(Main.uh.getUIStr("UI","TOGGLECODE_BTN_1"));
//            else
//                setText(Main.uh.getUIStr("UI","TOGGLECODE_BTN_2"));
//        }
//    }
}