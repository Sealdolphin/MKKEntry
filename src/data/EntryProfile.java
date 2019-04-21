package data;


import static control.AppController.ReadingFlag.FL_DEFAULT;
import static control.AppController.ReadingFlag.FL_IS_DELETE;
import static control.AppController.ReadingFlag.FL_IS_LEAVING;
import static control.Application.uh;
import static control.modifier.ModifierDialog.setConstraints;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.SOUTH;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
    private List<TicketType> ticketTypes;
    /**
     * Az alapméretezett jegytípus
     */
    private TicketType defaultType;
    /**
     * A kedvezmények listája
     */
    private List<Discount> discounts;

    /**
     * A vonalkódok listája
     */
    private List<Barcode> barCodes;

    /**
     * Kötelező-e a név megadása
     */
    private boolean nameRequirement;

    /**
     * A szükséges parancskódok listája
     */
    private HashMap<String, AppController.ReadingFlag> commandCodes;

    private static AppController.ReadingFlag[] commandJsonKeys = AppController.ReadingFlag.values();


    private EntryProfile() {
        ticketTypes = new ArrayList<>();
        discounts = new ArrayList<>();
        commandCodes = new HashMap<>();
        barCodes = new ArrayList<>();
        nameRequirement = true;
        defaultName = "Külsős jegy";
    }

    public EntryProfile(EntryProfile other){
        //Reference / primitives
        name = other.name;
        codeMask = other.codeMask;
        entryLimit = other.entryLimit;
        autoDataHandling = other.autoDataHandling;
        defaultType = other.defaultType;
        commandCodes = other.commandCodes;
        password = other.password;
        nameRequirement = other.nameRequirement;
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
            throw new IOException(uh.getUIStr("ERR","PROFILE_DATA_PARSE") + "\n" + e.toString());
        }
    }

    public static EntryProfile createProfileFromWizard(JFrame main, EntryProfile edit){
        if(edit == null)
            edit = new EntryProfile();
        ProfileWizard wizard = edit.getWizardEditor(main);
        int res = wizard.open();
        return res == 0 ? edit : null;
    }

    private static EntryProfile parseProfileFromJson(JSONObject jsonProfile) {
        //Loading basic information
        EntryProfile profile = new EntryProfile();

        profile.name = jsonProfile.get("name").toString();
        profile.codeMask = jsonProfile.get("mask").toString();

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
        }
        return new Entry(id,inputName,defaultType);
    }

    private ProfileWizard getWizardEditor(JFrame main) { return new ProfileWizard(main); }

    class ProfileWizard extends JDialog{

        private JTextField tfName;
        private JTextField tfMask;
        private JTextField tfCommandDefault;
        private JComboBox<Barcode> cbCommandLeave;
        private JComboBox<Barcode> cbCommandDelete;
        private JComboBox<EntryLimit> cbLimit;
        private JSpinner spEntryLimit;
        private JComboBox<TicketType> cbTypes;
        private JCheckBox checkNames = new JCheckBox("Név megadása kötelező");
        private JTextField tfDefaultName;
        private int result;
        private String leaveMeta = commandCodes.entrySet().stream().filter(flag -> flag.getValue().equals(FL_IS_LEAVING)).map(Map.Entry::getKey).findAny().orElse(null);
        private String deleteMeta = commandCodes.entrySet().stream().filter(flag -> flag.getValue().equals(FL_IS_DELETE)).map(Map.Entry::getKey).findAny().orElse(null);

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
            cbCommandLeave = new JComboBox<>(barCodes.toArray(new Barcode[0]));
            cbCommandDelete = new JComboBox<>(barCodes.toArray(new Barcode[0]));
            cbTypes = new JComboBox<>(ticketTypes.toArray(new TicketType[0]));

            if(commandCodes != null) {
                tfCommandDefault = new JTextField(commandCodes.entrySet().stream().filter(flag -> flag.getValue().equals(FL_DEFAULT)).map(Map.Entry::getKey).findAny().orElse(""));
            } else {
                tfCommandDefault = new JTextField();
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
            JPanel panelMain = new JPanel();
            panelMain.setLayout(new GridBagLayout());

            //Create panels
            //Main (name)
            panelMain.add(new JLabel("Profil neve:"), setConstraints(0,0,3,1));
            panelMain.add(tfName, setConstraints(0,1,3,1));
            panelMain.add(new JLabel("Belépési kód maszkja:"), setConstraints(0,2,3,1));
            GridBagConstraints c = setConstraints(0,3,3,1);
            c.insets = new Insets(0,0,20,0);
            panelMain.add(tfMask,c);

            //Commands
            panelMain.add(new JLabel("Alapvető parancsok:"), setConstraints(0,4,1,1));
            panelMain.add(new JLabel("Beléptetés (DEFAULT): "), setConstraints(0,5,1,1));
            panelMain.add(new JLabel("Kiléptetés (LEAVE): "), setConstraints(0,6,1,1));
            panelMain.add(new JLabel("Törlés (DELETE): "), setConstraints(0,7,1,1));
            panelMain.add(tfCommandDefault, setConstraints(1,5,2,1));
            panelMain.add(cbCommandLeave, setConstraints(1,6,2,1));
            panelMain.add(cbCommandDelete, setConstraints(1,7,2,1));

            //Settings and behaviours
            panelMain.add(new JLabel("Alapméretezett jegytípus: "), setConstraints(0,8,1,1));
            panelMain.add(cbTypes, setConstraints(1,8,2,1));

            cbLimit.addItemListener(e -> spEntryLimit.setEnabled(Objects.equals(cbLimit.getSelectedItem(), EntryLimit.CUSTOM)));
            spEntryLimit.setEnabled(Objects.equals(cbLimit.getSelectedItem(), EntryLimit.CUSTOM));

            //Statistics
            panelMain.add(new JLabel("Belépések sázma:"), setConstraints(0,9,1,1));
            panelMain.add(cbLimit, setConstraints(1,9,1,1));
            panelMain.add(spEntryLimit, setConstraints(2,9,1,1));

            //Other
            tfDefaultName = new JTextField(defaultName);
            checkNames.setToolTipText("Amennyiben nem kötelező a név megadása, akkor minden rekord automatikusan a megadott szöveggel lesz kitöltve");
            checkNames.addActionListener(e-> tfDefaultName.setEnabled(!checkNames.isSelected()));
            checkNames.setSelected(nameRequirement);
            tfDefaultName.setEnabled(!nameRequirement);

            panelMain.add(new JCheckBox("Duplikációk automatikus eldobása"), setConstraints(0,10,2,1));
            panelMain.add(new JCheckBox("Ismeretlen jegytípusok automatikus eldobása"), setConstraints(0,11,2,1));
            panelMain.add(checkNames,setConstraints(0,12,1,1));
            panelMain.add(tfDefaultName,setConstraints(1,12,2,1));


            return panelMain;
        }

        private <T extends Modifier> JPanel createListTab(List<T> objectList, ModifierWizardEditor<T> editor) {
            JPanel panelList = new JPanel();
            panelList.setLayout(new BorderLayout());

            JList<Object> list = new JList<>(objectList.toArray());
            list.addMouseListener(editor);
            list.setSelectionMode(SINGLE_SELECTION);
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
                list.setListData(objectList.toArray());
            });
            btnRemove.addActionListener(e -> {
                //Ask for removal
                int res = JOptionPane.showConfirmDialog(null,"Biztos eltávolítod a következőt a listából: " + list.getModel().getElementAt(list.getSelectedIndex()) + "?"
                        ,uh.getUIStr("MSG","WARNING"),JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
                if(res == JOptionPane.YES_OPTION) {
                    //They will be called in their respected place no worries :)
                    //noinspection unchecked
                    editor.removeFrom(objectList, (T) list.getModel().getElementAt(list.getSelectedIndex()));
                    list.setListData(objectList.toArray());
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
                    cbCommandDelete.getSelectedItem() == null;
            boolean commandInvalid = false, noTicket = false, hasInvalidDiscount = false;
            if(!empty) {
                leaveMeta = ((Barcode) (cbCommandLeave.getSelectedItem())).getMeta();
                deleteMeta = ((Barcode) (cbCommandDelete.getSelectedItem())).getMeta();
                commandInvalid = (cbCommandDelete.getSelectedIndex() == cbCommandLeave.getSelectedIndex()) ||
                        leaveMeta.equals(tfDefaultName.getText()) ||
                        deleteMeta.equals(tfDefaultName.getText());
                noTicket = ticketTypes.isEmpty();
                System.out.println("Profile (in Wizard) : " + EntryProfile.this.hashCode());
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
                commandCodes.clear();
                commandCodes.put(tfCommandDefault.getText(), FL_DEFAULT);
                commandCodes.put(leaveMeta, FL_IS_LEAVING);
                commandCodes.put(deleteMeta, FL_IS_DELETE);
                defaultType = (TicketType) cbTypes.getSelectedItem();
                nameRequirement = checkNames.isSelected();
                defaultName = tfDefaultName.getText();
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
