package view.main.panel.wizard.barcode;

import control.wizard.WizardEditor;
import data.modifier.Barcode;
import data.wizard.WizardType;
import view.main.panel.wizard.AbstractWizard;

public class BarcodeWizard extends AbstractWizard<Barcode> {

    public BarcodeWizard(WizardEditor<Barcode> editor) {
        super(editor);
    }

    @Override
    public void createWizardPage() {
        if (wizardPage == null) {
            wizardPage = new BarcodePanel(validator);
        }
    }

    @Override
    public void changeModel(WizardType model) {
        setVisible(model != null);
        if (model instanceof Barcode barcodeModel) {
            wizardPage.refreshPage(null);
        }
    }
}
