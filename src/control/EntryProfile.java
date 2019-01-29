package control;

import control.modifier.Discount;
import control.modifier.TicketType;
import data.Entry;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.swing.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static control.Application.uh;
import static javax.swing.BoxLayout.PAGE_AXIS;

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
    /**
     * A beléptetési profil neve
     */
    private String name;
    /**
     * A profilhoz tartozó belépési kódok maszkja
     */
    private String codeMask;

    private String codeStart;
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
    private String[] commandCodes;

    private static String[] commandJsonKeys = new String[]{"leave","delete"};


    private EntryProfile() {
        ticketTypes = new ArrayList<>();
        discounts = new ArrayList<>();
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

    private static EntryProfile parseProfileFromJson(JSONObject jsonProfile) {
        //Loading basic information
        EntryProfile profile = new EntryProfile();

        profile.name = jsonProfile.get("name").toString();
        profile.codeMask = jsonProfile.get("mask").toString();
        profile.codeStart = jsonProfile.get("code").toString();

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
        profile.defaultType = profile.ticketTypes.stream().filter(ticketType -> ticketType.toString().equals(defType)).findAny().orElse(profile.ticketTypes.get(0));

        //Loading default commands
        String[] commands = new String[commandJsonKeys.length];
        for (int i = 0; i < commands.length; i++) {
            commands[i] = ((JSONObject) jsonProfile.get("commands")).get(commandJsonKeys[i]).toString();
        }
        profile.commandCodes = commands;

        return profile;
    }

    /**
     * Creates a side menu with additional attributes and options
     * @return a JPanel containing the sideBar components
     */
    JPanel createDiscountMenu() {
        //Setting up layout
        JPanel panelSide = new JPanel();
        panelSide.setLayout(new BoxLayout(panelSide, PAGE_AXIS));
        for (Discount discount : discounts) {
            //Adding image and image label
            panelSide.add(discount.getDiscountPanel());
        }

        return panelSide;
    }

    Discount identifyDiscountMeta(String discountMeta){
        return discounts.stream().filter(discount -> discount.equals(discountMeta)).findAny().orElse(null);
    }

    TicketType identifyTicketType(String unknownType) {
        return ticketTypes.stream().filter(type -> type.toString().equals(unknownType)).findAny().orElse(defaultType);
    }

    String validateCode(String code) throws IOException{
        //Checking constraints
        String validID = code.replaceAll(codeStart,"").toUpperCase().trim();
        if (!(validID.matches(codeMask) && code.contains(codeStart))) throw new IOException(uh.getUIStr("ERR","CODE_MISMATCH"));
        return validID;
    }

    @Override
    public String toString(){
        return name;
    }

    Entry generateNewEntry(String id, String name) {
        if(name == null) name = "Generated Entry";
        return new Entry(id,name,defaultType);
    }
//
//    JDialog getProfileWizard() {
//        return new ProfileWizard();
//    }
//
//
//
//    private class ProfileWizard extends JDialog {
//
//        private ProfileWizard() {
//            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
//            setModal(true);
//            JTabbedPane panelProfile = new JTabbedPane();
//            setLayout(new BorderLayout());
//
//            //Adding tabs together
//            panelProfile.addTab("Általános", createTabGeneral());
//            panelProfile.addTab("Kedvezmények", createTabDiscounts());
//            panelProfile.addTab("Jegytípusok", new JPanel());
//            //Adding Tabs
//            add(panelProfile, BorderLayout.CENTER);
//
//            JPanel panelDialog = new JPanel();
//            panelDialog.add(new JButton("Mentés"));
//            panelDialog.add(new JButton("Mégse"));
//            panelDialog.add(new JButton("Alkalmaz"));
//
//            add(panelDialog, BorderLayout.SOUTH);
//            //Setting default
//            setTitle("Profil szerkesztése");
//            setMinimumSize(new Dimension(300, 400));
//            pack();
//            setResizable(false);
//            setRelativeLocationOnScreen(this, Main.ScreenLocation.CENTER);
//        }
//
//        private JPanel createTabDiscounts() {
//            JPanel tabDiscount = new JPanel();
//            tabDiscount.setLayout(new BorderLayout());
//
//            tempDiscounts = new AttributeList<>();
//            for (Discount d : discounts.elements) {
//                tempDiscounts.addElement(new Discount(d));
//            }
//            JList<Discount> listDiscounts = new JList<>(tempDiscounts);
//
//            listDiscounts.setSelectionMode(SINGLE_SELECTION);
//            listDiscounts.addMouseListener(new MouseAdapter() {
//                @Override
//                public void mouseClicked(MouseEvent e) {
//                    if (listDiscounts.getSelectedValue() != null && e.getClickCount() >= 2)
//                        listDiscounts.getSelectedValue().getDiscountWizard(EntryProfile.this, listDiscounts.getSelectedIndex()).setVisible(true);
//                }
//            });
//
//            //Assemble List
//            tabDiscount.add(new JLabel("Kedvezmények"), BorderLayout.NORTH);
//            tabDiscount.add(listDiscounts, BorderLayout.CENTER);
//            //Modify panel
//            JPanel panelModify = new JPanel();
//            panelModify.setLayout(new BoxLayout(panelModify, BoxLayout.PAGE_AXIS));
//
//            JButton btnModify = new JButton("Módosítás");
//            btnModify.addActionListener(e -> listDiscounts.getSelectedValue().getDiscountWizard(EntryProfile.this, listDiscounts.getSelectedIndex()).setVisible(true));
//            JButton btnNew = new JButton("Új kedvezmény");
//            btnNew.addActionListener(e -> Discount.createDiscountFromWizard(EntryProfile.this).setVisible(true));
//            panelModify.add(btnNew);
//            panelModify.add(btnModify);
//            panelModify.add(new JButton("Törlés"));
//
//            for (Component c : panelModify.getComponents()) {
//                c.setMaximumSize(new Dimension(getMaximumSize().width, c.getMaximumSize().height));
//            }
//            //Assemble panel
//            tabDiscount.add(panelModify, BorderLayout.SOUTH);
//            return tabDiscount;
//        }
//
//        private JPanel createTabGeneral() {
//            JPanel tabGeneral = new JPanel();
//            //Creating layout
//            tabGeneral.setLayout(new BoxLayout(tabGeneral, BoxLayout.PAGE_AXIS));
//
//            tabGeneral.add(new JLabel("Név"));
//            tabGeneral.add(new JTextField(name));
//            tabGeneral.add(new JLabel("Entry Code"));
//            tabGeneral.add(new JTextField(entryCode.pattern));
//
//            JSpinner spEntry = new JSpinner(new SpinnerNumberModel(2, 2, 100, 1));
//            spEntry.setEnabled(false);
//            spEntry.setEditor(new JSpinner.DefaultEditor(spEntry));
//
//            JComboBox<String> cbEntryMode = new JComboBox<>(new String[]{"Egyszeri", "Korlátlan", "Egyéni"});
//            cbEntryMode.addItemListener(e -> spEntry.setEnabled(cbEntryMode.getSelectedIndex() == 2));
//
//            tabGeneral.add(cbEntryMode);
//            tabGeneral.add(spEntry);
//
//            //Fixing alignment
//            for (Component c : tabGeneral.getComponents()) {
//                JComponent jComp = (JComponent) c;
//                jComp.setAlignmentX(Component.LEFT_ALIGNMENT);
//                c.setMaximumSize(new Dimension(getMaximumSize().width, jComp.getPreferredSize().height));
//            }
//            spEntry.setMaximumSize(new Dimension(spEntry.getMaximumSize().width, 20));
//
//            return tabGeneral;
//        }
//
//    }
//

}
