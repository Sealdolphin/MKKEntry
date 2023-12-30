package view.main.panel.wizard.entryprofile;

import control.wizard.BarcodeWizard;
import control.wizard.DiscountWizard;
import control.wizard.TicketTypeWizard;
import control.wizard.WizardEditor;
import data.DataModel;
import data.entryprofile.EntryProfile;
import data.modifier.Barcode;
import data.modifier.TicketType;
import data.wizard.BarcodeModel;
import data.wizard.TicketTypeModel;
import view.main.panel.AbstractPanel;
import view.main.panel.wizard.WizardPage;
import view.renderer.RenderedIcon;
import view.validation.ComponentValidator;

import javax.swing.*;

public class EntryProfileWizardPane extends AbstractPanel implements WizardPage<EntryProfile> {

    private final EntryProfileMainPanel mainPanel;

    private JTabbedPane tabbedPane;

    public EntryProfileWizardPane() {
        this(new BarcodeModel(), new TicketTypeModel());
    }

    @Override
    public void initializeLayout() {
        mainPanel.initializeLayout();
    }

    public EntryProfileWizardPane(DataModel<Barcode> barcodes, DataModel<TicketType> ticketTypes) {
        mainPanel = new EntryProfileMainPanel(barcodes, ticketTypes);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Beállítások", mainPanel);

        setSingleComponentLayout(tabbedPane);
    }

    public void updateWizardListeners(BarcodeWizard barcodeWizard, TicketTypeWizard ticketTypeWizard, DiscountWizard discountWizard) {
        barcodeWizard.addListUpdateListener(mainPanel.asBarcodeUpdater());
        ticketTypeWizard.addListUpdateListener(mainPanel.asTicketTypeUpdater());

        remove(tabbedPane);
        tabbedPane = new JTabbedPane();
        setSingleComponentLayout(tabbedPane);

        tabbedPane.addTab("Beállítások", RenderedIcon.ENTRY_PROFILE.getImage(), mainPanel);
        tabbedPane.addTab("Vonalkódok", RenderedIcon.BARCODE.getImage(), barcodeWizard.getView());
        tabbedPane.addTab("Kedvezmények", RenderedIcon.DISCOUNT.getImage(), discountWizard.getView());
        tabbedPane.addTab("Jegytípusok", RenderedIcon.TICKET_TYPE.getImage(), ticketTypeWizard.getView());
    }

    @Override
    public void refreshPage(EntryProfile model) {

    }

    @Override
    public void saveData(WizardEditor<EntryProfile> controller) {

    }

    @Override
    public void setupValidation(ComponentValidator validator) {

    }

    @Override
    public JComponent getIdentifyingComponent() {
        return mainPanel;
    }

    @Override
    public AbstractPanel getWizardEditPanel() {
        return this;
    }
}
