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
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import static java.awt.event.KeyEvent.VK_DELETE;
import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.KeyEvent.VK_SPACE;
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
        //Refresh side panel
        discountPanelStatus = false;
        //Clear and refresh JTable
        if(sidePanel != null) remove(sidePanel);
        sidePanel = controller.getSidePanel();
        entryView = new JTable(model);
        entryView.setDefaultRenderer(Discount.class,new DiscountRenderer(16));
        entryView.setRowHeight(32);
        entryView.getTableHeader().setReorderingAllowed(false);
        entryView.setSelectionMode(SINGLE_SELECTION);
        entryView.setRowSelectionAllowed(true);
        entryView.setColumnSelectionAllowed(false);
        entryView.createDefaultColumnsFromModel();
        entryView.getSelectionModel().addListSelectionListener(e -> {
            if((entryView.getSelectedRow() >= 0)){
                model.setSelection(model.getDataByIndex(entryView.getSelectedRow()));
            }
        });

        Runnable selectionUpdate = () -> {
            int index = model.getSelectedIndex();
            System.out.println("SELECTION: " + model.getSelectedData() + " at index " + index);
            if(index >= 0) {
                entryView.changeSelection(index, 0, false, false);
            }
        };
        model.addTableModelListener(e -> {
            if(e.getType() == TableModelEvent.UPDATE) {
                selectionUpdate.run();
            }
        });
        selectionUpdate.run();


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
                    else controller.readBarCode(tfInputCode.getText());
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