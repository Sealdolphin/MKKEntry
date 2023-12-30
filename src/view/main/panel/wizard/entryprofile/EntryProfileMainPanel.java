package view.main.panel.wizard.entryprofile;

import data.DataModel;
import data.modifier.Barcode;
import data.modifier.TicketType;
import view.main.panel.AbstractPanel;
import view.main.panel.wizard.ListUpdateListener;

import javax.swing.*;

import static data.entry.EntryCommand.getDefaultCommands;

public class EntryProfileMainPanel extends AbstractPanel {

    private final EntryProfileDefaultPanel topPanel;
    private final EntryProfileCommandPanel commandPanel;
    private final EntryProfileSettingsPanel settingsPanel;
    private final JScrollPane scrollWheel;

    public EntryProfileMainPanel(DataModel<Barcode> barcodes, DataModel<TicketType> ticketTypes) {
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
}
