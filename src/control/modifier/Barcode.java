package control.modifier;


import view.ImagePanel;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.List;

public class Barcode implements Serializable, Modifier{
    private String name;
    private String picturePath;
    private String description;


    public Barcode(String name){
        this.name = name;
    }

    public JPanel getBarcodePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));
        panel.add(new ImagePanel(picturePath));
        panel.add(new JLabel(description));
        return panel;
    }

    @Override
    public String toString(){
        return name;
    }

    @Override
    public ModifierDialog getModifierWizard(Window parent) {
        return new BarcodeWizard(parent);
    }

    public static class BarcodeListener extends ModifierWizardEditor<Barcode> {

        public BarcodeListener(Window parent) {
            super(parent);
        }

        @Override
        public void createNew(List<Barcode> objectList) {
            Barcode newCode = new Barcode(null);
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
        private JButton btnSetPic = new JButton("Kép módosítása");

        BarcodeWizard(Window parent) {
            //Set super
            super(parent, "Vonalkód módosítása");
            btnSave.addActionListener(e->saveBarcode());

            //Setup buttons
            btnSetPic.addActionListener(e -> selectPicture(panelImg));

            body.setLayout(new GridBagLayout());
            //Barcode image
            body.add(panelImg,setConstraints(0,0,2,1));
            body.add(btnSetPic,setConstraints(0,1,2,1));
            //Barcode name
            body.add(new JLabel("Név"),setConstraints(0,2,1,1));
            body.add(tfName,setConstraints(1,2,1,1));
            //Barcode description
            body.add(new JLabel("Leírás"),setConstraints(0,3,1,1));
            body.add(tfTooltip,setConstraints(1,3,1,1));

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
