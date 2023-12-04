package view.main.panel.wizard.barcode;

import control.modifier.BarcodeEditor;
import control.utility.devices.BasicSerialPortReader;
import control.wizard.WizardEditor;
import data.modifier.Barcode;
import view.main.panel.AbstractPanel;
import view.main.panel.utility.JImagePanel;
import view.main.panel.utility.LabeledComponent;
import view.main.panel.wizard.WizardPage;
import view.validation.ComponentValidator;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static javax.swing.JFileChooser.APPROVE_OPTION;
import static view.helper.DialogCreator.getPictureChooser;

public class BarcodePanel extends AbstractPanel implements WizardPage<Barcode> {

    private final JImagePanel imgBarcodePicture;
    private final JTextField tfBarcode;
    private final LabeledComponent<JButton> compBtnBrowse;
    private final LabeledComponent<JTextField> compName;
    private final LabeledComponent<JTextField> compDescription;

    public BarcodePanel() {
        imgBarcodePicture = new JImagePanel(null);
        compBtnBrowse = new LabeledComponent<>("", new JButton("Tallózás"));
        compBtnBrowse.getComponent().addActionListener(this::selectBarcodePicture);

        compName = new LabeledComponent<>("Név:", new JTextField(TEXT_PANEL_DEFAULT_WIDTH));
        compDescription = new LabeledComponent<>("Leírás:", new JTextField(TEXT_PANEL_DEFAULT_WIDTH));

        tfBarcode = new JTextField(TEXT_PANEL_DEFAULT_WIDTH);
        tfBarcode.setEditable(false);
        tfBarcode.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private int choosePictureFromDialog() {
        JFileChooser fc = getPictureChooser();
        int dialogResult = fc.showOpenDialog(this);
        if (dialogResult == JFileChooser.APPROVE_OPTION) {
            imgBarcodePicture.changePicture(fc.getSelectedFile().getAbsolutePath());
        }
        return dialogResult;
    }

    private void selectBarcodePicture(ActionEvent event) {

        if(choosePictureFromDialog() == APPROVE_OPTION) {
            compBtnBrowse.getLabel().setText(imgBarcodePicture.getPath());
            tfBarcode.setText(null);
            
            BasicSerialPortReader reader = new BasicSerialPortReader();
            reader.addReceiver(tfBarcode::setText);
            reader.readSerialPort();
        }
    }

    @Override
    public void initializeLayout() {
        // Horizontal layout
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addComponent(imgBarcodePicture)
                        .addComponent(tfBarcode)
                        .addGroup(compBtnBrowse.createSequentialLayout(layout))
                        .addGroup(compName.createSequentialLayout(layout))
                        .addGroup(compDescription.createSequentialLayout(layout))
        );

        // Vertical layout
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(imgBarcodePicture)
                        .addComponent(tfBarcode)
                        .addGroup(compBtnBrowse.createParallelLayout(layout))
                        .addGroup(compName.createParallelLayout(layout))
                        .addGroup(compDescription.createParallelLayout(layout))
        );

        layout.linkSize(compName.getComponent(), compDescription.getComponent());
        layout.linkSize(SwingConstants.VERTICAL, compName.getComponent(), tfBarcode);
    }

    @Override
    public void refreshPage(Barcode model) {
        compName.getComponent().setText(model.getName());
        compDescription.getComponent().setText(model.getDescription());
        tfBarcode.setText(model.getMetaData());
        compBtnBrowse.getLabel().setText(model.getPicturePath());
        imgBarcodePicture.changePicture(model.getPicturePath());
    }

    @Override
    public void saveData(WizardEditor<Barcode> controller) {
        if (controller instanceof BarcodeEditor editor) {
            editor.setBarcodeName(compName.getComponent().getText());
            editor.setBarcodeDescription(compDescription.getComponent().getText());
            editor.setBarcodeMetadata(tfBarcode.getText());
            editor.setBarcodeImagePath(compBtnBrowse.getLabel().getText());
        }
    }

    @Override
    public void setupValidation(ComponentValidator validator) {
        validator.addComponent(compName.getComponent(), this::isNameValid, "Név nem lehet üres");
        validator.addComponent(compDescription.getComponent(), this::isDescriptionValid, "Leírás nem lehet üres");
        validator.addComponent(imgBarcodePicture, imgBarcodePicture::validatePicture, "Válassz egy képet a vonalkódhoz");
        validator.addComponent(tfBarcode, this::isMetaValid, "Nem regisztrálhatsz nem beolvasott vonalkódot!");
//        validator.addComponent(compName.getComponent(), null, "");
    }

    @Override
    public AbstractPanel getWizardEditPanel() {
        return this;
    }

    private boolean isNameValid() {
        return !compName.getComponent().getText().isBlank();
    }

    private boolean isMetaValid() {
        return !tfBarcode.getText().isBlank();
    }

    private boolean isDescriptionValid() {
        return !compDescription.getComponent().getText().isBlank();
    }
}
