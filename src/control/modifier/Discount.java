package control.modifier;

import control.utility.BarcodeReader;
import control.utility.DefaultBarcodeListener;
import control.utility.file.ExtensionFilter;
import data.EntryProfile;
import org.json.simple.JSONObject;
import view.ImagePanel;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

import static control.modifier.ModifierDialog.setConstraints;
import static javax.swing.JOptionPane.ERROR_MESSAGE;

public class Discount implements Serializable, Modifier {

    private static final String basicIcon = "Icons\\BasicIcon.png";
    private String name;
    private String imagePath;
    private String iconPath;
    private String label;
    private String metaData;
    private int discount;

    private Discount(String name, String imagePath, String iconPath, String label, String meta, int price){
        this.name = name;
        this.imagePath = imagePath;
        this.label = label;
        this.iconPath = iconPath;
        metaData = meta;
        discount = price;
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
            image = jsonObject.get("imgPath").toString();
            label = jsonObject.get("label").toString();
            meta = jsonObject.get("meta").toString();
            icon = jsonObject.get("icon").toString();
            price = Integer.parseInt(jsonObject.get("discount").toString());
        } catch (Exception other){
            //Show warning message
            JOptionPane.showMessageDialog(new JFrame(),profileName+ ":\nA(z) '" + name +
                    "' kedvezmény importálása közben hiba történt.\n" +
                    "Az importálás nem sikerült. Részletek:\n" + other.toString(),"Hiba",ERROR_MESSAGE);
        }
        return new Discount(name,image,icon,label,meta,price);
    }

    public static JDialog createDiscountFromWizard(Frame parent, EntryProfile profile){
        Discount discount = new Discount("",null,basicIcon,"","",0);
        return discount.getTypeWizard(parent, profile,-1);
    }

    /**
     * Creates a panel, where the Discount's data is visualized to the user.
     * It is used to create the side menu of the application
     * @return a panel containing the Discount information
     */
    public JPanel getDiscountPanel(){
        JPanel panelDiscount = new JPanel();
        panelDiscount.setLayout(new BoxLayout(panelDiscount,BoxLayout.PAGE_AXIS));

        //Creating components
        JLabel lbTooltip = new JLabel(label);
        lbTooltip.setFont(new Font(lbTooltip.getFont().getName(),Font.PLAIN,20));
        lbTooltip.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbTooltip.setToolTipText(name);
        //Setting up panel
        panelDiscount.add(new ImagePanel(imagePath));
        panelDiscount.add(lbTooltip);

        System.out.println("Loaded discount: " + name);

        return panelDiscount;
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
    public JDialog getTypeWizard(Window parent, EntryProfile profile, int index) {
        return new DiscountWizard(parent,profile,index);
    }

    public static class DiscountListener extends ModifierWizardEditor<Discount>{

        public DiscountListener(Window parent, EntryProfile profile) {
            super(parent, profile);
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
         * @param profile the EntryProfile which writes back the modified data
         * @param index the index of the Discount in the profile's list (-1 for new discounts)
         */
        DiscountWizard(Window parent, EntryProfile profile,int index){
            super(parent,profile,index,"Kedvezmény beállítása");

            body.setLayout(new GridBagLayout());
            //Set textfield value
            spPrice.setValue(discount);
            labelIcon.setVerticalTextPosition(SwingConstants.TOP);
            labelIcon.setHorizontalTextPosition(SwingConstants.RIGHT);
            //Browse button
            JButton btnBrowse = new JButton("Tallózás");

            btnBrowse.addActionListener(e -> changePicture());
            btnSave.addActionListener(e -> saveDiscount(profile,index));
            btnReadPic.addActionListener(e -> {
                DefaultBarcodeListener dbl = new DefaultBarcodeListener(tfMeta::setText);
                BarcodeReader reader = new BarcodeReader();
                reader.addListener(dbl);
            });

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

        private void saveDiscount(EntryProfile profile, int index){
//            if(tfName.getText().length() > 0 &&
//                    tfMeta.getText().length() > 0 &&
//                    tfTooltip.getText().length() > 0 &&
//                    panelImg.validatePicture()){
//                name = tfName.getText();
//                metaData = tfMeta.getText();
//                label = tfTooltip.getText();
//                discount = Integer.parseInt(tfPrice.getValue().toString());
//                imagePath = panelImg.getPath();
//                //Save modifications in profile
//                //profile.modifyDiscount(index,Discount.this);
//                //Close dialog
//                dispose();
//            } else {
//                JOptionPane.showMessageDialog(this,"Not a valid discount!","ERROR",JOptionPane.ERROR_MESSAGE);
//            }
        }

        private void changePicture() {
            JFileChooser fc = new JFileChooser();
            fc.setAcceptAllFileFilterUsed(false);
            fc.addChoosableFileFilter(new ExtensionFilter(new String[]{"png", "jpg", "jpeg", "bmp", "gif"}, "Minden Képfájl"));
            fc.addChoosableFileFilter(new ExtensionFilter(new String[]{"png"}, "Portable Network Graphics (.png)"));
            fc.addChoosableFileFilter(new ExtensionFilter(new String[]{"jpg", "jpeg"}, "Joint Photographic Experts Group (.jpg, .jpeg)"));
            fc.addChoosableFileFilter(new ExtensionFilter(new String[]{"bmp"}, "Bitmap (.bmp)"));
            fc.addChoosableFileFilter(new ExtensionFilter(new String[]{"gif"}, "Graphics Interchange Format (.gif)"));
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                swapPicture(fc.getSelectedFile().getAbsolutePath());
            }
        }

        private void swapPicture(String imagePath){
            body.remove(panelImg);
            panelImg = new ImagePanel(imagePath);
            panelImg.setAlignmentX(Component.LEFT_ALIGNMENT);
            body.add(panelImg,setConstraints(0,5,4,2));
            //Resize window (since the user cannot do it)
            setResizable(true);
            pack();
            setResizable(false);
        }

    }


}
