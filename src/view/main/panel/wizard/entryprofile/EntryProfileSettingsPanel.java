package view.main.panel.wizard.entryprofile;

import view.main.panel.AbstractPanel;
import view.main.panel.utility.LabeledComponent;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.stream.Stream;

public class EntryProfileSettingsPanel extends AbstractPanel {

    private final LabeledComponent<JComboBox<String>> compDefaultTicketType;
    private final LabeledComponent<JTextField> compCustomName;
    private final LabeledComponent<JSpinner> compNumberOfEntries;

    private final static int STEP_SIZE = 1;

    private final JCheckBox checkUniqueNameRequired;
    private final JCheckBox checkNoDuplicatesAtImport;
    private final JCheckBox checkNoUnknownTypeAtImport;

    public EntryProfileSettingsPanel() {
        compDefaultTicketType = new LabeledComponent<>("Alapértelmezett jegytípus", new JComboBox<>(new String[]{"Testjegytípus 1", "Testjegytípus 2"}));
        compCustomName = new LabeledComponent<>("Új rekord neve:", new JTextField(TEXT_PANEL_DEFAULT_WIDTH));
        compNumberOfEntries = new LabeledComponent<>("Belépések száma:", new JSpinner(new SpinnerNumberModel(0,0, Short.MAX_VALUE ,STEP_SIZE)));

        checkUniqueNameRequired = new JCheckBox("Új rekordoknak egyéni név kötelező");
        checkNoDuplicatesAtImport = new JCheckBox("Duplikációk eldobása importáláskor");
        checkNoUnknownTypeAtImport = new JCheckBox("Ismeretlen jegytípus eldobása importáláskor");

        checkUniqueNameRequired.addActionListener(this::toggleCustomNameEnabled);
    }

    @Override
    public void initializeLayout() {
        // Horizontal layout
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(checkNoDuplicatesAtImport)
                        .addComponent(checkNoUnknownTypeAtImport)
                        .addComponent(checkUniqueNameRequired)
                        .addGroup(compCustomName.createSequentialLayout(layout))
                        .addGroup(compDefaultTicketType.createSequentialLayout(layout))
                        .addGroup(compNumberOfEntries.createSequentialLayout(layout))
        );

        // Vertical layout
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(checkNoDuplicatesAtImport)
                        .addComponent(checkNoUnknownTypeAtImport)
                        .addComponent(checkUniqueNameRequired)
                        .addGroup(compCustomName.createParallelLayout(layout))
                        .addGroup(compDefaultTicketType.createParallelLayout(layout))
                        .addGroup(compNumberOfEntries.createParallelLayout(layout))
        );


        layout.linkSize(Stream.of(compCustomName, compNumberOfEntries, compDefaultTicketType).map(LabeledComponent::getComponent).toArray(JComponent[]::new));
    }

    private void toggleCustomNameEnabled(ActionEvent event) {
        compCustomName.getComponent().setEnabled(!checkUniqueNameRequired.isSelected());
    }
}
