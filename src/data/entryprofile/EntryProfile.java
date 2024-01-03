package data.entryprofile;


import control.UIHandler;
import control.modifier.EntryProfileEditor;
import control.modifier.Modifier;
import control.modifier.ModifierWizardEditor;
import control.wizard.WizardEditor;
import data.entry.Entry;
import data.entry.EntryCommand;
import data.entry.EntryLimit;
import data.modifier.Barcode;
import data.modifier.Discount;
import data.modifier.TicketType;
import data.util.ReadingFlag;
import data.wizard.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import view.main.panel.wizard.entryprofile.EntryProfileWizardPane;
import view.renderer.ModifierValidationRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.*;
import java.util.function.Function;

import static control.Application.uh;
import static data.entry.EntryCommand.getDefaultCommands;
import static data.util.ReadingFlag.*;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.SOUTH;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

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
public class EntryProfile implements Serializable, WizardType {

    private final UUID profileID = UUID.randomUUID();

    private ProfileSettings profileSettings;

    private String profileName;

    private String profileMask;

    private String profileMaskForEntry;

    private String defaultName;

    private TicketType defaultTicketType;

    private final List<EntryCommand> commandList;

    private final List<Barcode> barcodes;

    private final List<Discount> discounts;

    private final List<TicketType> ticketTypes;

    private final Date createdAt;

    private int entryLimit;

    public EntryProfile() {
        this("");
    }

    public EntryProfile(String name) {
        this.profileName = name;
        this.profileSettings = new ProfileSettings();
        commandList = getDefaultCommands();
        ticketTypes = new ArrayList<>();
        discounts = new ArrayList<>();
        barcodes = new ArrayList<>();
        createdAt = new Date();
    }

    public EntryProfile(EntryProfile other){
        //Reference / primitives
        setProfileSettings(new ProfileSettings(other.getProfileSettings()));
        setProfileName(other.getProfileName());
        setProfileMaskForEntry(other.getProfileMaskForEntry());
        setDefaultTicketType(other.getDefaultTicketType());
        setProfileMask(other.getProfileMask());
        commandList = new ArrayList<>(other.commandList);
        ticketTypes = new ArrayList<>(other.ticketTypes);
        discounts = new ArrayList<>(other.discounts);
        barcodes = new ArrayList<>(other.barcodes);
        createdAt = other.createdAt;

        // Copy deprecated fields
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
        exportFilters = other.exportFilters;
    }

    public TicketType getDefaultTicketType() {
        return defaultTicketType;
    }

