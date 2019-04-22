package view.main;



import control.AppController;
import control.MenuHandler;
import control.modifier.Discount;
import data.AppData;
import data.EntryProfile;
import view.DiscountRenderer;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import static control.Application.uh;
import static java.awt.event.KeyEvent.VK_DELETE;
import static java.awt.event.KeyEvent.VK_ENTER;
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
    private JButton btnBarcodes;
    private JScrollPane sidePanel;
    private JTable entryView;

    private boolean discountPanelStatus = false;

    private Runnable selectionUpdate = () -> {
        int viewIndex = model.getSelectedIndex();
        int index;
        try {
            index = entryView.convertRowIndexToView(viewIndex);
        } catch (IndexOutOfBoundsException ex){
            index = -1;
        }
        System.out.println("SELECTION: " + model.getSelectedData() + " at index " + viewIndex + " ("+index+" in view)");
        if(index >= 0) {
            entryView.changeSelection(index, 0, false, false);
        }
    };

    /**
     * Main Constructor
     * Builds the main window of the program
     *
     * @param model the model of the Application
     */
    public MainWindow(AppData model, AppController controller, boolean admin) {
        //Setting up default fields
        this.model = model;
        setTitle(admin ? " (ADMIN MÓD)" : "");
        //Discount panel toggle button
        btnBarcodes = new JButton("Vonalkódok");
        btnBarcodes.addActionListener(e -> {
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
        labelDevice.setMaximumSize(new Dimension(labelDevice.getMaximumSize().width, btnBarcodes.getPreferredSize().height));

        //Create components
        setLayout(new BorderLayout());
        InfoPanel infoPanel = new InfoPanel();
        controller.addListener(infoPanel);
        setJMenuBar(new MainMenu().createMenu(controller,admin));
        //Create popup-menu (TEMP)
        JPopupMenu popupEditRecord = createPopUpMenu(controller);

        //Create Entry Table
        entryView = new JTable(model);
        //Render discounts differently
        entryView.setDefaultRenderer(Discount.class,new DiscountRenderer(24));
        //For the icons
        entryView.setRowHeight(48);
        entryView.getTableHeader().setReorderingAllowed(false);
        entryView.setSelectionMode(SINGLE_SELECTION);
        entryView.setRowSelectionAllowed(true);
        entryView.setColumnSelectionAllowed(false); //Selecting a column does not make sense anyway
        entryView.createDefaultColumnsFromModel();
        //Add selection changer (for live action selecting)
        entryView.getSelectionModel().addListSelectionListener(e -> {
            if((entryView.getSelectedRow() >= 0)){
                model.setSelection(model.getDataByIndex(entryView.convertRowIndexToModel(entryView.getSelectedRow())));
            }
        });
        if(admin)
            //Add right-click popup menu
            entryView.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    maybeShowPopup(e);
                }

                public void mouseReleased(MouseEvent e) {
                    maybeShowPopup(e);
                }

                private void maybeShowPopup(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        popupEditRecord.show(e.getComponent(), e.getX(), e.getY());
                        entryView.changeSelection(entryView.rowAtPoint(e.getPoint()),0,false,false);
                    }
                }
            });
        //Change model selection in consistency with the view's selection model
        model.addTableModelListener(e -> {
            if(e.getType() == TableModelEvent.UPDATE) {
                selectionUpdate.run();
            }
        });
        initiateView(controller);

        //Assembling panels
        add(new Header(controller), BorderLayout.NORTH);
        add(new Body(controller), BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);

    }

    /**
     * Creates a helper menu for the user with default record actions
     * - Enter code
     * - Leave code
     * - Delete code
     * - Modify discount
     * @return a popup menu
     */
    private JPopupMenu createPopUpMenu(AppController controller) {
        JPopupMenu popupEditRecord = new JPopupMenu();
        JMenuItem miEnter = new JMenuItem("Beléptetés");
        JMenuItem miLeave = new JMenuItem("Kiléptetés");
        JMenuItem miDelete = new JMenuItem("Törlés");
        JMenuItem miDiscounts = new JMenuItem("Kedvezmények módosítása");
        JMenuItem miReset = new JMenuItem("Rekord visszaállítása");

        miEnter.addActionListener(e -> controller.flagOperationOnEntry(AppController.ReadingFlag.FL_DEFAULT,model.getSelectedData()));
        miLeave.addActionListener(e -> controller.flagOperationOnEntry(AppController.ReadingFlag.FL_IS_LEAVING,model.getSelectedData()));
        miDelete.addActionListener(e -> controller.flagOperationOnEntry(AppController.ReadingFlag.FL_IS_DELETE,model.getSelectedData()));
        miDiscounts.addActionListener(e -> controller.discountOperationOnEntry(model.getSelectedData()));
        miReset.addActionListener(e -> controller.resetEntry(model.getSelectedData()));

        popupEditRecord.add(miEnter);
        popupEditRecord.add(miLeave);
        popupEditRecord.add(miDelete);
        popupEditRecord.add(miDiscounts);
        popupEditRecord.add(miReset);
        return popupEditRecord;
    }

    private void initiateView(AppController controller) {
        //Refresh side panel
        discountPanelStatus = false;
        //Clear and refresh JTable
        if(sidePanel != null) remove(sidePanel);
        sidePanel = new JScrollPane(controller.getSidePanel());
        selectionUpdate.run();
        revalidate();
    }

    private class Header extends JPanel{

        Header(AppController controller) {
            setLayout(new BoxLayout(this,BoxLayout.LINE_AXIS));
            //Profile Label
            add(new JLabel("Profil: "));
            add(labelProfile);
            JButton btnNet = new NetworkButton();
            //add(btnNet);  //TODO: Online mode access
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
            add(btnBarcodes);

            //Set default selection
            controller.scanPorts(cbPorts);
            cbPorts.setSelectedIndex(0);

            //Setup Online mode
            btnNet.addActionListener(e -> controller.switchOnlineMode());
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
        JMenuBar createMenu(AppController controller, boolean admin){
            JMenuBar menuBar = new JMenuBar();

            MenuHandler handler = new MenuHandler(controller);

            //Assembling Menus
            menuBar.add(createFileMenu(handler));
            //menuBar.add(createEditMenu(controller));
            if(admin) {
                menuBar.add(createSettingsMenu(controller));
                menuBar.add(createChartsMenu(controller));
            }
            menuBar.add(new JMenu("Tranzakciók"));

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
            mi.addActionListener(e -> handler.importEntries());
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
        private JMenu createSettingsMenu(AppController controller) {
            JMenu menuProfiles = new JMenu("Beállítások");

            JMenuItem mi = new JMenuItem("Új profil létrehozása");
            mi.addActionListener(e-> {
                try {
                    EntryProfile newProfile = EntryProfile.createProfileFromWizard(MainWindow.this, null);
                    controller.addProfile(newProfile);
                } catch (IOException ex){
                    JOptionPane.showMessageDialog(null,"HIBA: " + ex.getMessage(),uh.getUIStr("ERR","HEADER"),JOptionPane.ERROR_MESSAGE);
                }
            });
            menuProfiles.add(mi);
            mi = new JMenuItem("Profil szerkesztése");
            mi.addActionListener(e -> {
                controller.editProfile(MainWindow.this,labelProfile);
                initiateView(controller);
            });
            menuProfiles.add(mi);

            mi = new JMenuItem("Profil váltása");
            mi.addActionListener(e-> {
                labelProfile.setText(controller.changeProfile(controller.chooseProfile()));
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
            JMenu menuStats = new JMenu("Statisztikák");
            JMenuItem miStats = new JMenuItem("Statisztikák megtekintése");
            miStats.addActionListener(e -> handler.createStatistics());
            menuStats.add(miStats);
            return menuStats;
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

            JTextField tfInputCode = new JTextField(32);
            JCheckBox checkBoxCode = new JCheckBox("Belépőkód küldése");

            Action deleteAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    controller.setReadingFlag(AppController.ReadingFlag.FL_IS_DELETE);
                }
            };

            Action actionSendCode = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(checkBoxCode.isSelected())
                        controller.readEntryCode(tfInputCode.getText());
                    else controller.receiveBarCode(tfInputCode.getText());
                }
            };

            //Code input
            JButton btnDelete = new JButton("Töröl");
            btnDelete.setForeground(Color.RED);
            btnDelete.setBackground(new Color(0xff6666));
            btnDelete.addActionListener(deleteAction);
            JButton btnLeave = new JButton("Kiléptet");
            btnLeave.addActionListener(e -> controller.setReadingFlag(AppController.ReadingFlag.FL_IS_LEAVING));

            JButton btnSendCode = new JButton("Olvas");

            btnSendCode.addActionListener(actionSendCode);
            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(VK_ENTER,0),"sendCode");
            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(VK_DELETE,0),"deleteCode");
            getActionMap().put("sendCode",actionSendCode);
            getActionMap().put("deleteCode",deleteAction);



            JPanel inputPanel = new JPanel();
            inputPanel.add(btnDelete);
            inputPanel.add(btnLeave);
            inputPanel.add(tfInputCode);
            inputPanel.add(checkBoxCode);
            inputPanel.add(btnSendCode);



            //Assembling body components
            add(spTable,BorderLayout.CENTER);
            add(inputPanel,BorderLayout.SOUTH);
            add(new QuickSearchHeader(model,entryView),BorderLayout.NORTH);


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
            lbInfo.setText(flag.getInfo());
            setBackground(flag.getColor());
            lbInfo.setForeground(flag.getTextColor());
        }
    }

}