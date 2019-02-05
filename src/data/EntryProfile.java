package data;

import control.AppController;
import control.UIHandler;
import control.modifier.Discount;
import control.modifier.TicketType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.List;

import static control.AppController.ReadingFlag.FL_DEFAULT;
import static control.AppController.ReadingFlag.FL_IS_DELETE;
import static control.AppController.ReadingFlag.FL_IS_LEAVING;
import static control.Application.uh;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.SOUTH;
import static javax.swing.BoxLayout.PAGE_AXIS;
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
public class EntryProfile implements Serializable {

    public enum EntryLimit {
        ONCE("Egyszeri"),
        NO_LIMIT("Korlátlan"),
        CUSTOM("Egyéni");

        private final String name;

        EntryLimit(String name){this.name = name;}
        @Override
        public String toString(){ return name; }
    }

    public String startCode;
    /**
     * A beléptetési profil neve
     */
    private String name;
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
     * A szükséges parancskódok listája
     */
    private HashMap<String, AppController.ReadingFlag> commandCodes;

    private static AppController.ReadingFlag[] commandJsonKeys = AppController.ReadingFlag.values();


    private EntryProfile() {
        ticketTypes = new ArrayList<>();
        discounts = new ArrayList<>();
    }

    protected EntryProfile(EntryProfile other){
        //Reference / primitives
        name = other.name;
        codeMask = other.codeMask;
        entryLimit = other.entryLimit;
        autoDataHandling = other.autoDataHandling;
        defaultType = other.defaultType;
        //TODO: exact copying
        commandCodes = other.commandCodes;
        ticketTypes = other.ticketTypes;
        discounts = other.discounts;
        exportFilters = other.exportFilters;
    }


