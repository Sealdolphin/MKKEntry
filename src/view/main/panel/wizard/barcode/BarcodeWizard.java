package view.main.panel.wizard.barcode;

import control.modifier.Barcode;
import data.wizard.WizardType;
import view.main.panel.wizard.AbstractWizard;
import view.main.panel.wizard.barcode.panel.BarcodeEditPanel;

public class BarcodeWizard extends AbstractWizard<Barcode> {

    public BarcodeWizard() {

    }

    @Override
    public void createWizardPage() {
        if (wizardPage == null) {
            wizardPage = new BarcodeEditPanel(validator);
            wizardPage.refreshPage(model);
        }
    }

    @Override
    public void changeModel(WizardType model) {
        if (model instanceof Barcode barcodeModel) {
            wizardPage.refreshPage(barcodeModel);
        }
    }
}