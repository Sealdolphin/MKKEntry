package view.main.panel.wizard.barcode;

import control.wizard.WizardEditor;
import data.modifier.Barcode;
import data.wizard.WizardType;
import view.main.panel.wizard.AbstractWizard;
import view.main.panel.wizard.barcode.panel.BarcodeEditPanel;

public class BarcodeWizard extends AbstractWizard<Barcode> {

    public BarcodeWizard(WizardEditor<Barcode> editor) {
        super(editor);
    }

    @Override
    public void createWizardPage() {
        if (wizardPage == null) {
            wizardPage = new BarcodeEditPanel(validator);
        }
    }

    @Override
    public void changeModel(WizardType model) {
        setVisible(model != null);
        if (model instanceof Barcode barcodeModel) {
            wizardPage.refreshPage(barcodeModel);
        }
    }
}
