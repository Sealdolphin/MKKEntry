package view.main.panel.wizard.entryprofile;

import control.wizard.WizardEditor;
import data.DataModel;
import data.entryprofile.EntryProfile;
import data.modifier.Barcode;
import data.modifier.TicketType;
import data.wizard.BarcodeModel;
import data.wizard.TicketTypeModel;
import view.main.panel.AbstractPanel;
import view.main.panel.wizard.ListUpdateListener;
import view.main.panel.wizard.WizardPage;
import view.validation.ComponentValidator;

import javax.swing.*;

import static data.entry.EntryCommand.getDefaultCommands;

public class EntryProfileMainPanel extends AbstractPanel implements WizardPage<EntryProfile> {

    private final EntryProfileDefaultPanel topPanel;
    private final EntryProfileCommandPanel commandPanel;
    private final EntryProfileSettingsPanel settingsPanel;
    private final JScrollPane scrollWheel;

    public EntryProfileMainPanel() {
        this(new TicketTypeModel(), new BarcodeModel());
    }

    public EntryProfileMainPanel(DataModel<TicketType> ticketTypes, DataModel<Barcode> barcodes) {
        super(true, false);

        topPanel = new EntryProfileDefaultPanel();
        // TODO: get default commands from somewhere (schema or have default commands)
        commandPanel = new EntryProfileCommandPanel(getDefaultCommands());
        settingsPanel = new EntryProfileSettingsPanel();

        commandPanel.listUpdated(barcodes);
        settingsPanel.listUpdated(ticketTypes);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
        contentPane.add(topPanel);
        topPanel.setAlignmentX(LEFT_ALIGNMENT);
        createSection(contentPane, "Parancsok testreszabása:", commandPanel);
        createSection(contentPane, "Szabályok:", settingsPanel);

        scrollWheel = new JScrollPane(contentPane);
    }

    private void createSection(JPanel contentPane, String name, JPanel sectionPanel) {
        JLabel lbSectionName = new JLabel(name);
        JSeparator separator = new JSeparator();

        separator.setAlignmentX(LEFT_ALIGNMENT);
        lbSectionName.setAlignmentX(LEFT_ALIGNMENT);
        sectionPanel.setAlignmentX(LEFT_ALIGNMENT);

        contentPane.add(lbSectionName);
        contentPane.add(separator);
        contentPane.add(sectionPanel);
    }

    public ListUpdateListener<Barcode> asBarcodeUpdater() {
        return commandPanel;
    }

    public ListUpdateListener<TicketType> asTicketTypeUpdater() {
        return settingsPanel;
    }

    @Override
    public void initializeLayout() {
        topPanel.initializeLayout();
        commandPanel.initializeLayout();
        settingsPanel.initializeLayout();

        setSingleComponentLayout(scrollWheel);
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
        //FIXME: this does not seem valid :(
        return new JPanel();
    }

    @Override
    public AbstractPanel getWizardEditPanel() {
        return this;
    }
}
