//package Control.EntryModifier;
//
//import Control.EntryProfile;
//import Control.Utility.ExtensionFilter;
//import ImagePanel;
//import org.json.simple.JSONObject;
//
//import javax.swing.*;
//import java.awt.*;
//import java.text.NumberFormat;
//
//import static Window.Main.ScreenLocation.CENTER;
//import static Window.Main.setRelativeLocationOnScreen;
//
///**
// * An entry modifier. Upon use it modifies the price of an entry.
// */
//public class Discount {
//
//    private String name;
//    private String imagePath;
//    private String label;
//    private String metaData;
//    private int discount;
//
//    private Discount(String name, String imagePath, String label, String meta, int price){
//        this.name = name;
//        this.imagePath = imagePath;
//        this.label = label;
//        metaData = meta;
//        discount = price;
//    }
//
//    public Discount(Discount other) {
//        this(other.name,other.imagePath,other.label,other.metaData,other.discount);
//    }
//
//    public static Discount parseDiscountFromJson(JSONObject jsonObject) {
//        //TODO: catching parse exceptions
//        String name = jsonObject.get("name").toString();
//        String image = jsonObject.get("imgPath").toString();
//        String label = jsonObject.get("label").toString();
//        String meta = jsonObject.get("meta").toString();
//        int price = Integer.parseInt(jsonObject.get("discount").toString());
//        return new Discount(name,image,label,meta,price);
//    }
//
//    public static JDialog createDiscountFromWizard(EntryProfile profile){
//        Discount discount = new Discount("",null,"","",0);
//        return discount.getDiscountWizard(profile,-1);
//    }
//
//    public JDialog getDiscountWizard(EntryProfile profile,int index){
//        return new DiscountWizard(profile,index);
//    }
//
//    /**
//     * Creates a panel, where the Discount's data is visualized to the user.
//     * It is used to create the side menu of the application
//     * @return a panel containing the Discount information
//     */
//    public JPanel getDiscountPanel(){
//        JPanel panelDiscount = new JPanel();
//        panelDiscount.setLayout(new BoxLayout(panelDiscount,BoxLayout.PAGE_AXIS));
//
//        //Creating components
//        JLabel lbTooltip = new JLabel(label);
//        lbTooltip.setFont(new Font(lbTooltip.getFont().getName(),Font.PLAIN,20));
//        lbTooltip.setAlignmentX(Component.CENTER_ALIGNMENT);
//        lbTooltip.setToolTipText(name);
//        //Setting up panel
//        panelDiscount.add(new ImagePanel(imagePath));
//        panelDiscount.add(lbTooltip);
//
//        System.out.println("Loaded discount: " + name);
//
//        return panelDiscount;
//    }
//
//    public String getMeta() {
//        return metaData;
//    }
//
//    @Override
//    public String toString(){
//        return name;
//    }
//
//
//    /**
//     * An inner class of the Discount.
//     * It is responsible for creating or modifying Discounts.
//     */
//    private class DiscountWizard extends JDialog {
//        /**
//         * The image of the barcode
//         */
//        private ImagePanel panelImg = new ImagePanel(imagePath);
//        /**
//         * The field containing the name of the Discount
//         */
//        private JTextField fieldName = new JTextField(name);
//        /**
//         * The field containing the metadata of the Discount (the barcode's string data)
//         */
//        private JTextField fieldMeta = new JTextField(metaData);
//        /**
//         * The field containing the tooltip of the Discount
//         */
//        private JTextField fieldTooltip = new JTextField(label);
//        /**
//         * The field containing the price of the Discount
//         */
//        private JFormattedTextField fieldPrice = new JFormattedTextField(NumberFormat.getNumberInstance());
//
//        /**
//         * Default constructor.
//         * It creates a separate window for the user to modify the values.
//         * @param profile the EntryProfile which writes back the modified data
//         * @param index the index of the Discount in the profile's list (-1 for new discounts)
//         */
//        DiscountWizard(EntryProfile profile,int index){
//            //Set basic window attributes
//            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
//            setModal(true);
//            setTitle("Kedvezmény beállítása");
//            setLayout(new BoxLayout(getContentPane(),BoxLayout.PAGE_AXIS));
//
//            //Set textfield value
//            fieldPrice.setValue(discount);
//            //Browse button
//            JButton btnBrowse = new JButton("Tallózás");
//            btnBrowse.addActionListener(e -> changePicture());
//            //Accept and Cancel buttons
//            JButton btnCancel = new JButton("Mégse");
//            btnCancel.addActionListener(e -> dispose());
//            JButton btnSave = new JButton("Mentés");
//            btnSave.addActionListener(e -> saveDiscount(profile,index));
//            JPanel panelDialog = new JPanel();
//            panelDialog.add(btnSave);
//            panelDialog.add(btnCancel);
//
//            //Assemble Window
//            add(new JLabel("Név"));
//            add(fieldName);
//            add(new JLabel("META"));
//            add(fieldMeta);
//            add(new JLabel("Tooltip"));
//            add(fieldTooltip);
//            add(new JLabel("Kedvezmény"));
//            add(fieldPrice);
//            add(new JLabel("Vonalkód képe"));
//            add(btnBrowse);
//            add(panelImg);
//            add(panelDialog);
//            //Set alignment
//            for (Component c: getContentPane().getComponents()) {
//                ((JComponent)c).setAlignmentX(Component.LEFT_ALIGNMENT);
//            }
//            //Pack
//            pack();
//            setResizable(false);
//            setRelativeLocationOnScreen(this, CENTER);
//        }
//
//        private void saveDiscount(EntryProfile profile, int index){
//            if(fieldName.getText().length() > 0 &&
//                    fieldMeta.getText().length() > 0 &&
//                    fieldTooltip.getText().length() > 0 &&
//                    panelImg.validatePicture()){
//                name = fieldName.getText();
//                metaData = fieldMeta.getText();
//                label = fieldTooltip.getText();
//                discount = Integer.parseInt(fieldPrice.getValue().toString());
//                imagePath = panelImg.getPath();
//                //Save modifications in profile
//                profile.modifyDiscount(index,Discount.this);
//                //Close dialog
//                dispose();
//            } else {
//                JOptionPane.showMessageDialog(this,"Not a valid discount!","ERROR",JOptionPane.ERROR_MESSAGE);
//            }
//        }
//
//        private void changePicture() {
//            JFileChooser fc = new JFileChooser();
//            fc.setAcceptAllFileFilterUsed(false);
//            fc.addChoosableFileFilter(new ExtensionFilter(new String[]{"png", "jpg", "jpeg", "bmp", "gif"}, "Minden Képfájl"));
//            fc.addChoosableFileFilter(new ExtensionFilter(new String[]{"png"}, "Portable Network Graphics (.png)"));
//            fc.addChoosableFileFilter(new ExtensionFilter(new String[]{"jpg", "jpeg"}, "Joint Photographic Experts Group (.jpg, .jpeg)"));
//            fc.addChoosableFileFilter(new ExtensionFilter(new String[]{"bmp"}, "Bitmap (.bmp)"));
//            fc.addChoosableFileFilter(new ExtensionFilter(new String[]{"gif"}, "Graphics Interchange Format (.gif)"));
//            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
//                swapPicture(fc.getSelectedFile().getAbsolutePath());
//            }
//        }
//
//        private void swapPicture(String imagePath){
//            remove(panelImg);
//            panelImg = new ImagePanel(imagePath);
//            panelImg.setAlignmentX(Component.LEFT_ALIGNMENT);
//            add(panelImg,9);
//            //Resize window (since the user cannot do it)
//            setResizable(true);
//            pack();
//            setResizable(false);
//        }
//
//    }
//
//
//}
