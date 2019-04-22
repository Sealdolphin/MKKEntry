package control.modifier;

import control.Application;
import data.EntryProfile;
import org.json.simple.JSONObject;
import view.BarcodePanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import static javax.swing.JOptionPane.ERROR_MESSAGE;

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

    private Discount(String name, Barcode barcode , String iconPath, int price, EntryProfile profile){
        this.name = name;
        this.iconPath = iconPath;
        discount = price;
        this.barcode = barcode;
        this.profile = profile;
    }

    public Discount(Discount other, EntryProfile profile) {
        this(other.name,other.barcode,other.iconPath,other.discount,profile);
    }

    public String getMeta(){
        return barcode.getMeta();
    }

    public static Discount parseDiscountFromJson(JSONObject jsonObject, EntryProfile profile) {
        String name;
        name = "undefined";
        String icon = null;
        Barcode barcode = new Barcode(name);
        int price = 0;
        try {
            name = jsonObject.get("name").toString();
            icon = Application.parseFilePath(jsonObject.get("icon").toString());
            price = Integer.parseInt(jsonObject.get("discount").toString());
            barcode = profile.identifyBarcode(jsonObject.get("barCode").toString());
        } catch (Exception other){
            //Show warning message
            JOptionPane.showMessageDialog(new JFrame(),profile.toString()+ ":\nA(z) '" + name +
                    "' kedvezmény importálása közben hiba történt.\n" +
                    "Az importálás nem sikerült. Részletek:\n" + other.toString(),"Hiba",ERROR_MESSAGE);
        }
        return new Discount(name,barcode,icon,price,profile);
    }


    /**
     * Creates a panel, where the Discount's data is visualized to the user.
     * It is used to create the side menu of the application
     * @return a panel containing the Discount information
     */
    public BarcodePanel getBarcodePanel(){
        if(barcode != null) {
            barcode.setLink(true);
            return barcode.createBarcodePanel();
        } else
            return new BarcodePanel(null,"No barcode!");
    }

    @Override
    public String toString(){
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj == this) return true;
        if(!(obj.getClass().equals(Discount.class))) return false;
        Discount other = (Discount) obj;
        return other.getMeta().equals(getMeta());
    }

    public String getIcon(){
        return iconPath;
    }

    @Override
    public ModifierDialog getModifierWizard(Window parent) {
        return new DiscountWizard(parent);
    }

    @Override
    public boolean validate() {
        //Refresh barcode whether if it still in the list of barCodes...
        barcode = Arrays.stream(profile.getBarcodes()).filter(b -> b.getMeta().equals(barcode.getMeta())).findAny().orElse(null);
        return name != null && barcode != null && !name.isEmpty();
    }

    public int getPrice() {
        return discount;
    }

    public static class DiscountListener extends ModifierWizardEditor<Discount>{

        public DiscountListener(Window parent, EntryProfile profile) {
            super(parent);
            this.profile = profile;
        }

        private EntryProfile profile;

        @Override
        public void createNew(List<Discount> objectList) {
            Discount newDiscount = new Discount("",null,basicIcon,0,profile);
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

        private JLabel labelIcon = new JLabel(new ImageIcon(new ImageIcon(iconPath).getImage().getScaledInstance(50,50,Image.SCALE_SMOOTH)));
        private JTextField tfName = new JTextField(name);
        private JComboBox<Barcode> cbBarcodes;
        private JSpinner spPrice = new JSpinner(new SpinnerNumberModel(0, Short.MIN_VALUE,Short.MAX_VALUE,1));

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

            //Assemble Window
            //1. row A: Name
            body.add(new JLabel("Név"),setConstraints(0,0,1,1));
            body.add(tfName,setConstraints(1,0,1,1));
            //1. row B: icon
            body.add(new JLabel(iconPath),setConstraints(2,0,1,1));
            body.add(labelIcon,setConstraints(3,0,1,3));
            //2. row: Discount
            body.add(new JLabel("Hozzájárulás"),setConstraints(0,1,1,1));
            body.add(spPrice,setConstraints(1,1,1,1));
            //3. row: Barcode
            body.add(new JLabel("Vonalkód"),setConstraints(0,2,1,1));
            body.add(cbBarcodes,setConstraints(1,2,1,1));

            finishDialog(parent);
        }

        private void saveDiscount(){
            if(tfName.getText().length() > 0 && cbBarcodes.getSelectedItem() != null){
                name = tfName.getText();
                barcode = (Barcode) cbBarcodes.getSelectedItem();
                discount = Integer.parseInt(spPrice.getValue().toString());
                result = 0;
                //Close dialog
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,"Not a valid discount!","ERROR",JOptionPane.ERROR_MESSAGE);
            }
        }

    }


}
