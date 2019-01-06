package Window;

import Control.EntryController;
import Control.EntryProfile;
import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;
import java.awt.*;

import static Control.EntryController.DEFAULT_OPTION;

/**
 * The class of the main program window
 * It contains two panels: A header and a body
 * The header is responsible for the selection of the port / device
 * The Body is responsible for the guest list.
 */
public class MainWindow extends JFrame implements ProgramStateListener{

    /**
     * The list of the selectable Serial Ports in a combo box
     */
    private JComboBox<String> cbSelectPort = new JComboBox<>();
    /**
     * A label indicating whether the selected port / device is active
     */
    private JLabel lbdeviceActive;
    private JPanel panelSide;
    private JTable entryView;

    private EntryProfile profile;

    private boolean sideBar = false;

    /**
     * The EntryController
     * It is responsible for operative decisions
     */
    private EntryController controller;


    /**
     * Main Constructor
     * Builds the main window of the program
     */
    MainWindow(){
        //Loading profile TODO: reload profile etc.
        profile = new EntryProfile();

        //Creating EntryController
        controller = new EntryController(profile);
        controller.addProgramStateListener(this);

        //Setting Layout for header and body
        setLayout(new BorderLayout());
        //Setting up default parameters
        setMinimumSize(new Dimension(640,200));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("MKK Beléptető rendszer");

        panelSide = profile.createSideMenu();
        add(createHeader(),BorderLayout.NORTH);
        add(createBody(),BorderLayout.CENTER);
        setJMenuBar(MainMenu.createMenu(controller.getDefaultEventHandler()));

        //Running basic event routines
        eventRefreshPorts();

        //Pack window
        pack();

    }

    /**
     * Activates or deactivates the label indicating the active state of the selected port / device
     * @param active is the state of the port / device
     */
    private void activateButton(boolean active) {
        if(active){
            lbdeviceActive.setText("Aktív");
            lbdeviceActive.setBackground(Color.green);
        } else {
            lbdeviceActive.setText("Inaktív");
            lbdeviceActive.setBackground(Color.red);
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
        lbdeviceActive = new JLabel();
        lbdeviceActive.setOpaque(true);
        lbdeviceActive.setBackground(Color.RED);

        //Device refresher button
        JButton btnRefreshPorts = new JButton("Frissíts");
        btnRefreshPorts.addActionListener(e -> eventRefreshPorts());
        //Device Combo Box
        cbSelectPort.addItemListener(controller);
        //SideBar button
        JButton btnOpenSideBar = new JButton("Kedvezmények");
        //Adding sidePanel
        btnOpenSideBar.addActionListener(e -> {
            sideBar = !sideBar;
            if(sideBar)
                add(panelSide,BorderLayout.EAST);
            else{
                remove(panelSide);
            }
            revalidate();
        });

        //Assembling components
        panelHeader.add(new JLabel("Vonalkód olvasó:"));
        panelHeader.add(cbSelectPort);
        panelHeader.add(lbdeviceActive);
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
            activateButton(controller.portIsActive());
        } else {
            activateButton(false);
            cbSelectPort.addItem(DEFAULT_OPTION);
        }
        pack();
    }

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
     */
    @Override
    public void renewState() {
        controller = new EntryController(profile);
        controller.addProgramStateListener(this);
        controller.setTable(entryView);
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


}
