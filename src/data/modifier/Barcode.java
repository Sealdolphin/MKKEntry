package data.modifier;


import control.modifier.Modifier;
import control.modifier.ModifierDialog;
import control.modifier.ModifierWizardEditor;
import control.utility.devices.BarCodeReaderListenerFactory;
import data.wizard.WizardType;
import org.json.simple.JSONObject;
import view.BarcodePanel;
import view.ImagePanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.Serializable;
import java.util.List;

import static javax.swing.JFileChooser.APPROVE_OPTION;

public class Barcode implements Serializable, Modifier, WizardType {
    /**
     * The name of the barcode
     */
    private String name;
    /**
     * The data of the barcode
     */
    private String metaData;
    /**
     * The image path of the picture containing the barcode
     */
    private String picturePath;
    /**
     * Short description of the barcode
     */
    private String description;

    @Deprecated
    private boolean hasLink = false;

    public Barcode() {}

    @Deprecated
    public Barcode(String name, String meta, String picture, String desc){
        this.setName(name);
        setMetaData(meta);
        setImagePath(picture);
        setDescription(desc);
    }

    public Barcode(Barcode other) {
        setName(other.getName());
        setMetaData(other.getMetaData());
        setImagePath(other.getPicturePath());
        setDescription(other.getDescription());
    }

    public BarcodePanel createBarcodePanel() {
        return new BarcodePanel(getPicturePath(), getDescription());
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null) return false;
        if(obj == this) return true;
        if(!obj.getClass().equals(Barcode.class)) return false;
        Barcode other = (Barcode) obj;
        return getMetaData().equals(other.getMetaData());
    }

    @Override
    public ModifierDialog getModifierWizard(Window parent) {
        return new BarcodeWizard(parent);
    }

    @Override
    public boolean validate() {
        return getName() != null && !getName().isEmpty() && getMetaData() != null && !getMetaData().isEmpty();
    }

    @Deprecated
    public void setLink(boolean link) {
        hasLink = link;
    }

    @Deprecated
    public boolean hasLink() {
        return hasLink;
    }

    /**
     * The data of the barcode
     */
    public String getMetaData() {
        return metaData;
    }

    /**
     * Short description of the barcode
     */
    public String getDescription() {
        return description;
    }

    /**
     * The image path of the picture containing the barcode
     */
    public String getPicturePath() {
        return picturePath;
    }

    public static Barcode parseBarcodeFromJSON(JSONObject jsonObject){
        String name, meta, desc, picture;
        name = jsonObject.get("name").toString();
        meta = jsonObject.get("meta").toString();
        desc = jsonObject.get("description").toString();
        picture = "Barcodes" + File.separator + jsonObject.get("imagePath").toString();
        return new Barcode(name,meta,picture,desc);
    }

    /**
     * The name of the barcode
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }

    public void setImagePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Deprecated
    public static class BarcodeListener extends ModifierWizardEditor<Barcode> {

        public BarcodeListener(Window parent) {
            super(parent);
        }

        @Override
        public void createNew(List<Barcode> objectList) {
            Barcode newCode = new Barcode("", "", "", "");
            ModifierDialog wizard = newCode.getModifierWizard(null);
            int result = wizard.open();
            if(result == 0){
                objectList.add(newCode);
            }
        }
    }

    /**
     * The Wizard class of the Barcode
     */
    @Deprecated
    private class BarcodeWizard extends ModifierDialog {

        private ImagePanel panelImg = new ImagePanel(getPicturePath());
        private JTextField tfName = new JTextField(name);
        private JTextField tfTooltip = new JTextField(getDescription());
        private JTextField tfMetaData = new JTextField(getMetaData());

        BarcodeWizard(Window parent) {
            //Set super
            super(parent, "Vonalkód módosítása");
            btnSave.addActionListener(e->saveBarcode());

            //Setup components
            tfMetaData.setEditable(false);
            JButton btnSetPic = new JButton("Kép módosítása");
            btnSetPic.addActionListener(e -> {
                if(selectPicture(panelImg) == APPROVE_OPTION){
                    setMetaData(null);
                    while (getMetaData() == null) {
                        //Read new code
                        this.setAlwaysOnTop(true);
                        BarCodeReaderListenerFactory.generateReader(tfMetaData::setText,"Szkenneld be a kódot (ESC a kilépéshez)!",true);
                        setMetaData(tfMetaData.getText());
                    }
                }
            });

            body.setLayout(new GridBagLayout());
            //Barcode image
            body.add(panelImg,setConstraints(0,0,2,1));
            body.add(btnSetPic,setConstraints(0,1,2,1));
            body.add(tfMetaData,setConstraints(0,2,2,1));
            //Barcode name
            body.add(new JLabel("Név"),setConstraints(0,3,1,1));
            body.add(tfName,setConstraints(1,3,1,1));
            //Barcode description
            body.add(new JLabel("Leírás"),setConstraints(0,4,1,1));
            body.add(tfTooltip,setConstraints(1,4,1,1));

            //Protocol!!!
            finishDialog(parent);
        }

        private void saveBarcode() {
            if(!tfMetaData.getText().isEmpty()) {
                setMetaData(tfMetaData.getText());
                setImagePath(panelImg.getPath());
                name = tfName.getText();
                setDescription(tfTooltip.getText());
                super.result = 0;
                dispose();
            } else {
                //ERROR MESSAGE
                JOptionPane.showMessageDialog(this,"A kitöltés érvénytelen","Hiba",JOptionPane.ERROR_MESSAGE);
            }
        }


    }

}
