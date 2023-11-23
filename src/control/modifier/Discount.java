package control.modifier;

import data.EntryProfile;
import data.modifier.Barcode;
import org.json.simple.JSONObject;
import view.BarcodePanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import static javax.swing.JOptionPane.ERROR_MESSAGE;

/**
 * This class represents a Discount modifier.
 * It stores special price modification for individual records.
 * A Discount can modify one Entry's price. It can increase or decrease it's value (below negative)
 * Every Discount has a representative name and a barcode associated to it.
 * The associated barcode is the identifier used for the Discounts.
 * It comes with a wizard class to modify the attributes.
 * @author Mihalovits Márk
 */
public class Discount implements Serializable, Modifier {

    /**
     * The default option for icons
     */
    private static final String basicIcon = "Icons"+ File.separator +"BasicIcon.png";

    /**
     * The descriptive name of the discount
     */
    private String name;

    /**
     * The barcode connected to the discount
     */
    private Barcode barcode;

    /**
     * Ingyenes beléptetést biztosít
     */
    private Boolean isFree;

    /**
     * The filepath of the icon's image
     */
    private String iconPath;

    /**
     * The ammount of discount this discount gives.
     */
    private int discount;

    /**
     * The EntryProfile owning the discount
     */
    private EntryProfile profile;

    /**
     * Private constructor for creating Discounts in parse functions
     * @param name the name of the discount
     * @param barcode the associated barcode
     * @param iconPath the filepath of the icon
     * @param price the discount
     * @param profile the associated profile
     */
    private Discount(String name, Barcode barcode , String iconPath, int price, boolean isFree, EntryProfile profile){
        this.name = name;
        this.iconPath = iconPath;
        discount = price;
        this.isFree = isFree;
        this.barcode = barcode;
        this.profile = profile;
    }

    /**
     * The copy constructor
     * @param other the Discount you want to copy
     * @param profile the profile you want to copy to
     */
    public Discount(Discount other, EntryProfile profile) {
        this(other.name,other.barcode,other.iconPath,other.discount,other.isFree,profile);
    }

    /**
     * Returns the associated barcode's metadata
     * @return the barcode's metadata
     */
    public String getMeta(){
        return barcode.getMetaData();
    }

    /**
     * Parses a JSON object and creates a Discount object.
     * It searches for all the required fields.
     * If any of the required field is missing then it fails with a NPE.
     * For field naming see the JSON documentation
     * @see Discount(String,Barcode,String,int,EntryProfile)
     * @param jsonObject the object it parses
     * @param profile the associated profile
     * @return the parsed Discount class reference
     */
    public static Discount parseDiscountFromJson(JSONObject jsonObject, EntryProfile profile) {
        String name;
        name = "undefined";
        String icon = null;
        Barcode barcode = new Barcode(name, "", "", "");
        int price = 0;
        boolean isFree = false;
        try {
            name = jsonObject.get("name").toString();
            icon = "Icons" + File.separator + jsonObject.get("icon").toString();
            price = Integer.parseInt(jsonObject.get("discount").toString());
            barcode = profile.identifyBarcode(jsonObject.get("barCode").toString());
            isFree = Boolean.parseBoolean(jsonObject.getOrDefault("isFree", false).toString());
        } catch (Exception other){
            //Show warning message
            JOptionPane.showMessageDialog(new JFrame(),profile.toString()+ ":\nA(z) '" + name +
                    "' kedvezmény importálása közben hiba történt.\n" +
                    "Az importálás nem sikerült. Részletek:\n" + other,"Hiba",ERROR_MESSAGE);
        }
        return new Discount(name,barcode,icon,price,isFree,profile);
    }


    /**
     * Creates a panel with the Discount's barcode picture
     * @return a panel containing the barcode picture and other info
     */
    public BarcodePanel getBarcodePanel(){
        if(barcode != null) {
            barcode.setLink(true);
            return barcode.createBarcodePanel();
        } else
            return new BarcodePanel(null,"No barcode!");
    }

    /**
     * Returns the filepath of the icon
     * @return the filepath of the icon
     */
    public String getIcon(){
        return iconPath;
    }

    /**
     * Returns if discount is free
     */
    public boolean isFree() {
        return isFree;
    }

    /**
     * Returns the price
     * @return the amount of the discount
     */
    public int getPrice() {
        return discount;
    }

    /**
     * Overrides Object::toString()
     * @return the Discount's name
     */
    @Override
    public String toString(){
        return name;
    }

