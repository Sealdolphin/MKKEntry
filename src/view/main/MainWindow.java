package view.main;



import control.AppController;
import control.MenuHandler;
import control.modifier.Discount;
import data.AppData;
import view.DiscountRenderer;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumn;
import java.awt.*;

import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

/**
 * The class of the main program window
 * It contains two panels: A header and a body
 * The header is responsible for the selection of the port / device
 * The Body is responsible for the guest list.
 * It also has a bottom info panel which displays the state of the reading
 * @author Márk Mihalovits
 */
public class MainWindow extends JFrame {

    private AppData model;

    private JLabel labelProfile;
    private JLabel labelDevice;
    private JButton btnDiscounts;
    private JPanel sidePanel;
    private JTable entryView;

    private boolean discountPanelStatus = false;

    /**
     * Main Constructor
     * Builds the main window of the program
     *
     * @param model the model of the Application
     */
    public MainWindow(AppData model, AppController controller) {
        //Setting up default fields
        this.model = model;
        //Discount panel toggle button
        btnDiscounts = new JButton("Kedvezmények");
        btnDiscounts.addActionListener(e -> {
            discountPanelStatus = !discountPanelStatus;
            if(discountPanelStatus)
                add(sidePanel,BorderLayout.EAST);
            else
                remove(sidePanel);
            revalidate();
        });
        //Active profile label
        labelProfile = new JLabel(controller.getProfileName());
        labelProfile.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 14));
        labelProfile.setForeground(new Color(0xc08500));
        //Device status label
        labelDevice = new JLabel("TEMP");
        labelDevice.setOpaque(true);
        labelDevice.setMaximumSize(new Dimension(labelDevice.getMaximumSize().width,btnDiscounts.getPreferredSize().height));

        //Create components
        setLayout(new BorderLayout());
        InfoPanel infoPanel = new InfoPanel();
        controller.addListener(infoPanel);
        setJMenuBar(new MainMenu().createMenu(controller));
        initiateView(controller);

        //Assembling panels
        add(new Header(controller), BorderLayout.NORTH);
        add(new Body(controller), BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);

    }

    private void initiateView(AppController controller) {
        discountPanelStatus = false;
        if(sidePanel != null) remove(sidePanel);
        sidePanel = controller.getSidePanel();
        entryView = new JTable(model);
        entryView.setDefaultRenderer(Discount.class,new DiscountRenderer(16));
        entryView.setRowHeight(32);
        entryView.getTableHeader().setReorderingAllowed(false);
        entryView.setSelectionMode(SINGLE_SELECTION);
        entryView.createDefaultColumnsFromModel();
        entryView.getSelectionModel().addListSelectionListener(e -> {
            if(!(entryView.getSelectedRow() == -1)){
                model.setSelection(model.getDataByIndex(entryView.getSelectedRow()));
            }
        });
        model.addTableModelListener(e -> {
            if(e.getType() == TableModelEvent.UPDATE && e.getLastRow() < entryView.getRowCount()) {
                entryView.setRowSelectionInterval(e.getLastRow(), 0);
            }
        });

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
            mi.addActionListener(e-> {
                labelProfile.setText(controller.changeProfile());
                initiateView(controller);
            });
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

    private class Body extends JPanel {

        Body(AppController controller) {
            setLayout(new BorderLayout());

            //Table
            JScrollPane spTable = new JScrollPane(entryView);
            spTable.setVerticalScrollBar(spTable.createVerticalScrollBar());
            spTable.setWheelScrollingEnabled(true);



            //Code input
            JButton btnDelete = new JButton("Delete");
            btnDelete.addActionListener(e -> controller.setReadingFlag(AppController.ReadingFlag.FL_IS_DELETE));
            JButton btnLeave = new JButton("Leave");
            btnLeave.addActionListener(e -> controller.setReadingFlag(AppController.ReadingFlag.FL_IS_LEAVING));
            JTextField tfInputCode = new JTextField(32);
            JButton btnSendCode = new JButton("Send");
            btnSendCode.addActionListener(e -> controller.readBarCode(tfInputCode.getText()));

//            //TEMPORARY#####
//            JPanel tempPanel = new JPanel();
//            tempPanel.setLayout(new BoxLayout(tempPanel,BoxLayout.PAGE_AXIS));
//            //TEMP DISCOUNTS
//            JLabel[] labels = new JLabel[]{
//                    new JLabel("first", new ImageIcon(new ImageIcon("Icons\\DIS_HELP.png").getImage().getScaledInstance(32,32,Image.SCALE_SMOOTH)),SwingConstants.CENTER),
//                    new JLabel("second", new ImageIcon(new ImageIcon("Icons\\DIS_PIE.png").getImage().getScaledInstance(32,32,Image.SCALE_SMOOTH)),SwingConstants.CENTER)
//            };
//            JList<JLabel> disList = new JList<>(labels);
//            disList.setCellRenderer(new CustomLabelRenderer());
//            disList.setLayoutOrientation(JList.VERTICAL_WRAP);
//            disList.setVisibleRowCount(1);
//            disList.setMaximumSize(new Dimension(getMaximumSize().width,disList.getMaximumSize().height));
//            tempPanel.add(disList);
//            //TEMPORARY#####

            JPanel inputPanel = new JPanel();
            inputPanel.add(btnDelete);
            inputPanel.add(btnLeave);
            inputPanel.add(tfInputCode);
            inputPanel.add(btnSendCode);

            //Assembling body components
            add(spTable,BorderLayout.CENTER);
            add(inputPanel,BorderLayout.SOUTH);

        }
    }

    /**
     * An information panel at the bottom of the screen.
     * It reacts to the barcode reading operations and behaves correctly.
     * Shows the current state of reading, with color codes.
     */
    private class InfoPanel extends JPanel implements ReadFlagListener{

        private JLabel lbInfo;

        InfoPanel(){
            setLayout(new BoxLayout(this,BoxLayout.LINE_AXIS));
            lbInfo = new JLabel("Hello Beléptető rendszer!");
            lbInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
            lbInfo.setFont(new Font("Arial", Font.BOLD,15));

            add(Box.createGlue());
            add(lbInfo);
            add(Box.createGlue());
            readingFlagChanged(AppController.ReadingFlag.FL_DEFAULT);
        }

        @Override
        public void readingFlagChanged(AppController.ReadingFlag flag){
            System.out.println("FLAG READ: " + flag.toString());
            String msg;
            Color bgColor;
            switch (flag) {
                default:
                case FL_DEFAULT:
                    msg = "Belépésre vár";
                    bgColor = Color.GREEN;
                    break;
                case FL_IS_LEAVING:
                    msg = "Kilépésre vár";
                    bgColor = Color.YELLOW;
                    break;
                case FL_IS_DELETE:
                    msg = "Törlésre vár";
                    bgColor = Color.RED;
                    break;
            }
            lbInfo.setText(msg);
            setBackground(bgColor);
        }
    }

//
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