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
import java.io.File;

public class DiscountPanel extends AbstractPanel implements WizardPage<Discount> {

    /**
     * The default option for icons
     */
    private static final String basicIcon = "Icons"+ File.separator +"BasicIcon.png";
    private final JLabel lbIcon;
    private final JLabel lbPath;
    private final LabeledComponent<JTextField> compName;
    private final LabeledComponent<JComboBox<Barcode>> compBarcodes;
    private final LabeledComponent<JSpinner> compDiscount;
    private final JCheckBox cbFree;
    private final JButton btnChangeIcon;

    public static final int DEFAULT_ICON_SIZE = 50;

    public DiscountPanel() {
        lbIcon = new JLabel(new LoadedIcon(basicIcon, DEFAULT_ICON_SIZE).getIcon());
        lbPath = new JLabel(basicIcon);
        compName = new LabeledComponent<>("Név:", new JTextField(TEXT_PANEL_DEFAULT_WIDTH));
        compBarcodes = new LabeledComponent<>("Vonalkód:", new JComboBox<>());
        compDiscount = new LabeledComponent<>("Kedvezmény:", new JSpinner(new SpinnerNumberModel(0, Short.MIN_VALUE,Short.MAX_VALUE,1)));
        cbFree = new JCheckBox("Ingyenes");
        btnChangeIcon = new JButton("Módosítás");

        compBarcodes.getComponent().setRenderer(new BarcodeListRenderer());
    }

    @Override
    public void refreshPage(Discount model) {
        compName.getComponent().setText(model.getName());
        cbFree.setSelected(model.isFree());
        compDiscount.getComponent().setValue(model.getDiscount());
        compBarcodes.getComponent().setSelectedItem(model.getBarcode());

        String iconPath = model.getIconPath();
        if (iconPath == null) {
            iconPath = basicIcon;
        }
        lbPath.setText(iconPath);
        lbIcon.setIcon(new LoadedIcon(iconPath, DEFAULT_ICON_SIZE).getIcon());
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
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(lbIcon)
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
                                        .addComponent(lbIcon)
                        )
                        .addComponent(cbFree)
                        .addGroup(compBarcodes.createSequentialLayout(layout))
        );

        layout.linkSize(compName.getComponent(), compDiscount.getComponent());
        layout.linkSize(SwingConstants.HORIZONTAL, lbPath, compName.getLabel());

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
