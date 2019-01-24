package view.main;



import control.AppController;
import control.MenuHandler;
import data.AppData;

import javax.swing.*;
import java.awt.*;

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

    private JLabel labelProfile;
    private JLabel labelDevice;
    private JButton btnDiscounts;

    /**
     * Main Constructor
     * Builds the main window of the program
     *
     * @param model the model of the Application
     */
    public MainWindow(AppData model, AppController controller) {
        //Setting up default fields
        this.model = model;

        btnDiscounts = new JButton("Kedvezmények");

        labelProfile = new JLabel(controller.getProfileName());
        labelProfile.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 14));
        labelProfile.setForeground(new Color(0xcc8500));
        labelDevice = new JLabel("TEMP");
        labelDevice.setOpaque(true);
        labelDevice.setMaximumSize(new Dimension(labelDevice.getMaximumSize().width,btnDiscounts.getMaximumSize().height));

        //Create components
        setLayout(new BorderLayout());
        JPanel body = new JPanel();
        JPanel sidePanel = new JPanel();
        JPanel infoPanel = new JPanel();
        setJMenuBar(new MainMenu().createMenu(controller));

        //Assembling panels
        add(new Header(controller), BorderLayout.NORTH);
        add(sidePanel, BorderLayout.EAST);
        add(body, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);
    }

    private class Header extends JPanel{

        Header(AppController controller) {
            setLayout(new BoxLayout(this,BoxLayout.LINE_AXIS));
            //Profile Label
            add(new JLabel("Profil: "));
            add(labelProfile);
            add(Box.createHorizontalGlue());
            //Comm. Port chooser
            add(new JLabel("Vonalkód olvasó: "));
            JComboBox<String> cbPorts = new JComboBox<>();
            cbPorts.addItemListener(event -> controller.checkPort(event,labelDevice));
            add(cbPorts);
            add(labelDevice);
            JButton btnSelectPort = new JButton("Frissíts");
            btnSelectPort.addActionListener(e -> controller.scanPorts(cbPorts));
            add(btnSelectPort);
            add(Box.createHorizontalGlue());
            add(btnDiscounts);

            //Set default selection
            controller.scanPorts(cbPorts);
            cbPorts.setSelectedIndex(0);


        }
    }

    /**
     * An abstract class responsible for creating the Application's menu bar.
     * @author Márk Mihalovits
     */
    private class MainMenu {
        /**
         * Creates a default menu bar with an event handler
         * @return a default menu bar
         */
        JMenuBar createMenu(AppController controller){
            JMenuBar menuBar = new JMenuBar();

            MenuHandler handler = new MenuHandler(controller);

            //Assembling Menus
            menuBar.add(createFileMenu(handler));
            //menuBar.add(createEditMenu(controller));
            //menuBar.add(createChartsMenu(controller));
            menuBar.add(createProfileMenu(controller));

            return menuBar;
        }

        /**
         * Creates the FILE menu for the menu bar
         * @return the FILE menu
         */
        private JMenu createFileMenu(MenuHandler handler){
            JMenu fileMenu = new JMenu("Fájl");

            JMenuItem mi = new JMenuItem("Új Lista");
            mi.addActionListener(e -> handler.notImplemented());
            fileMenu.add(mi);

            mi = new JMenuItem("Állás betöltése");
            mi.addActionListener(e -> handler.notImplemented());
            fileMenu.add(mi);

            fileMenu.addSeparator();

            mi = new JMenuItem("Mentés");
            mi.addActionListener(e -> handler.notImplemented());
            fileMenu.add(mi);

            mi = new JMenuItem("Mentés másként");
            mi.addActionListener(e -> handler.notImplemented());
            fileMenu.add(mi);

            fileMenu.addSeparator();

            mi = new JMenuItem("Lista importálása");
            mi.addActionListener(e -> handler.notImplemented());
            fileMenu.add(mi);

            mi = new JMenuItem("Lista exportálása");
            mi.addActionListener(e -> handler.exportEntries());
            fileMenu.add(mi);

            fileMenu.addSeparator();

            mi = new JMenuItem("Kilépés");
            mi.addActionListener(e -> handler.notImplemented());
            fileMenu.add(mi);

            return fileMenu;
        }

        /**
         * Creates the PROFILE menu for the menu bar
         * @param controller the event handler handling the action events
         * @return the PROFILE menu
         */
        private JMenu createProfileMenu(AppController controller) {
            JMenu menuProfiles = new JMenu("Profilok");

            JMenuItem mi = new JMenuItem("Profil váltása");
            mi.addActionListener(e-> labelProfile.setText(controller.changeProfile()));
            menuProfiles.add(mi);


            return menuProfiles;
        }

        /**
         * Creates the CHARTS menu for the menu bar
         * TODO: waiting for implementation
         * @param handler the event handler handling the action events
         * @return the CHARTS menu
         */
        private JMenu createChartsMenu(AppController handler){
            return new JMenu("Statisztikák");
        }

        /**
         * Creates the EDIT menu for the menu bar
         * TODO: waiting for implementation
         * @param handler the event handler handling the action events
         * @return the EDIT menu
         */
        private JMenu createEditMenu(AppController handler){
            return new JMenu("Szerkesztés");
        }
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
//                String data = btnToggleInput.codeInput ? AppController.ENTRY_CODE : "";
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
//        //Creating new AppController
//        MenuHandler handler = null;
//        //If an event handler exists
//        if(controller != null) handler = controller.getDefaultEventHandler();
//        controller = new AppController(activeProfile,handler,profiles.stream().map(EntryProfile::getName).toArray(),infoPanel);
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
//    public AppController getController() {
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
//        public void flagChange(AppController.readCodeFlag flag) {
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