    public void setDefaultTicketType(TicketType defaultTicketType) {
        this.defaultTicketType = defaultTicketType;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getProfileMask() {
        return profileMask;
    }

    public void setProfileMask(String profileMask) {
        this.profileMask = profileMask;
    }

    public String getProfileMaskForEntry() {
        return profileMaskForEntry;
    }

    public void setProfileMaskForEntry(String profileMaskForEntry) {
        this.profileMaskForEntry = profileMaskForEntry;
    }

    public BarcodeModel createBarcodeModel() {
        return new BarcodeModel(barcodes);
    }

    public void updateBarcodes(BarcodeModel model) {
        updateFromModel(barcodes, model);
    }

    public TicketTypeModel createTicketTypeModel() {
        return new TicketTypeModel(ticketTypes);
    }

    public void updateTicketTypes(TicketTypeModel model) {
        updateFromModel(ticketTypes, model);
    }

    public DiscountModel createDiscountModel() {
        return new DiscountModel(discounts);
    }

    public void updateDiscounts(DiscountModel model) {
        updateFromModel(discounts, model);
    }

    public void setProfileSettings(ProfileSettings profileSettings) {
        this.profileSettings = profileSettings;
    }

    public ProfileSettings getProfileSettings() {
        return this.profileSettings;
    }

    private static <T extends WizardType> void updateFromModel(List<T> list, DefaultWizardModel<T> model) {
        list.clear();
        for (int i = 0; i < model.getSize(); i++) {
            list.add(model.getElementAt(i));
        }
    }

    @Override
    public String getId() {
        return profileID.toString();
    }

    @Override
    public WizardEditor<EntryProfile> createWizard() {
        return new EntryProfileEditor(this, new EntryProfileWizardPane());
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Discount identifyDiscount(String discountMeta){
        return discounts.stream().filter(discount -> discount.getMeta().equals(discountMeta)).findAny().orElse(null);
    }

    public TicketType identifyTicketType(String ticketType) {
        return ticketTypes.stream().filter(type -> type.getName().equals(ticketType)).findAny().orElse(null);
    }

    public Barcode identifyBarcode(String barcodeMeta){
        return barcodes.stream().filter(barCode -> barCode.getMetaData().equals(barcodeMeta)).findAny().orElse(null);
    }

    public static EntryProfile parseProfileFromJson(JSONObject profileJson) throws JSONException {
        EntryProfile profile = new EntryProfile();

        profile.setProfileName(profileJson.getString("name"));
        profile.setProfileMask(profileJson.getString("mask"));
        profile.setProfileMaskForEntry(profileJson.optString("maskForEntry", ""));

        profile.barcodes.addAll(parseJSONArray(profileJson.getJSONArray("barcodes"), Barcode::parseBarcodeFromJSON));
        profile.ticketTypes.addAll(parseJSONArray(profileJson.getJSONArray("tickets"), TicketType::parseTicketTypeFromJson));
        profile.discounts.addAll(
                parseJSONArray(profileJson.getJSONArray("discounts"), obj -> Discount.parseDiscountFromJson(obj, profile))
        );

        profile.setDefaultTicketType(profile.identifyTicketType(profileJson.optString("defaultType", null)));

        profile.setProfileSettings(ProfileSettings.parseProfileSettingsFromJSON(profileJson.optJSONObject("settings", new JSONObject())));

        return profile;
    }

    private static <T> List<T> parseJSONArray(JSONArray array, Function<JSONObject, T> parser) {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            list.add(parser.apply(array.getJSONObject(i)));
        }
        return list;
    }


    /**
     * #################################### OLD STUFF BELOW ##################################
     */

    @Deprecated
    private String password = "";

    @Deprecated
    public String getPassword() {
        return password;
    }

    /**
     * A beléptetési profil neve
     */
    @Deprecated
    private String name;

    /**
     * A profilhoz tartozó belépési kódok maszkja
     */
    @Deprecated
    private String codeMask;

    /**
     * Ismeretlen adat kezelésének megoldása.
     * true = Automatikus kezelés. Az adatot figyelmen kívül hagyja, vagy az alapméretezett beállításokkal dolgozik.
     * false = Egyéni kezelés. A felhasználó dönti el
     */
    @Deprecated
    private boolean autoDataHandling;
    /**
     * Az exportálási filterek listája
     */

    @Deprecated
    private List exportFilters;

    /**
     * Az alapméretezett jegytípus
     */

    @Deprecated
    private TicketType defaultType;

    /**
     * Kötelező-e a név megadása
     */
    @Deprecated
    private boolean nameRequirement;

    /**
     * Belépéskor új ID-t kap a kijelölt rekord
     */
    @Deprecated
    private boolean entryModifiesID;

    /**
     * A lecserélendő ID-k maszkja
     */
    @Deprecated
    private String modificationMask;

    /**
     * A szükséges parancskódok listája
     */
    @Deprecated
    private HashMap<String, ReadingFlag> commandCodes;

    /**
     * Maximum of actions
     */
    @Deprecated
    private final int maxActionCount = 10;

    @Deprecated
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

    @Deprecated
    public static EntryProfile createProfileFromWizard(JFrame main, EntryProfile edit){
        if(edit == null)
            edit = new EntryProfile();
        ProfileWizard wizard = edit.getWizardEditor(main);
        int res = wizard.open();
        return res == 0 ? edit : null;
    }

    @Deprecated
    public static boolean isRestartNeeded(EntryProfile oldProfile, EntryProfile newProfile) {
        //1. code mask comparison
        if(!oldProfile.codeMask.equals(newProfile.codeMask)) return true;
        //2. Barcode list comparision
        for (Barcode barcode : oldProfile.barcodes) {
            if(!newProfile.barcodes.contains(barcode)) return true;
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

    /**
     * Creates a side menu with additional attributes and options
     * @return a JPanel containing the sideBar components
     */
    @Deprecated
    public JPanel getSidePanel() {
        //Setting up layout
        for(Barcode barcode : barcodes)
            barcode.setLink(false);

        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel,BoxLayout.PAGE_AXIS));

        sidePanel.add(new JLabel("Kedvezmények:"));
        for (Discount discount : discounts) {
            //Adding image and image label
            sidePanel.add(discount.getBarcodePanel());
        }

        sidePanel.add(new JLabel("Parancskódok:"));
        for (Barcode barcode : barcodes) {
            if(!barcode.hasLink())
                sidePanel.add(barcode.createBarcodePanel());
        }

        return sidePanel;
    }

    @Deprecated
    public String validateCode(String code) throws IOException{
        //Checking constraints
        String startCode = getEntryCode();
        String validID = code.replaceAll(startCode,"").toUpperCase().trim();
        if (!(validID.matches(codeMask) && code.contains(startCode))) throw new IOException(uh.getUIStr("ERR","CODE_MISMATCH"));
        return validID;
    }

    @Deprecated
    public ReadingFlag validateCommand(String code) {
        return commandCodes.get(code.toUpperCase());
    }

    @Deprecated
    public String getEntryCode(){
        return commandCodes.entrySet().stream().filter(c -> c.getValue().equals(FL_DEFAULT)).map(Map.Entry::getKey).findAny().orElse("");
    }

    @Deprecated
    public int getMaxActionCount() {
        return maxActionCount;
    }

    @Deprecated
    public Discount[] getDiscounts(){
        return discounts.toArray(new Discount[0]);
    }

    @Deprecated
    public Barcode[] getBarcodes(){
        return barcodes.toArray(new Barcode[0]);
    }

    @Deprecated
    public Entry generateNewEntry(String id) {
        String inputName = defaultName;
        if(nameRequirement) {
            inputName = JOptionPane.showInputDialog("Adj meg egy nevet!");
            if (inputName == null || inputName.isEmpty()) return null;
        }
        return new Entry(id,inputName,defaultType);
    }

    @Deprecated
    public Entry generateFromEntry(Entry entry, String id) {
        return new Entry(id, entry);
    }

    @Deprecated
    public boolean enteringModifiesEntry(String id) {
        return entryModifiesID && id.matches(modificationMask);
    }

    @Deprecated
    private ProfileWizard getWizardEditor(JFrame main) { return new ProfileWizard(main); }

    @Deprecated
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
            mainPanel.addTab("Vonalkódok",null, createListTab(barcodes, new Barcode.BarcodeListener(this)),"A profilhoz tartozó vonalkódok");
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
                barcodes.forEach(b -> {cbCommandLeave.addItem(b); cbCommandDelete.addItem(b);});
                ticketTypes.forEach(t -> cbTypes.addItem(t));
                //Set selection to previous
                cbTypes.setSelectedItem(defaultType);
                cbCommandLeave.setSelectedItem(identifyBarcode(leaveMeta));
                cbCommandDelete.setSelectedItem(identifyBarcode(deleteMeta));
                //Reseting selection
                leaveMeta = cbCommandLeave.getSelectedItem() == null ? null : ((Barcode) (cbCommandLeave.getSelectedItem())).getMetaData();
                deleteMeta = cbCommandDelete.getSelectedItem() == null ? null : ((Barcode) (cbCommandDelete.getSelectedItem())).getMetaData();
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
            tfModifyMask = new JTextField(modificationMask,32);
            cbCommandLeave = new JComboBox<>(barcodes.toArray(new Barcode[0]));
            cbCommandDelete = new JComboBox<>(barcodes.toArray(new Barcode[0]));
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
            btnRemove.setEnabled(false);
            list.addListSelectionListener(e -> btnRemove.setEnabled(e.getFirstIndex() >= 0));

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
                    btnRemove.setEnabled(false);
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
                leaveMeta = ((Barcode) (cbCommandLeave.getSelectedItem())).getMetaData();
                deleteMeta = ((Barcode) (cbCommandDelete.getSelectedItem())).getMetaData();
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
