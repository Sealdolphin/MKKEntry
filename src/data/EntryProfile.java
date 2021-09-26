package data;


import static control.AppController.ReadingFlag.FL_DEFAULT;
import static control.AppController.ReadingFlag.FL_IS_DELETE;
import static control.AppController.ReadingFlag.FL_IS_LEAVING;
import static control.Application.uh;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.SOUTH;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import javax.swing.*;

import control.modifier.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import control.AppController;
import control.UIHandler;
import view.ModifierValidationRenderer;

/**
 * Beléptetési profil.
 * A beléptetési profil jellemzi a program aktuális működését.
 * A profilhoz tartozó adatok:
 * - A profil neve
 * - Különböző jegytípusok
 * - Különböző kedvezmények
 * - A jellemző belépési kód maszkja
 * - Exportálási filterek (adatokra vonatkozik)
 * - Az eseményt jellemző egyéb konvenciók:
 * -- Belépési kvóta (hányszor léphet be egy vendég)
 * -- Ismeretlen adat kezelése (Automatikus, vagy egyéni)
 * -- A szükséges parancskódok listája
 * A beléptetési profil létrehozása csak varázsló segítségével lehetséges
 */
public class EntryProfile implements Serializable {

    private String password = "";

    @Deprecated
    public String getPassword() {
        return password;
    }

    public enum EntryLimit {
        ONCE("Egyszeri"),
        NO_LIMIT("Korlátlan"),
        CUSTOM("Egyéni");

        private final String name;

        EntryLimit(String name){this.name = name;}
        @Override
        public String toString(){ return name; }
    }

    /**
     * A beléptetési profil neve
     */
    private String name;

    /**
     * A rekordok automatikus neve
     */
    private String defaultName;

    /**
     * A profilhoz tartozó belépési kódok maszkja
     */
    private String codeMask;
    /**
     * A belépési kvóta.
     * 0 = nincs kvóta
     */
    private int entryLimit;
    /**
     * Ismeretlen adat kezelésének megoldása.
     * true = Automatikus kezelés. Az adatot figyelmen kívül hagyja, vagy az alapméretezett beállításokkal dolgozik.
     * false = Egyéni kezelés. A felhasználó dönti el
     */
    private boolean autoDataHandling;
    /**
     * Az exportálási filterek listája
     */
    private List exportFilters;
    /**
     * A jegytípusok listája
     */
    private final List<TicketType> ticketTypes;
    /**
     * Az alapméretezett jegytípus
     */
    private TicketType defaultType;
    /**
     * A kedvezmények listája
     */
    private final List<Discount> discounts;

    /**
     * A vonalkódok listája
     */
    private final List<Barcode> barCodes;

    /**
     * Kötelező-e a név megadása
     */
    private boolean nameRequirement;

    /**
     * Belépéskor új ID-t kap a kijelölt rekord
     */
    private boolean entryModifiesID;

    /**
     * A lecserélendő ID-k maszkja
     */
    private String modificationMask;

    /**
     * A szükséges parancskódok listája
     */
    private HashMap<String, AppController.ReadingFlag> commandCodes;

    private static final AppController.ReadingFlag[] commandJsonKeys = AppController.ReadingFlag.values();


    private EntryProfile() {
        ticketTypes = new ArrayList<>();
        discounts = new ArrayList<>();
        commandCodes = new HashMap<>();
        barCodes = new ArrayList<>();
        nameRequirement = true;
        entryModifiesID = false;
        defaultName = "Külsős jegy";
    }

