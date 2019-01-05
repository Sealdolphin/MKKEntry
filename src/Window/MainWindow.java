package Window;

import Control.EntryController;
import com.fazecast.jSerialComm.SerialPort;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static Control.EntryController.DEFAULT_OPTION;
import static javax.swing.BoxLayout.PAGE_AXIS;

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
    private JLabel lbdeviceActive = new JLabel();

    private JPanel panelSide;

    private boolean sideBar = false;

    /**
     * The EntryController
     * It is responsible for operative decisions
     */
    private EntryController controller;
    private JTable entryView;

    /**
     * Main Constructor
     * Builds the main window of the program
     */
    MainWindow(){
        //Creating EntryController
        controller = new EntryController();
        controller.addProgramStateListener(this);

        //Setting Layout for header and body
        setLayout(new BorderLayout());
        //Setting up default parameters
        setMinimumSize(new Dimension(640,200));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("MKK Beléptető rendszer");

        panelSide = createSideMenu();
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

    private JPanel createHeader(){
        //Header Components
        JPanel panelHeader = new JPanel();

        //Device state label
        lbdeviceActive.setOpaque(true);
        lbdeviceActive.setBackground(Color.RED);

        //Device refresher button
        JButton btnRefreshPorts = new JButton("Frissíts");
        btnRefreshPorts.addActionListener(e -> eventRefreshPorts());
        //Device Combo Box
        cbSelectPort.addItemListener(controller);
        //SideBar button
        JButton btnOpenSideBar = new JButton("Opciók");
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

    private JPanel createSideMenu(){
        JPanel panelSide = new JPanel();
        panelSide.setLayout(new BoxLayout(panelSide,PAGE_AXIS));
        BufferedImage barCode = null;
        try{
            barCode = ImageIO.read(new File("Barcodes\\foodSale.png"));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(new JFrame(),"Nem találom a vonalkódképet","Hiba",JOptionPane.ERROR_MESSAGE);
        }

        JLabel label = new JLabel("Nem hozott sütit vagy üdítőt");
        label.setFont(new Font(label.getFont().getName(),Font.PLAIN,20));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelSide.add(new ImagePanel(barCode));
        panelSide.add(label);


        return panelSide;
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

    @Override
    public void stateChanged(boolean stateChanged, String headerName) {
        if(stateChanged)
            setTitle(headerName + "*");
        else
            setTitle(headerName);
    }

    @Override
    public void renewState() {
        controller = new EntryController();
        controller.addProgramStateListener(this);
        controller.setTable(entryView);
    }

    @Override
    public void readBarCode(String barCode) {
        controller.receiveCode(barCode);
    }

    /**
     * An empty JPanel containing a custom image
     */
    private class ImagePanel extends JPanel {
        private BufferedImage image;

        ImagePanel(BufferedImage image){
            this.image = image;
            //Set layout to fill available space
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
            if(image != null) {
                //Adding Empty space to fill out image
                add(Box.createRigidArea(new Dimension(image.getWidth(), image.getHeight())));
                //Setting maximum height not to be infinity
                setMaximumSize(new Dimension(image.getWidth(), image.getHeight()));
            }
        }

        @Override
        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            g.drawImage(image,0,0,this);
        }

    }

}
