package view.main.panel.wizard.discount;

import control.modifier.DiscountEditor;
import control.wizard.WizardEditor;
import data.modifier.Barcode;
import data.modifier.Discount;
import data.wizard.BarcodeModel;
import view.main.panel.AbstractPanel;
import view.main.panel.utility.LabeledComponent;
import view.main.panel.utility.LoadedIcon;
import view.main.panel.wizard.WizardPage;
import view.renderer.list.BarcodeListRenderer;
import view.validation.ComponentValidator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.io.File;

import static view.helper.DialogCreator.choosePictureFromDialog;

public class DiscountPanel extends AbstractPanel implements WizardPage<Discount> {

    /**
     * The default option for icons
     */
    private static final String basicIcon = "Icons"+ File.separator +"BasicIcon.png";
    private final JLabel lbPath;
    private final LabeledComponent<JTextField> compName;
    private final LabeledComponent<JComboBox<Barcode>> compBarcodes;
    private final LabeledComponent<JSpinner> compDiscount;
    private final JCheckBox cbFree;
    private final JButton btnChangeIcon;

    public static final int DEFAULT_ICON_SIZE = 50;

    public DiscountPanel() {
        lbPath = new JLabel(basicIcon);
        compName = new LabeledComponent<>("Név:", new JTextField(TEXT_PANEL_DEFAULT_WIDTH));
        compBarcodes = new LabeledComponent<>("Vonalkód:", new JComboBox<>());
        compDiscount = new LabeledComponent<>("Kedvezmény:", new JSpinner(new SpinnerNumberModel(0, Short.MIN_VALUE,Short.MAX_VALUE,1)));
        cbFree = new JCheckBox("Ingyenes");
        btnChangeIcon = new JButton(new LoadedIcon(basicIcon, DEFAULT_ICON_SIZE).getIcon());

        compBarcodes.getComponent().setRenderer(new BarcodeListRenderer());

        btnChangeIcon.setBorder(new EmptyBorder(5,5,5,5));
        btnChangeIcon.addActionListener(this::updateIconPath);
    }

    @Override
    public void refreshPage(Discount model) {
        compName.getComponent().setText(model.getName());
        cbFree.setSelected(model.isFree());
        compDiscount.getComponent().setValue(model.getDiscount());
        compBarcodes.getComponent().setSelectedItem(model.getBarcode());
        compBarcodes.getComponent().invalidate();
        refreshIcon(model.getIconPath());
    }

    @Override
    public void saveData(WizardEditor<Discount> controller) {
        if (controller instanceof DiscountEditor editor) {
            editor.setDiscountName(compName.getComponent().getText());
            editor.setDiscountIconPath(lbPath.getText());
            editor.setDiscountIsFree(cbFree.isSelected());
            editor.setDiscountValue((Integer) compDiscount.getComponent().getValue());
            editor.setDiscountBarcode((Barcode) compBarcodes.getComponent().getSelectedItem());
        }
    }

    @Override
    public void setupValidation(ComponentValidator validator) {
        validator.addComponent(compName.getComponent(), this::isNameValid, "Név nem lehet üres");
        validator.addComponent(compBarcodes.getComponent(), this::isBarcodeSelected, "Válassz egy vonalkódot!");
    }

    @Override
    public JComponent getIdentifyingComponent() {
        return compBarcodes.getComponent();
    }

    @Override
    public AbstractPanel getWizardEditPanel() {
        return this;
    }
    @Override
    public void initializeLayout() {
        // Horizontal layout
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(compName.createSequentialLayout(layout))
                        .addGroup(compDiscount.createSequentialLayout(layout))
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addComponent(lbPath)
                                        .addGap(0,0,Short.MAX_VALUE)
                                        .addComponent(btnChangeIcon)
                        )
                        .addComponent(cbFree)
                        .addGroup(compBarcodes.createParallelLayout(layout))
        );

        // Vertical layout
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(compName.createParallelLayout(layout))
                        .addGroup(compDiscount.createParallelLayout(layout))
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(lbPath)
                                        .addComponent(btnChangeIcon)
                        )
                        .addComponent(cbFree)
                        .addGroup(compBarcodes.createSequentialLayout(layout))
        );

        layout.linkSize(compName.getComponent(), compDiscount.getComponent());

    }

    private void updateIconPath(ActionEvent event) {
        if (event.getSource().equals(btnChangeIcon)) {
            choosePictureFromDialog(this, (fileChooser) -> refreshIcon(fileChooser.getSelectedFile().getAbsolutePath()));
        }
    }

    private void refreshIcon(String iconPath) {
        String newIconPath = iconPath == null ? basicIcon : iconPath;
        lbPath.setText(newIconPath);
        btnChangeIcon.setIcon(new LoadedIcon(newIconPath, DEFAULT_ICON_SIZE).getIcon());
    }

    public void updateBarcodeOptions(BarcodeModel options) {
        compBarcodes.getComponent().setModel(options);
    }

    private boolean isNameValid() {
        return !compName.getComponent().getText().isBlank();
    }

    private boolean isBarcodeSelected() {
        return compBarcodes.getComponent().getSelectedItem() != null;
    }
}
