package view.main.panel.wizard.barcode.panel;

import control.modifier.Barcode;
import view.ImagePanel;
import view.main.panel.AbstractPanel;
import view.main.panel.utility.LabeledComponent;
import view.main.panel.wizard.Wizard;
import view.main.panel.wizard.WizardPage;

import javax.swing.*;

public class BarcodeEditPanel extends AbstractPanel implements WizardPage<Barcode> {

    private final ImagePanel imgBarcodePicture;
    private final JTextField tfBarcode;
    private final LabeledComponent<JButton> compBtnBrowse;
    private final LabeledComponent<JTextField> compName;
    private final LabeledComponent<JTextField> compDescription;

    private final JButton btnSave;
    private final JButton btnCancel;

    public BarcodeEditPanel(Wizard parent) {
        imgBarcodePicture = new ImagePanel(null);
        compBtnBrowse = new LabeledComponent<>("/vonalkodok/vonalkod.jpg", new JButton("Tallózás"));

        compName = new LabeledComponent<>("Név:", new JTextField(TEXT_PANEL_DEFAULT_WIDTH));
        compDescription = new LabeledComponent<>("Leírás:", new JTextField(TEXT_PANEL_DEFAULT_WIDTH));
        tfBarcode = new JTextField(TEXT_PANEL_DEFAULT_WIDTH);

        btnSave = new JButton("Mentés");
        btnCancel = new JButton("Mégsem");

        tfBarcode.setEditable(false);
        tfBarcode.setHorizontalAlignment(SwingConstants.CENTER);
        tfBarcode.setText("BARCODE");

        btnSave.addActionListener(parent::doSaveEntity);
        btnCancel.addActionListener(parent::cancelEditing);

        validator.addComponent(compName.getComponent(), this::isNameValid, "");
        validator.addComponent(compDescription.getComponent(), this::isDescriptionValid, "");
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
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addComponent(btnSave)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnCancel)
                        )
        );

        // Vertical layout
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(imgBarcodePicture)
                        .addComponent(tfBarcode)
                        .addGroup(compBtnBrowse.createParallelLayout(layout))
                        .addGroup(compName.createParallelLayout(layout))
                        .addGroup(compDescription.createParallelLayout(layout))
                        .addGroup(
                                layout.createParallelGroup()
                                        .addComponent(btnSave)
                                        .addComponent(btnCancel)
                        )
        );

        layout.linkSize(compName.getComponent(), compDescription.getComponent());
    }

    @Override
    public boolean validateWizardPage() {
        validator.validate();

        return false;
    }

    @Override
    public Barcode generateWizardType() {
        return null;
    }

    @Override
    public void refreshPage(Barcode model) {
        compName.getComponent().setText(model.toString());
        compDescription.getComponent().setText(model.getDescription());
        tfBarcode.setText(model.getMetaData());
        compBtnBrowse.getLabel().setText(model.getPicturePath());
    }

    private boolean isNameValid() {
        return !compName.getComponent().getText().isBlank();
    }

    private boolean isDescriptionValid() {
        return !compDescription.getComponent().getText().isBlank();
    }
}