    public EntryProfile(EntryProfile other){
        //Reference / primitives
        name = other.name;
        codeMask = other.codeMask;
        modificationMask = other.modificationMask;
        entryLimit = other.entryLimit;
        autoDataHandling = other.autoDataHandling;
        defaultType = other.defaultType;
        commandCodes = other.commandCodes;
        password = other.password;
        nameRequirement = other.nameRequirement;
        entryModifiesID = other.entryModifiesID;
        defaultName = other.defaultName;
        ticketTypes = new ArrayList<>();
        discounts = new ArrayList<>();
        barCodes = new ArrayList<>();
        for (Barcode barcode : other.barCodes) { barCodes.add(new Barcode(barcode)); }
        for (Discount discount : other.discounts) { discounts.add(new Discount(discount,this)); }
        for (TicketType type : other.ticketTypes) { ticketTypes.add(new TicketType(type)); }
        exportFilters = other.exportFilters;
    }


    public static void loadProfilesFromJson(JSONObject object, List<EntryProfile> profileList) throws IOException{
        if(!object.get("version").toString().equals(UIHandler.uiVersion))
            throw new IOException(uh.getUIStr("ERR","VERSION_MISMATCH") + UIHandler.uiVersion);

        try {
            JSONArray jsonProfiles = (JSONArray) object.get("profiles");
            for (Object profileObj : jsonProfiles) {
                profileList.add(parseProfileFromJson((JSONObject) profileObj));
            }
        } catch (Exception e){
            throw new IOException(uh.getUIStr("ERR","PROFILE_DATA_PARSE") + "\n" + e);
        }
    }

    public static EntryProfile createProfileFromWizard(JFrame main, EntryProfile edit){
        if(edit == null)
            edit = new EntryProfile();
        ProfileWizard wizard = edit.getWizardEditor(main);
        int res = wizard.open();
        return res == 0 ? edit : null;
    }

    public static boolean isRestartNeeded(EntryProfile oldProfile, EntryProfile newProfile) {
        //1. code mask comparison
        if(!oldProfile.codeMask.equals(newProfile.codeMask)) return true;
        //2. Barcode list comparision
        for (Barcode barcode : oldProfile.barCodes) {
            if(!newProfile.barCodes.contains(barcode)) return true;
        }
        //3. Discount comparision
        for (Discount discount : oldProfile.discounts) {
            if(!newProfile.discounts.contains(discount)) return true;
        }
        //4. TicketTypes
        for (TicketType ticketType : oldProfile.ticketTypes) {
            if(!newProfile.ticketTypes.contains(ticketType)) return true;
        }

        return false;
    }

    private static EntryProfile parseProfileFromJson(JSONObject jsonProfile) {
        //Loading basic information
        EntryProfile profile = new EntryProfile();

        profile.name = jsonProfile.get("name").toString();
        profile.codeMask = jsonProfile.get("mask").toString();
        profile.modificationMask = jsonProfile.get("modificationMask").toString();
        profile.entryModifiesID = !Objects.equals(profile.modificationMask, "");

        //Loading Barcodes
        JSONArray jArray = (JSONArray) jsonProfile.get("barCodes");
        for (Object barCodeObject : jArray) {
            profile.barCodes.add(Barcode.parseBarcodeFromJSON((JSONObject) barCodeObject));
        }

        //Loading discounts
        jArray = (JSONArray) jsonProfile.get("discounts");
        for (Object discountObject : jArray) {
            profile.discounts.add(Discount.parseDiscountFromJson((JSONObject) discountObject, profile));
        }
        //Loading Ticket types
        jArray = (JSONArray) jsonProfile.get("tickets");
        for (Object discountObject : jArray) {
            profile.ticketTypes.add(TicketType.parseTicketTypeFromJson((JSONObject) discountObject, profile.name));
        }

        //Setting the default ticket type
        String defType = jsonProfile.get("defaultType").toString();
        profile.defaultType = profile.ticketTypes.stream().filter(ticketType -> ticketType.toString().equals(defType))
                .findAny().orElse(profile.ticketTypes.get(0));

        //Loading default commands
        HashMap<String, AppController.ReadingFlag> commands = new HashMap<>();
        for (AppController.ReadingFlag commandFlag : commandJsonKeys) {
            String key = ((JSONObject) jsonProfile.get("commands")).get(commandFlag.getMeta()).toString();
            commands.put(key, commandFlag);
        }
        profile.commandCodes = commands;

        return profile;
    }