    public static void loadProfilesFromJson(JSONObject object, List<EntryProfile> profileList) throws IOException{
        System.out.println("Loaded JSON Object: " + object);
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

    public static EntryProfile parseProfileFromJson(JSONObject jsonProfile) {
        //Loading basic information
        EntryProfile profile = new EntryProfile();

        profile.name = jsonProfile.get("name").toString();
        profile.codeMask = jsonProfile.get("mask").toString();

        //Loading discounts
        JSONArray jArray = (JSONArray) jsonProfile.get("discounts");
        for (Object discountObject : jArray) {
            profile.discounts.add(Discount.parseDiscountFromJson((JSONObject) discountObject, profile.name));
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
            if(commandFlag.equals(FL_DEFAULT)) profile.startCode = key;
        }
        profile.commandCodes = commands;

        return profile;
    }

    /**
     * Creates a side menu with additional attributes and options
     * @return a JPanel containing the sideBar components
     */
    public JPanel createDiscountMenu() {
        //Setting up layout
        JPanel panelSide = new JPanel();
        panelSide.setLayout(new BoxLayout(panelSide, PAGE_AXIS));
        for (Discount discount : discounts) {
            //Adding image and image label
            panelSide.add(discount.getDiscountPanel());
        }

        return panelSide;
    }

    public Discount identifyDiscountMeta(String discountMeta){
        return discounts.stream().filter(discount -> discount.equals(discountMeta)).findAny().orElse(null);
    }

    public TicketType identifyTicketType(String unknownType) {
        return ticketTypes.stream().filter(type -> type.toString().equals(unknownType)).findAny().orElse(defaultType);
    }

    public String validateCode(String code) throws IOException{
        //Checking constraints
        String validID = code.replaceAll(startCode,"").toUpperCase().trim();
        if (!(validID.matches(codeMask) && code.contains(startCode))) throw new IOException(uh.getUIStr("ERR","CODE_MISMATCH"));
        return validID;
    }

    public AppController.ReadingFlag validateCommand(String code) {
        return commandCodes.get(code.toUpperCase());
    }

    @Override
    public String toString(){
        return name;
    }

    public Entry generateNewEntry(String id) {
        return new Entry(id,null,defaultType);
    }

    public JDialog getWizardEditor(JFrame main) {
        return new ProfileWizard(main);
    }

    class ProfileWizard extends JDialog{

        private JTextField tfName;
        private JTextField tfMask;
        private JTextField tfCommandDefault;
        private JTextField tfCommandLeave;
        private JTextField tfCommandDelete;

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
            mainPanel.addTab("Jegytípusok",null, new JPanel(),"A profilhoz tartozó jegytípusok");
            mainPanel.addTab("Kedvezmények",null, new JPanel(),"A profilhoz tartozó kedvezmények");
            mainPanel.setMnemonicAt(0, KeyEvent.VK_1);
            mainPanel.setMnemonicAt(1, KeyEvent.VK_2);
            mainPanel.setMnemonicAt(2, KeyEvent.VK_3);
            //Assembling wizard panel
            add(mainPanel,CENTER);
            add(createBottomPanel(),SOUTH);
            //Finalizing
            pack();
            setResizable(false);
            setLocationRelativeTo(parent);

            //Setting up parameters

            /*

            -> EntryProfile.getData() -> Components.setData

            -> Components.getData() : View.Components -> new EntryProfile -> Save config!
            VIEW -> CONTROLLER -> VIEW -> MODEL -> SERIALIZE
            Wizard.JButton -> Controller -> Wizard.getComponent.getText -> new EntryProfile -> writeObject

             */
        }

        private void initComponents(){
            tfName = new JTextField(name,32);
            tfMask = new JTextField(codeMask,32);
            tfCommandDefault = new JTextField(commandCodes.entrySet().stream().filter(flag -> flag.getValue().equals(FL_DEFAULT)).map(Map.Entry::getKey).findAny().orElse(null));
            tfCommandLeave = new JTextField(commandCodes.entrySet().stream().filter(flag -> flag.getValue().equals(FL_IS_LEAVING)).map(Map.Entry::getKey).findAny().orElse(null));
            tfCommandDelete = new JTextField(commandCodes.entrySet().stream().filter(flag -> flag.getValue().equals(FL_IS_DELETE)).map(Map.Entry::getKey).findAny().orElse(null));
        }

        private JPanel createMainPanel(){
            JPanel panelMain = new JPanel();
            panelMain.setLayout(new GridBagLayout());

            //Create panels
            //Main (name)
            panelMain.add(new JLabel("Profil neve:"), setConstraints(0,0,3));
            panelMain.add(tfName, setConstraints(0,1,3));
            panelMain.add(new JLabel("Belépési kód maszkja:"), setConstraints(0,2,3));
            GridBagConstraints c = setConstraints(0,3,3);
            c.insets = new Insets(0,0,20,0);
            panelMain.add(tfMask,c);

            //Commands
            panelMain.add(new JLabel("Alapvető parancsok:"), setConstraints(0,4,1));
            panelMain.add(new JLabel("Beléptetés (DEFAULT): "), setConstraints(0,5,1));
            panelMain.add(new JLabel("Kiléptetés (LEAVE): "), setConstraints(0,6,1));
            panelMain.add(new JLabel("Törlés (DELETE): "), setConstraints(0,7,1));
            panelMain.add(tfCommandDefault, setConstraints(1,5,2));
            panelMain.add(tfCommandLeave, setConstraints(1,6,2));
            panelMain.add(tfCommandDelete, setConstraints(1,7,2));

            //Settings and behaviours
    //        spEntryLimit.setEditor(new JSpinner.DefaultEditor(spEntryLimit));
    //        cbLimit.addItemListener(e -> spEntryLimit.setEnabled(Objects.equals(cbLimit.getSelectedItem(), EntryLimit.CUSTOM)));
    //
    //        spEntryLimit.setEnabled(Objects.equals(cbLimit.getSelectedItem(), ProfileWizard.EntryLimit.CUSTOM));

            panelMain.add(new JLabel("Belépések sázma:"), setConstraints(0,8,1));
    //        panelMain.add(cbLimit, setConstraints(1,8,1));
    //        panelMain.add(spEntryLimit, setConstraints(2,8,1));

            panelMain.add(new JCheckBox("Duplikációk automatikus eldobása"), setConstraints(0,9,2));
            panelMain.add(new JCheckBox("Ismeretlen jegytípusok automatikus eldobása"), setConstraints(0,10,2));

            return panelMain;
        }

        private GridBagConstraints setConstraints(int x, int y, int w){
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = x;
            constraints.gridy = y;
            constraints.gridwidth = w;
            constraints.gridheight = 1;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            return constraints;
        }

        private JPanel createListTab(Object[] objectList, ListSelectionListener editor) {
            JPanel panelList = new JPanel();
            panelList.setLayout(new BorderLayout());

            //noinspection unchecked
            JList<String> list = new JList(objectList);
            list.addListSelectionListener(editor);
            list.setSelectionMode(SINGLE_SELECTION);
            JScrollPane paneListHolder = new JScrollPane(list);
            panelList.add(paneListHolder,CENTER);


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
            switch (e.getActionCommand()){
                case "Cancel":
                    System.out.println("EDITING CANCELLED");
                    break;
                case "OK":
                case "Accept":
                    System.out.println("EDITING ACCEPTED");
                    break;
            }
            dispose();
        }

    }
}
