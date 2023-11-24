package control.modifier;

import control.wizard.WizardEditor;
import data.modifier.Barcode;
import view.main.panel.wizard.barcode.BarcodeWizard;

import javax.swing.*;

public class BarcodeEditor extends WizardEditor<Barcode> {

    public BarcodeEditor(Barcode data) {
        super(data);
    }

    @Override
    public JPanel createView() {
        return new BarcodeWizard(this).getPanel();
    }

    @Override
    public Barcode createNew() {
        return new Barcode(data);
    }
}