    /**
     * Creates a side menu with additional attributes and options
     * @return a JPanel containing the sideBar components
     */
    public JPanel getSidePanel() {
        //Setting up layout
        for(Barcode barcode : barCodes)
            barcode.setLink(false);

        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel,BoxLayout.PAGE_AXIS));

        sidePanel.add(new JLabel("Kedvezmények:"));
        for (Discount discount : discounts) {
            //Adding image and image label
            sidePanel.add(discount.getBarcodePanel());
        }

        sidePanel.add(new JLabel("Parancskódok:"));
        for (Barcode barcode : barCodes) {
            if(!barcode.hasLink())
                sidePanel.add(barcode.createBarcodePanel());
        }

        return sidePanel;
    }

    public Discount identifyDiscountMeta(String discountMeta){
        return discounts.stream().filter(discount -> discount.getMeta().equals(discountMeta)).findAny().orElse(null);
    }

    TicketType identifyTicketType(String unknownType) {
        return ticketTypes.stream().filter(type -> type.toString().equals(unknownType)).findAny().orElse(defaultType);
    }

    public Barcode identifyBarcode(String barcodeMeta){
        return barCodes.stream().filter(barCode -> barCode.getMeta().equals(barcodeMeta)).findAny().orElse(null);
    }

    public String validateCode(String code) throws IOException{
        //Checking constraints
        String startCode = getEntryCode();
        String validID = code.replaceAll(startCode,"").toUpperCase().trim();
        if (!(validID.matches(codeMask) && code.contains(startCode))) throw new IOException(uh.getUIStr("ERR","CODE_MISMATCH"));
        return validID;
    }

    public AppController.ReadingFlag validateCommand(String code) {
        return commandCodes.get(code.toUpperCase());
    }

    public String getEntryCode(){
        return commandCodes.entrySet().stream().filter(c -> c.getValue().equals(FL_DEFAULT)).map(Map.Entry::getKey).findAny().orElse("");
    }

    @Override
    public String toString(){
        return name;
    }

    public Discount[] getDiscounts(){
        return discounts.toArray(new Discount[0]);
    }

    public Barcode[] getBarcodes(){
        return barCodes.toArray(new Barcode[0]);
    }

    public Entry generateNewEntry(String id) {
        String inputName = defaultName;
        if(nameRequirement) {
            inputName = JOptionPane.showInputDialog("Adj meg egy nevet!");
            if (inputName == null || inputName.isEmpty()) return null;
        }
        return new Entry(id,inputName,defaultType);
    }

    public Entry generateFromEntry(Entry entry, String id) {
        return new Entry(id, entry);
    }

    public boolean enteringModifiesEntry(String id) {
        return entryModifiesID && id.matches(modificationMask);
    }


    private ProfileWizard getWizardEditor(JFrame main) { return new ProfileWizard(main); }

    class ProfileWizard extends JDialog {

        private JTextField tfName;
        private JTextField tfMask;
        private JTextField tfModifyMask;
        private JTextField tfCommandDefault;
        private JTextField tfDefaultTicket;
        private JComboBox<Barcode> cbCommandLeave;
        private JComboBox<Barcode> cbCommandDelete;
        private JComboBox<EntryLimit> cbLimit;
        private JSpinner spEntryLimit;
        private JComboBox<TicketType> cbTypes;
        private final JCheckBox cbRequiredName = new JCheckBox("Név megadása kötelező");
        private final JCheckBox cbEntryModification = new JCheckBox("Belépéskor új ID kiosztása");
        private final JCheckBox cbNoDuplicates = new JCheckBox("Duplikációk eldobása importáláskor");
        private final JCheckBox cbNoUnknownType = new JCheckBox("Ismeretlen jegytípus eldobása importáláskor");
        private int result;
        private String leaveMeta;
        private String deleteMeta;

        ProfileWizard(JFrame parent){
            super(parent,"Profil szerkesztése",true);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    closeWizard(new ActionEvent(this,0,"Cancel"));
                }
            });
            setLayout(new BorderLayout());
            initComponents();

            //Adding tabs
            JTabbedPane mainPanel = new JTabbedPane();
            mainPanel.addTab("Általános",null, createMainPanel(),"A profil általános beállításai");
            mainPanel.addTab("Vonalkódok",null, createListTab(barCodes, new Barcode.BarcodeListener(this)),"A profilhoz tartozó vonalkódok");
            mainPanel.addTab("Jegytípusok",null, createListTab(ticketTypes,new TicketType.TicketTypeListener(this)),"A profilhoz tartozó jegytípusok");
            mainPanel.addTab("Kedvezmények",null, createListTab(discounts, new Discount.DiscountListener(this,EntryProfile.this)),"A profilhoz tartozó kedvezmények");
            mainPanel.setMnemonicAt(0, KeyEvent.VK_1);
            mainPanel.setMnemonicAt(1, KeyEvent.VK_2);
            mainPanel.setMnemonicAt(2, KeyEvent.VK_3);
            mainPanel.setMnemonicAt(3, KeyEvent.VK_4);
            mainPanel.addChangeListener(e -> {
                //Refresh combo boxes
                cbTypes.removeAllItems();
                cbCommandDelete.removeAllItems();
                cbCommandLeave.removeAllItems();
                //Fill combo boxes with new things
                barCodes.forEach(b -> {cbCommandLeave.addItem(b); cbCommandDelete.addItem(b);});
                ticketTypes.forEach(t -> cbTypes.addItem(t));
                //Set selection to previous
                cbTypes.setSelectedItem(defaultType);
                cbCommandLeave.setSelectedItem(identifyBarcode(leaveMeta));
                cbCommandDelete.setSelectedItem(identifyBarcode(deleteMeta));
                //Reseting selection
                leaveMeta = cbCommandLeave.getSelectedItem() == null ? null : ((Barcode) (cbCommandLeave.getSelectedItem())).getMeta();
                deleteMeta = cbCommandDelete.getSelectedItem() == null ? null : ((Barcode) (cbCommandDelete.getSelectedItem())).getMeta();
            });
            //Assembling wizard panel
            add(mainPanel,CENTER);
            add(createBottomPanel(),SOUTH);
            //Finalizing
            pack();
            setResizable(false);
            setLocationRelativeTo(parent);
        }

        private void initComponents(){
            tfName = new JTextField(name,32);
            tfMask = new JTextField(codeMask,32);
            tfModifyMask = new JTextField(codeMask,32);
            cbCommandLeave = new JComboBox<>(barCodes.toArray(new Barcode[0]));
            cbCommandDelete = new JComboBox<>(barCodes.toArray(new Barcode[0]));
            cbTypes = new JComboBox<>(ticketTypes.toArray(new TicketType[0]));

            tfCommandDefault = new JTextField();
            deleteMeta = leaveMeta = null;
            if(commandCodes != null) {
                commandCodes.forEach((command, flag) -> {
                    switch (flag){
                        case FL_DEFAULT:
                            tfCommandDefault = new JTextField(command);
                            break;
                        case FL_IS_DELETE:
                            deleteMeta = command;
                            break;
                        case FL_IS_LEAVING:
                            leaveMeta = command;
                            break;
                    }
                });
            }
            //Setup values
            cbLimit = new JComboBox<>(EntryLimit.values());
            spEntryLimit = new JSpinner(new SpinnerNumberModel(2,2,100,1));
            spEntryLimit.setEditor(new JSpinner.DefaultEditor(spEntryLimit));
            if (entryLimit >= 2) { spEntryLimit.setValue(entryLimit); } else { cbLimit.setSelectedIndex(entryLimit); }

            cbTypes.setSelectedItem(defaultType);
            cbCommandLeave.setSelectedItem(identifyBarcode(leaveMeta));
            cbCommandDelete.setSelectedItem(identifyBarcode(deleteMeta));
        }

        private JPanel createMainPanel(){
            cbLimit.addItemListener(e -> spEntryLimit.setEnabled(Objects.equals(cbLimit.getSelectedItem(), EntryLimit.CUSTOM)));
            spEntryLimit.setEnabled(Objects.equals(cbLimit.getSelectedItem(), EntryLimit.CUSTOM));

            //Check boxes
            tfDefaultTicket = new JTextField(defaultName);
            cbRequiredName.setToolTipText("Amennyiben nem kötelező a név megadása, akkor minden új rekord automatikusan a megadott szöveggel lesz kitöltve");
            cbRequiredName.addActionListener(e-> tfDefaultTicket.setEnabled(!cbRequiredName.isSelected()));
            cbRequiredName.setSelected(nameRequirement);
            tfDefaultTicket.setEnabled(!nameRequirement);

            cbEntryModification.setToolTipText("Beléptetésnél a megadott maszknak megfelelő kódok lecserélődnek az olvasott kódra");
            cbEntryModification.addActionListener(e -> tfModifyMask.setEnabled(cbEntryModification.isSelected()));
            cbEntryModification.setSelected(entryModifiesID);
            tfModifyMask.setEnabled(entryModifiesID);

            JPanel panelMain = new JPanel();
            panelMain.setLayout(createMainPanelLayout(panelMain));
            return panelMain;
        }

        private GroupLayout createMainPanelLayout(JPanel panel) {
            JLabel lbProfileName = new JLabel("Profil neve:");
            JLabel lbMask = new JLabel("Belépési kód maszkja:");
            JLabel lbModifyMask = new JLabel("Átírási kód maszkja:");
            JLabel lbCommandList = new JLabel("Alapvető parancsok:");
            JLabel lbCommands = new JLabel("Parancs");
            JLabel lbBarcodes = new JLabel("Vonalkód");
            JLabel lbEntryCommand = new JLabel("Beléptetés (DEFAULT):");
            JLabel lbLeaveCommand = new JLabel("Kiléptetés (LEAVE):");
            JLabel lbDeleteCommand = new JLabel("Törlés (DELETE):");
            JLabel lbSettings = new JLabel("Profil Beállítások:");
            JLabel lbDefaultTicketEntry = new JLabel("Külsős jegy:");
            JLabel lbEntryNumber = new JLabel("Belépések száma");
            JLabel lbDefaultTicketType = new JLabel("Alapértelmezett jegytípus:");

            GroupLayout gl = new GroupLayout(panel);
            gl.setAutoCreateGaps(true);
            gl.setVerticalGroup(gl.createSequentialGroup()
                    .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(lbProfileName)
                            .addComponent(tfName)
                    )
                    .addGap(25)
                    .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addGroup(gl.createSequentialGroup()
                                    .addComponent(lbMask)
                                    .addComponent(tfMask)
                            )
                            .addGroup(gl.createSequentialGroup()
                                    .addComponent(lbModifyMask)
                                    .addComponent(tfModifyMask)
                            )
                    )
                    .addComponent(cbEntryModification)
                    .addGap(25)
                    .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addGroup(gl.createSequentialGroup()
                                            .addComponent(lbCommandList)
                                            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(lbCommands)
                                                    .addComponent(lbBarcodes)
                                            )
                                            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(lbEntryCommand)
                                                    .addComponent(tfCommandDefault)
                                            )
                                            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(lbLeaveCommand)
                                                    .addComponent(cbCommandLeave)
                                            )
                                            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(lbDeleteCommand)
                                                    .addComponent(cbCommandDelete)
                                            )
                                            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(lbDefaultTicketType)
                                                    .addComponent(cbTypes)
                                            )
                                    )
                            )
                            .addGroup(gl.createSequentialGroup()
                                    .addComponent(lbSettings)
                                    .addComponent(cbNoDuplicates)
                                    .addComponent(cbNoUnknownType)
                                    .addComponent(cbRequiredName)
                                    .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(lbDefaultTicketEntry)
                                            .addComponent(tfDefaultTicket)
                                    )
                                    .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(lbEntryNumber)
                                            .addComponent(spEntryLimit)
                                    )
                            )
                    )
            );

            gl.setHorizontalGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(gl.createSequentialGroup()
                            .addComponent(lbProfileName)
                            .addComponent(tfName)
                    )
                    .addGroup(gl.createSequentialGroup()
                            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addComponent(lbMask)
                                    .addComponent(tfMask)
                                    .addComponent(lbCommandList)
                                    .addGroup(gl.createSequentialGroup()
                                            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addComponent(lbCommands)
                                                    .addComponent(lbEntryCommand)
                                                    .addComponent(lbLeaveCommand)
                                                    .addComponent(lbDeleteCommand)
                                                    .addComponent(lbDefaultTicketType)
                                            )
                                            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addComponent(lbBarcodes)
                                                    .addComponent(tfCommandDefault)
                                                    .addComponent(cbCommandLeave)
                                                    .addComponent(cbCommandDelete)
                                                    .addComponent(cbTypes)
                                            )
                                    )
                            )
                            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addComponent(lbModifyMask)
                                    .addComponent(tfModifyMask)
                                    .addComponent(cbEntryModification)
                                    .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                                            .addComponent(lbSettings)
                                            .addComponent(cbNoDuplicates)
                                            .addComponent(cbNoUnknownType)
                                            .addComponent(cbRequiredName)
                                            .addGroup(gl.createSequentialGroup()
                                                    .addComponent(lbDefaultTicketEntry)
                                                    .addComponent(tfDefaultTicket)
                                            )
                                            .addGroup(gl.createSequentialGroup()
                                                    .addComponent(lbEntryNumber)
                                                    .addComponent(spEntryLimit)
                                            )
                                    )
                            )
                    )
            );
            return gl;
        }

        private <T extends Modifier> JPanel createListTab(List<T> objectList, ModifierWizardEditor<T> editor) {
            JPanel panelList = new JPanel();
            panelList.setLayout(new BorderLayout());

            JList<Modifier> list = new JList<>(objectList.toArray(new Modifier[0]));
            list.addMouseListener(editor);
            list.setSelectionMode(SINGLE_SELECTION);
            list.setCellRenderer(new ModifierValidationRenderer());
            JScrollPane paneListHolder = new JScrollPane(list);
            panelList.add(paneListHolder,CENTER);
            
            JPanel panelOperations = new JPanel();
            JButton btnRemove = new JButton("Törlés");
            JButton btnAdd = new JButton("Hozzáadás");

            panelOperations.add(btnAdd);
            panelOperations.add(btnRemove);
            btnAdd.addActionListener(e ->{
                //Create respective wizard
                editor.createNew(objectList);
                list.setListData(objectList.toArray(new Modifier[0]));
            });

            btnRemove.addActionListener(e -> {
                //Ask for removal
                int res = JOptionPane.showConfirmDialog(null,"Biztos eltávolítod a következőt a listából: " + list.getModel().getElementAt(list.getSelectedIndex()) + "?"
                        ,uh.getUIStr("MSG","WARNING"),JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
                if(res == JOptionPane.YES_OPTION) {
                    //They will be called in their respected place no worries :)
                    //noinspection unchecked
                    editor.removeFrom(objectList, (T) list.getModel().getElementAt(list.getSelectedIndex()));
                    list.setListData(objectList.toArray(new Modifier[0]));
                }
            });
            panelList.add(panelOperations,SOUTH);

            return panelList;
        }

        private JPanel createBottomPanel(){
            JButton[] buttons = new JButton[3];
            buttons[0] = new JButton("OK");
            buttons[1] = new JButton("Cancel");
            buttons[2] = new JButton("Accept");

            JPanel panelBottom = new JPanel();
            panelBottom.setLayout(new BoxLayout(panelBottom,BoxLayout.LINE_AXIS));
            panelBottom.add(Box.createHorizontalGlue());
            for (JButton btn : buttons) {
                btn.setActionCommand(btn.getText());
                btn.addActionListener(this::closeWizard);
                panelBottom.add(btn);
            }

            return panelBottom;
        }

        private void closeWizard(ActionEvent e){
            //TODO: need serious rework....
            switch (e.getActionCommand()){
                case "Cancel":
                    result = -1;
                    System.out.println("EDITING CANCELLED");
                    dispose();
                    break;
                case "OK":
                    if(validateProfile()) {
                        result = 0;
                        System.out.println("EDITING ACCEPTED");
                        dispose();
                    }
                    break;
                case "Accept":
                    if(validateProfile()) {
                        System.out.println("EDITING ACCEPTED (Accept)");
                        result = 0;
                    }
                    break;
            }
        }

        private boolean validateProfile(){
            boolean empty = tfName.getText().isEmpty() ||
                    tfMask.getText().isEmpty() ||
                    tfCommandDefault.getText().isEmpty() ||
                    cbCommandLeave.getSelectedItem() == null ||
                    cbCommandDelete.getSelectedItem() == null ||
                    tfModifyMask.getText().isEmpty();
            boolean commandInvalid = false, noTicket = false, hasInvalidDiscount = false;
            if(!empty) {
                leaveMeta = ((Barcode) (cbCommandLeave.getSelectedItem())).getMeta();
                deleteMeta = ((Barcode) (cbCommandDelete.getSelectedItem())).getMeta();
                commandInvalid = (cbCommandDelete.getSelectedIndex() == cbCommandLeave.getSelectedIndex()) ||
                        leaveMeta.equals(tfDefaultTicket.getText()) ||
                        deleteMeta.equals(tfDefaultTicket.getText());
                noTicket = ticketTypes.isEmpty();
                for (Discount discount : discounts) {
                    hasInvalidDiscount = !discount.validate();
                }

                //ERROR
                if (commandInvalid)
                    JOptionPane.showMessageDialog(null, "A parancsok nem lehetnek egyformák", uh.getUIStr("ERR", "HEADER"), JOptionPane.ERROR_MESSAGE);
                if (noTicket)
                    JOptionPane.showMessageDialog(null, "Vegyél fel legalább egy jegytípust!", uh.getUIStr("ERR", "HEADER"), JOptionPane.ERROR_MESSAGE);
                if(hasInvalidDiscount)
                    JOptionPane.showMessageDialog(null, "Egy, vagy több kedvezmény nem érvényes!", uh.getUIStr("ERR", "HEADER"), JOptionPane.ERROR_MESSAGE);

                //Setting variables
                name = tfName.getText();
                codeMask = tfMask.getText();
                modificationMask = tfModifyMask.getText();
                entryModifiesID = cbEntryModification.isSelected();
                commandCodes.clear();
                commandCodes.put(tfCommandDefault.getText(), FL_DEFAULT);
                commandCodes.put(leaveMeta, FL_IS_LEAVING);
                commandCodes.put(deleteMeta, FL_IS_DELETE);
                defaultType = (TicketType) cbTypes.getSelectedItem();
                nameRequirement = cbRequiredName.isSelected();
                defaultName = tfDefaultTicket.getText();
            } else
                JOptionPane.showMessageDialog(null, "Minden mező kitöltése kötelező", uh.getUIStr("ERR", "HEADER"), JOptionPane.ERROR_MESSAGE);
            return !(empty || commandInvalid || noTicket);
        }

        private int open(){
            setVisible(true);
            while(true)
                if(!isVisible()) break;
            return result;
        }
    }

}
