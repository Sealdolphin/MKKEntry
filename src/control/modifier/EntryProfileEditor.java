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
        if (data != null) {

            BarcodeWizard barcodeWizard = new BarcodeWizard(data.createBarcodeModel());
            DiscountWizard discountWizard = new DiscountWizard(data.createDiscountModel(), data.createBarcodeModel());
            TicketTypeWizard ticketTypeWizard = new TicketTypeWizard(data.createTicketTypeModel());

            barcodeWizard.addListUpdateListener(discountWizard);
            wizardPage.updateWizardListeners(barcodeWizard, ticketTypeWizard, discountWizard);

        }
    }

    @Override
    protected EntryProfile cacheEditData() {
//        return new EntryProfile(data);
        return data;
    }

    @Override
    protected EntryProfile loadBackEditCache() {
        // Do nothing
        return data;
    }

    /**
     * TODO: create setters for EntryProfile (including Entryprofile.ProfileSettings)
     */
}
