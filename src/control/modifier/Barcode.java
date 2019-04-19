package control.modifier;

import view.ImagePanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Barcode implements Modifier{
    private String name;
    private String picturePath;
    private String toolTip;
    private String description;


    public Barcode(String name){
        this.name = name;
    }

    public JPanel getBarcodePanel() {

        return null;
    }

    @Override
    public String toString(){
        return name;
    }

    @Override
    public ModifierDialog getModifierWizard(Window parent) {
        return new BarcodeCreator(parent);
    }

    public static class BarcodeReaderListener extends ModifierWizardEditor<Barcode> {

        public BarcodeReaderListener(Window parent) {
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

    private class BarcodeCreator extends ModifierDialog {

        private ImagePanel panelImg = new ImagePanel(picturePath);
        private JTextField tfName = new JTextField(name);
        private JTextField tfTooltip = new JTextField(description);
        private JButton btnSetPic = new JButton("Kép módosítása");

        BarcodeCreator(Window parent) {
            super(parent, "Vonalkód módosítása");
            body.setLayout(new GridBagLayout());

            body.add(panelImg,setConstraints(0,0,2,1));

            body.add(btnSetPic,setConstraints(0,1,2,1));

            body.add(new JLabel("Név"),setConstraints(0,2,1,1));
            body.add(tfName,setConstraints(1,2,1,1));

            body.add(new JLabel("Leírás"),setConstraints(0,3,1,1));
            body.add(tfTooltip,setConstraints(1,3,1,1));

            finishDialog(parent);

        }
    }

}
