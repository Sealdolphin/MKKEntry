package control.modifier;


import view.BarcodePanel;
import view.ImagePanel;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.List;

import static javax.swing.JFileChooser.APPROVE_OPTION;

public class Barcode implements Serializable, Modifier {
    private String name;
    private String metaData;
    private String picturePath = null;
    private String description = "Unknown";
    private boolean hasLink = false;

    public Barcode(String name){
        this.name = name;
    }

    public Barcode(Barcode other) {
        name = other.name;
        metaData = other.metaData;
        picturePath = other.picturePath;
        description = other.description;
    }

    public BarcodePanel createBarcodePanel() {
        return new BarcodePanel(picturePath,description);
    }

    @Override
    public String toString(){
        return name;
    }

    @Override
    public ModifierDialog getModifierWizard(Window parent) {
        return new BarcodeWizard(parent);
    }

    public void setLink(boolean link) {
        hasLink = link;
    }

    public boolean hasLink() {
        return hasLink;
    }

    public String getMeta() {
        return metaData;
    }

    public static class BarcodeListener extends ModifierWizardEditor<Barcode> {

        public BarcodeListener(Window parent) {
            super(parent);
        }

        @Override
        public void createNew(List<Barcode> objectList) {
            Barcode newCode = new Barcode("");
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
    private class BarcodeWizard extends ModifierDialog {

        private ImagePanel panelImg = new ImagePanel(picturePath);
        private JTextField tfName = new JTextField(name);
        private JTextField tfTooltip = new JTextField(description);
        private JTextField tfMetaData = new JTextField(metaData);
        private JButton btnSetPic = new JButton("Kép módosítása");

        BarcodeWizard(Window parent) {
            //Set super
            super(parent, "Vonalkód módosítása");
            btnSave.addActionListener(e->saveBarcode());

            //Setup components
            tfMetaData.setEditable(false);
            btnSetPic.addActionListener(e -> {
                if(selectPicture(panelImg) == APPROVE_OPTION){
                    metaData = null;
                    while (metaData == null) {
                        //Read new code TODO: insert scanning mode here
                        metaData = JOptionPane.showInputDialog(null,"Írd be, vagy szkenneld be a kódot","Kódbeolvasás",JOptionPane.INFORMATION_MESSAGE);
                        tfMetaData.setText(metaData);
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
            picturePath = panelImg.getPath();
            name = tfName.getText();
            description = tfTooltip.getText();
            super.result = 0;
            dispose();
        }


    }

}
