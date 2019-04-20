package control.modifier;

import control.Application;
import org.json.simple.JSONObject;
import view.BarcodePanel;
import view.ImagePanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.Serializable;
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
     * The path of the image
     */
    @Deprecated
    private String imagePath;

    /**
     * The barcode connected to the discount
     */
    private Barcode barcode = new Barcode("TEMP");  //TODO: temp removal

    /**
     * The filepath of the icon's image
     */
    private String iconPath;

    /**
     * The descriptive label of the discount
     */
    @Deprecated
    private String label;

    /**
     * The metadata identifying the discount.
     */
    private String metaData;

    /**
     * The ammount of discount this discount gives.
     */
    private int discount;

    private Discount(String name, String imagePath, String iconPath, String label, String meta, int price){
        this.name = name;
        this.imagePath = imagePath;
        this.label = label;
        this.iconPath = iconPath;
        metaData = meta;
        discount = price;
        //TODO: link barcode
    }

    public Discount(Discount other) {
        this(other.name,other.imagePath,other.iconPath,other.label,other.metaData,other.discount);
    }

    public String getMeta(){
        return metaData;
    }

    public static Discount parseDiscountFromJson(JSONObject jsonObject, String profileName) {
        String name, label, meta;
        name = label = meta = "undefined";
        String image = null, icon = null;
        int price = 0;
        try {
            name = jsonObject.get("name").toString();
            image = Application.parseFilePath((jsonObject.get("imgPath").toString()));
            label = jsonObject.get("label").toString();
            meta = jsonObject.get("meta").toString();
            icon = Application.parseFilePath(jsonObject.get("icon").toString());
            price = Integer.parseInt(jsonObject.get("discount").toString());
        } catch (Exception other){
            //Show warning message
            JOptionPane.showMessageDialog(new JFrame(),profileName+ ":\nA(z) '" + name +
                    "' kedvezmény importálása közben hiba történt.\n" +
                    "Az importálás nem sikerült. Részletek:\n" + other.toString(),"Hiba",ERROR_MESSAGE);
        }
        return new Discount(name,image,icon,label,meta,price);
    }


    /**
     * Creates a panel, where the Discount's data is visualized to the user.
     * It is used to create the side menu of the application
     * @return a panel containing the Discount information
     */
    public BarcodePanel getBarcodePanel(){
        return barcode.createBarcodePanel();
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
        return other.metaData.equals(metaData);
    }

    public String getIcon(){
        return iconPath;
    }

    @Override
    public ModifierDialog getModifierWizard(Window parent) {
        return new DiscountWizard(parent);
    }

    public int getPrice() {
        return discount;
    }

    public static class DiscountListener extends ModifierWizardEditor<Discount>{

        public DiscountListener(Window parent) {
            super(parent);
        }

        @Override
        public void createNew(List<Discount> objectList) {
            Discount newDiscount = new Discount("",null,basicIcon,"","",0);
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

        private ImagePanel panelImg = new ImagePanel(imagePath);
        private JLabel labelIcon = new JLabel(new ImageIcon(new ImageIcon(iconPath).getImage().getScaledInstance(50,50,Image.SCALE_SMOOTH)));
        private JTextField tfName = new JTextField(name);
        private JTextField tfMeta = new JTextField(metaData);
        private JTextField tfTooltip = new JTextField(label);
        private JSpinner spPrice = new JSpinner(new SpinnerNumberModel(0, Short.MIN_VALUE,Short.MAX_VALUE,1));
        private JButton btnReadPic = new JButton("Olvass!");

        /**
         * Default constructor.
         * It creates a separate window for the user to modify the values.
         */
        DiscountWizard(Window parent){
            super(parent,"Kedvezmény beállítása");

            body.setLayout(new GridBagLayout());
            //Set textfield value
            spPrice.setValue(discount);
            labelIcon.setVerticalTextPosition(SwingConstants.TOP);
            labelIcon.setHorizontalTextPosition(SwingConstants.RIGHT);
            //Browse button
            JButton btnBrowse = new JButton("Tallózás");

            btnBrowse.addActionListener(e -> selectPicture(panelImg));
            btnSave.addActionListener(e -> saveDiscount());

            //Assemble Window
            body.add(new JLabel("Név"),setConstraints(0,0,1,1));
            body.add(new JLabel("Hozzájárulás"),setConstraints(0,1,1,1));
            body.add(new JLabel("META"),setConstraints(0,2,1,1));
            body.add(new JLabel("Tooltip"),setConstraints(0,3,1,1));
            body.add(new JLabel("Vonalkód képe:"),setConstraints(0,4,1,1));
            body.add(new JLabel(imagePath),setConstraints(2,4,2,1));
            body.add(new JLabel(iconPath),setConstraints(2,0,1,1));

            body.add(tfName,setConstraints(1,0,1,1));
            body.add(spPrice,setConstraints(1,1,1,1));
            body.add(tfMeta,setConstraints(1,2,1,1));
            body.add(tfTooltip,setConstraints(1,3,3,1));
            body.add(labelIcon,setConstraints(3,0,1,3));
            body.add(new JButton("..."),setConstraints(2,1,1,1));
            body.add(btnReadPic,setConstraints(2,2,1,1));
            body.add(btnBrowse,setConstraints(1,4,1,1));
            body.add(panelImg,setConstraints(0,5,4,2));

            finishDialog(parent);
        }

        private void saveDiscount(){
            if(tfName.getText().length() > 0 &&
                    tfMeta.getText().length() > 0 &&
                    tfTooltip.getText().length() > 0 &&
                    panelImg.validatePicture()){
                name = tfName.getText();
                metaData = tfMeta.getText();
                label = tfTooltip.getText();
                discount = Integer.parseInt(spPrice.getValue().toString());
                imagePath = panelImg.getPath();
                result = 0;
                //Close dialog
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,"Not a valid discount!","ERROR",JOptionPane.ERROR_MESSAGE);
            }
        }

    }


}
