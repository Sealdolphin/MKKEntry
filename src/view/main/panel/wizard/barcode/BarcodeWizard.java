package view.main.panel.wizard.barcode;

import control.modifier.Barcode;
import view.main.panel.AbstractPanel;
import view.main.panel.wizard.AbstractWizard;
import view.main.panel.wizard.barcode.panel.BarcodeEditPanel;

public class BarcodeWizard extends AbstractWizard<Barcode> {

    public BarcodeWizard(Barcode model) {
        super(model);
    }

    @Override
    public AbstractPanel getWizardPage() {
        if (wizardPage == null) {
            wizardPage = new BarcodeEditPanel(this);
            wizardPage.refreshPage(model);
        }
        return (AbstractPanel) wizardPage;
    }
}