    /**
     * Overrides Object::equals(Object)
     * @param obj the other object it compares to
     * @return true if the two object shares the same name
     */
    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj == this) return true;
        if(!(obj.getClass().equals(Discount.class))) return false;
        Discount other = (Discount) obj;
        return other.getMeta().equals(getMeta());
    }

    /**
     * Overrides Modifier::getModifierWizard(Window)
     * @param parent the parent window
     * @return a wizard dialog to change the attributes
     */
    @Override
    public ModifierDialog getModifierWizard(Window parent) {
        return new DiscountWizard(parent);
    }

    /**
     * Overrides Modifier::validate()
     * All discounts are required to have a @NouNull barcode field.
     * @return true if the fields are correct
     */
    @Override
    public boolean validate() {
        //Refresh barcode whether if it still in the list of barCodes...
        if(barcode == null) return false;
        barcode = Arrays.stream(profile.getBarcodes()).filter(b -> b.getMetaData().equals(barcode.getMetaData())).findAny().orElse(null);
        return name != null && barcode != null && !name.isEmpty();
    }

    /**
     * A required object for Modifier classes.
     * It handles the safe modification of attributes
     */
    public static class DiscountListener extends ModifierWizardEditor<Discount>{

        public DiscountListener(Window parent, EntryProfile profile) {
            super(parent);
            this.profile = profile;
        }

        private EntryProfile profile;

        @Override
        public void createNew(List<Discount> objectList) {
            Discount newDiscount = new Discount("",null,basicIcon,0,false,profile);
            ModifierDialog wizard = newDiscount.getModifierWizard(null);
            int result = wizard.open();
            if(result == 0){
                objectList.add(newDiscount);
            }
        }
    }

    /**
     * An inner class of the Discount.
     * It is responsible for creating or modifying Discounts.
     */
    private class DiscountWizard extends ModifierDialog {

        private final JLabel labelIcon = new JLabel(new ImageIcon(new ImageIcon(iconPath).getImage().getScaledInstance(50,50,Image.SCALE_SMOOTH)));
        private final JLabel lbPath = new JLabel(iconPath);
        private final JTextField tfName = new JTextField(name);
        private final JComboBox<Barcode> cbBarcodes;
        private final JSpinner spPrice = new JSpinner(new SpinnerNumberModel(0, Short.MIN_VALUE,Short.MAX_VALUE,1));
        private final JCheckBox cbIsFree = new JCheckBox("Ingyenes");

        /**
         * Default constructor.
         * It creates a separate window for the user to modify the values.
         */
        DiscountWizard(Window parent){
            //Setup super
            super(parent,"Kedvezmény beállítása");
            btnSave.addActionListener(e -> saveDiscount());
            body.setLayout(new GridBagLayout());
            //Setup components
            labelIcon.setVerticalTextPosition(SwingConstants.TOP);
            labelIcon.setHorizontalTextPosition(SwingConstants.RIGHT);
            spPrice.setValue(discount);
            cbBarcodes = new JComboBox<>(profile.getBarcodes());
            JButton btnChangeIcon = new JButton("Tallóz");
            btnChangeIcon.addActionListener(e -> {
                String newPath = selectPicture(labelIcon);
                if(newPath != null) {
                    lbPath.setText(newPath);
                    refresh(parent);
                }
            });

            //Assemble Window
            //1. row A: Name
            body.add(new JLabel("Név"),setConstraints(0,0,2,1));
            body.add(tfName,setConstraints(2,0,2,1));
            //1. row B: icon
            body.add(labelIcon,setConstraints(0,1,2,2));
            body.add(lbPath,setConstraints(2,1,2,1));
            body.add(btnChangeIcon,setConstraints(2,2,2,1));
            //2. row: Discount
            body.add(new JLabel("Hozzájárulás"),setConstraints(0,3,2,1));
            body.add(spPrice,setConstraints(2,3,2,1));
            //3. row: Barcode
            body.add(new JLabel("Vonalkód"),setConstraints(0,4,2,1));
            body.add(cbBarcodes,setConstraints(2,4,2,1));
            //4. row: Free
            body.add(cbIsFree,setConstraints(2,5,2,1));

            finishDialog(parent);
        }

        /**
         * Write changes to the Discount and closes wizard dialog
         * If any illegal argument is found then alerts the user without changing
         */
        private void saveDiscount(){
            if(!tfName.getText().isEmpty() && cbBarcodes.getSelectedItem() != null){
                name = tfName.getText();
                barcode = (Barcode) cbBarcodes.getSelectedItem();
                discount = Integer.parseInt(spPrice.getValue().toString());
                isFree = cbIsFree.isSelected();
                result = 0;
                iconPath = lbPath.getText();
                //Close dialog
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,"Not a valid discount!","ERROR",JOptionPane.ERROR_MESSAGE);
            }
        }

    }


}
