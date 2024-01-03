package data.modifier;


import control.modifier.BarcodeEditor;
import control.modifier.Modifier;
import control.modifier.ModifierDialog;
import control.modifier.ModifierWizardEditor;
import control.utility.devices.BarCodeReaderListenerFactory;
import control.wizard.WizardEditor;
import data.wizard.WizardType;
import org.json.JSONObject;
import view.BarcodePanel;
import view.main.panel.utility.JImagePanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import static javax.swing.JFileChooser.APPROVE_OPTION;

public class Barcode implements Serializable, Modifier, WizardType {

    private final UUID id = UUID.randomUUID();

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

    public Barcode(Barcode other) {
        setName(other.getName());
        setMetaData(other.getMetaData());
        setImagePath(other.getImagePath());
        setDescription(other.getDescription());
        setLink(other.hasLink());
    }

    @Deprecated
    public BarcodePanel createBarcodePanel() {
        return new BarcodePanel(getImagePath(), getDescription());
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null) return false;
        if(obj == this) return true;
        if(!obj.getClass().equals(Barcode.class)) return false;
        Barcode other = (Barcode) obj;
        if (metaData == null) {
            return other.getMetaData() == null;
        }
        return getMetaData().equals(other.getMetaData());
    }

    @Deprecated
    @Override
    public ModifierDialog getModifierWizard(Window parent) {
        return new BarcodeWizard(parent);
    }

    @Deprecated
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
    public String getImagePath() {
        return picturePath;
    }

    public static Barcode parseBarcodeFromJSON(JSONObject jsonObject){
        Barcode barcode = new Barcode();
        barcode.setName(jsonObject.optString("name", "undefined"));
        barcode.setMetaData(jsonObject.optString("meta", "UNKNOWN"));
        barcode.setDescription(jsonObject.optString("description", ""));
        barcode.setImagePath("Barcodes" + File.separator + jsonObject.getString("imagePath"));
        return barcode;
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

    @Override
    public String getId() {
        return id.toString();
    }

    @Override
    public WizardEditor<Barcode> createWizard() {
        return new BarcodeEditor(this, new view.main.panel.wizard.barcode.BarcodePanel());
    }

    @Deprecated
    public static class BarcodeListener extends ModifierWizardEditor<Barcode> {

        public BarcodeListener(Window parent) {
            super(parent);
        }

        @Override
        public void createNew(List<Barcode> objectList) {
            Barcode newCode = new Barcode();
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

        private final JImagePanel panelImg = new JImagePanel(getImagePath());
        private final JTextField tfName = new JTextField(name);
        private final JTextField tfTooltip = new JTextField(getDescription());
        private final JTextField tfMetaData = new JTextField(getMetaData());

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
