package Control;

import Control.EntryModifier.Discount;
import Control.EntryModifier.TicketType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    private String[] defaultCommands;


    public EntryProfile() {
        ticketTypes = new ArrayList<>();
        discounts = new ArrayList<>();
        defaultCommands = new String[]{"leave","delete"};
    }

//    public static EntryProfile parseProfileFromJson(JSONObject jsonProfile) throws Exception {
//        //Loading basic information
//        JSONObject jsonCode = (JSONObject) jsonProfile.get("entry_code");
//        EntryProfile profile = new EntryProfile(jsonCode);
//        profile.name = jsonProfile.get("name").toString();
//
//        //Loading discounts
//        JSONArray jArray = (JSONArray) jsonProfile.get("discounts");
//        for (Object discountObject : jArray) {
//            profile.discounts.addElement(Discount.parseDiscountFromJson((JSONObject) discountObject));
//        }
//        //Loading Ticket types
//        jArray = (JSONArray) jsonProfile.get("tickets");
//        for (Object discountObject : jArray) {
//            profile.types.addElement(TicketType.parseTicketTypeFromJson((JSONObject) discountObject));
//        }
//
//        //Setting the default ticket type
//        String defType = jsonProfile.get("defaultType").toString();
//        TicketType.defaultType = profile.types.stream().filter(ticketType -> ticketType.getName().equals(defType)).findAny().orElse(profile.types.get(0));
//
//        //Loading commands
//        String[] commands = new String[2];
//        commands[0] = ((JSONObject) jsonProfile.get("commands")).get(defaults[0]).toString();
//        commands[1] = ((JSONObject) jsonProfile.get("commands")).get(defaults[1]).toString();
//        profile.defaultCommands = commands;
//
//        return profile;
//    }
//
//    /**
//     * Creates a side menu with additional attributes and options
//     * @return a JPanel containing the sideBar components
//     */
//    public JPanel createSideMenu() {
//        //Setting up layout
//        JPanel panelSide = new JPanel();
//        panelSide.setLayout(new BoxLayout(panelSide, PAGE_AXIS));
//
//        //Add the label first
//        JLabel lbHeader = new JLabel(ui.getUIStr("UI", "DISCOUNT_BTN"));
//        lbHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
//        panelSide.add(lbHeader);
//
//        for (Discount discount : discounts.elements) {
//            //Adding image and image label
//            panelSide.add(discount.getDiscountPanel());
//        }
//
//        return panelSide;
//    }
//
//    public String getName() {
//        return name;
//    }
//
    TicketType identifyTicketType(String unknownType) {
        return ticketTypes.stream().filter(type -> type.toString().equals(unknownType)).findAny().orElse(defaultType);
    }
//
//    /**
//     * Sets up the controller meta information from the stored profile
//     * @param controller the controller to be set up
//     */
//    void setController(EntryController controller) {
//        //Set MetaData for discounts
//        List<String> discountMeta = new ArrayList<>();
//        for (Discount discount : discounts.elements) {
//            discountMeta.add(discount.getMeta());
//        }
//        controller.setMetaData(entryCode.start, discountMeta, defaultCommands);
//    }
//
//    String validateCode(String code) throws IOException {
//        String validID;
//        //Removing code start part
//        validID = code.replace(entryCode.start, "").toUpperCase().trim();
//        //Checking constraints
//        if (!validID.matches(entryCode.pattern)) throw new IOException(ui.getUIStr("ERR","CODE_MISMATCH"));
//
//        return validID;
//    }
//
//    JDialog getProfileWizard() {
//        return new ProfileWizard();
//    }
//
//    private class CodeRestraints {
//        final String start;
//        final String pattern;
//
//        private CodeRestraints(Object start, Object pattern) throws Exception {
//            if (start == null || pattern == null) throw new Exception();
//            this.start = start.toString();
//            this.pattern = pattern.toString();
//        }
//    }
//
//    private class AttributeList<T> extends DefaultListModel<T> {
//        private List<T> elements = new ArrayList<>();
//
//        Stream<T> stream() {
//            return elements.stream();
//        }
//
//        @Override
//        public void add(int index, T element) {
//            super.add(index, element);
//            elements.add(index, element);
//        }
//
//        @Override
//        public void addElement(T element) {
//            super.addElement(element);
//            elements.add(element);
//        }
//    }
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
