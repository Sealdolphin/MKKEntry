package view.main.panel.wizard.barcode;

import control.modifier.BarcodeEditor;
import control.wizard.WizardEditor;
import data.modifier.Barcode;
import view.ImagePanel;
import view.main.panel.AbstractPanel;
import view.main.panel.utility.LabeledComponent;
import view.main.panel.wizard.WizardPage;
import view.validation.ComponentValidator;

import javax.swing.*;

public class BarcodePanel extends AbstractPanel implements WizardPage<Barcode> {

    private final ImagePanel imgBarcodePicture;
    private final JTextField tfBarcode;
    private final LabeledComponent<JButton> compBtnBrowse;
    private final LabeledComponent<JTextField> compName;
    private final LabeledComponent<JTextField> compDescription;

    public BarcodePanel(ComponentValidator validator) {
        imgBarcodePicture = new ImagePanel(null);
        compBtnBrowse = new LabeledComponent<>("", new JButton("Tallózás"));

        compName = new LabeledComponent<>("Név:", new JTextField(TEXT_PANEL_DEFAULT_WIDTH));
        compDescription = new LabeledComponent<>("Leírás:", new JTextField(TEXT_PANEL_DEFAULT_WIDTH));

        tfBarcode = new JTextField(TEXT_PANEL_DEFAULT_WIDTH);
        tfBarcode.setEditable(false);
        tfBarcode.setHorizontalAlignment(SwingConstants.CENTER);

        validator.addComponent(compName.getComponent(), this::isNameValid, "Név nem lehet üres");
        validator.addComponent(compDescription.getComponent(), this::isDescriptionValid, "Leírás nem lehet üres");
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


    private boolean isNameValid() {
        return !compName.getComponent().getText().isBlank();
    }

    private boolean isDescriptionValid() {
        return !compDescription.getComponent().getText().isBlank();
    }
}
