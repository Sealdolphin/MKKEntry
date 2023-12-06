package view.main.panel.wizard.entryprofile;

import control.wizard.WizardEditor;
import data.entryprofile.EntryProfile;
import view.main.panel.AbstractPanel;
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
        super(true, false);

        topPanel = new EntryProfileDefaultPanel();
        // TODO: get default commands from somewhere (schema or have default commands)
        commandPanel = new EntryProfileCommandPanel(getDefaultCommands());
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
    public JComponent getObjectValidationComponent() {
        return new JPanel();
    }

    @Override
    public AbstractPanel getWizardEditPanel() {
        return new AbstractPanel() {
            @Override
            public void initializeLayout() {

            }
        };
    }
}
