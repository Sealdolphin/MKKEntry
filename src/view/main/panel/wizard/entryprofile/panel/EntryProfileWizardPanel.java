package view.main.panel.wizard.entryprofile.panel;

import data.entry.EntryCommand;
import view.main.panel.AbstractPanel;

import javax.swing.*;
import java.util.List;

public class EntryProfileWizardPanel extends AbstractPanel {

    private final EntryProfileDefaultPanel topPanel;
    private final EntryProfileCommandPanel commandPanel;
    private final EntryProfileSettingsPanel settingsPanel;

    private final JScrollPane scrollWheel;

    public EntryProfileWizardPanel() {
        super(true, false);

        topPanel = new EntryProfileDefaultPanel();
        // TODO: get default commands from somewhere (schema or have default commands)
        commandPanel = new EntryProfileCommandPanel(List.of(
                new EntryCommand("FL_ENTRY", "Alapértelmezett", "MKK"),
                new EntryCommand("FL_LEAVE", "Kilépés", "LEAVE"),
                new EntryCommand("FL_DELETE", "Törlés", "DELETE")
        ));
        settingsPanel = new EntryProfileSettingsPanel();

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

    @Override
    public void initializeLayout() {
        topPanel.initializeLayout();
        commandPanel.initializeLayout();
        settingsPanel.initializeLayout();

        setSingleComponentLayout(scrollWheel);
    }

}
