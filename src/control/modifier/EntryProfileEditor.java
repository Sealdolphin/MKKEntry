package control.modifier;

import control.wizard.BarcodeWizard;
import control.wizard.DiscountWizard;
import control.wizard.TicketTypeWizard;
import control.wizard.WizardEditor;
import data.entryprofile.EntryProfile;
import view.main.panel.wizard.WizardPage;
import view.main.panel.wizard.entryprofile.EntryProfileWizardPane;

public class EntryProfileEditor extends WizardEditor<EntryProfile> {

    private final EntryProfileWizardPane wizardPage;

    public EntryProfileEditor(EntryProfile data, WizardPage<EntryProfile> view) {
        super(data, view);
        wizardPage = (EntryProfileWizardPane) view;
    }

    public void updateSelection(EntryProfile data) {
        this.data = data;
        if (data != null) {
            this.cachedEdit = cacheEditData();
        }

        updateWizards();
        updateView();
    }

    private void updateWizards() {
        if (cachedEdit != null) {

            BarcodeWizard barcodeWizard = new BarcodeWizard(cachedEdit.createBarcodeModel());
            DiscountWizard discountWizard = new DiscountWizard(cachedEdit.createDiscountModel());
            TicketTypeWizard ticketTypeWizard = new TicketTypeWizard(cachedEdit.createTicketTypeModel());

            discountWizard.listUpdated(cachedEdit.createBarcodeModel());

            wizardPage.updateWizardListeners(barcodeWizard, ticketTypeWizard, discountWizard);

        }
    }

    @Override
    protected EntryProfile cacheEditData() {
        return new EntryProfile(data);
    }

    @Override
    protected EntryProfile loadBackEditCache() {
        // TODO: implement load back

        return data;
    }

}
