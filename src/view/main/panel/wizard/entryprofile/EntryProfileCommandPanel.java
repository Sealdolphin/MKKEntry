package view.main.panel.wizard.entryprofile;

import control.wizard.WizardEditor;
import data.entry.EntryCommand;
import data.wizard.EntryProfileCommandList;
import view.main.panel.AbstractPanel;
import view.main.panel.utility.LabeledComponent;
import view.main.panel.wizard.WizardPage;

import javax.swing.*;
import java.util.List;

public class EntryProfileCommandPanel extends AbstractPanel implements WizardPage<EntryProfileCommandList> {

    private final List<LabeledComponent<JComboBox<String>>> components;

    public EntryProfileCommandPanel(List<EntryCommand> commands) {
        components = commands.stream().map(this::createLabeledComponent).toList();
    }

    private LabeledComponent<JComboBox<String>> createLabeledComponent(EntryCommand command) {
        return new LabeledComponent<>(command.toString(), new JComboBox<>(new String[]{"Test 1", "Test 2", "Test 3"}));
    }

    @Override
    public void initializeLayout() {
        // Horizontal layout
        GroupLayout.ParallelGroup horizontalGroup = layout.createParallelGroup();
        components.forEach(component -> horizontalGroup.addGroup(component.createSequentialLayout(layout)));

        // Vertical layout
        GroupLayout.SequentialGroup verticalGroup = layout.createSequentialGroup();
        components.forEach(component -> verticalGroup.addGroup(component.createParallelLayout(layout)));

        layout.setHorizontalGroup(horizontalGroup);
        layout.setVerticalGroup(verticalGroup);

        layout.linkSize(components.stream().map(LabeledComponent::getComponent).toArray(JComponent[]::new));
    }

    @Override
    public void refreshPage(EntryProfileCommandList model) {
        //TODO: add ticket types
        ComboBoxModel<String> ticketTypes = new DefaultComboBoxModel<>();
        components.forEach(labeledCommand -> labeledCommand.getComponent().setModel(ticketTypes));
    }

    @Override
    public void saveData(WizardEditor<EntryProfileCommandList> controller) {

    }
}
