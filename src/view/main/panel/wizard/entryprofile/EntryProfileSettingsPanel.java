package view.main.panel.wizard.entryprofile;

import data.DataModel;
import data.modifier.TicketType;
import data.wizard.TicketTypeModel;
import view.main.panel.AbstractPanel;
import view.main.panel.utility.LabeledComponent;
import view.main.panel.wizard.ListUpdateListener;
import view.renderer.list.TicketTypeListRenderer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.stream.Stream;

public class EntryProfileSettingsPanel extends AbstractPanel implements ListUpdateListener<TicketType>{

    private final LabeledComponent<JComboBox<TicketType>> compDefaultTicketType;
    private final LabeledComponent<JTextField> compCustomName;
    private final LabeledComponent<JSpinner> compNumberOfEntries;

    private final static int STEP_SIZE = 1;

    private final JCheckBox checkUniqueNameRequired;
    private final JCheckBox checkNoDuplicatesAtImport;
    private final JCheckBox checkNoUnknownTypeAtImport;

    public EntryProfileSettingsPanel() {
        JComboBox<TicketType> ticketTypeChooser = new JComboBox<>();
        ticketTypeChooser.setRenderer(new TicketTypeListRenderer());

        compDefaultTicketType = new LabeledComponent<>("Alapértelmezett jegytípus", ticketTypeChooser);
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


        layout.linkSize(
                SwingConstants.HORIZONTAL,
                Stream.of(compCustomName, compNumberOfEntries, compDefaultTicketType)
                        .map(LabeledComponent::getComponent)
                        .toArray(JComponent[]::new)
        );

    }

    private void toggleCustomNameEnabled(ActionEvent event) {
        compCustomName.getComponent().setEnabled(!checkUniqueNameRequired.isSelected());
    }

    public void updateTicketTypes(DataModel<TicketType> model) {
        if (model instanceof TicketTypeModel ticketTypes) {
            compDefaultTicketType.getComponent().setModel(ticketTypes);
        }
    }

    @Override
    public void listUpdated(DataModel<TicketType> model) {
        if (model instanceof TicketTypeModel ticketTypes) {
            compDefaultTicketType.getComponent().setModel(ticketTypes);
        }
    }
}
