package view.main.panel.wizard.entryprofile;

import control.wizard.WizardEditor;
import data.DataModel;
import data.entry.EntryCommand;
import data.entryprofile.EntryProfile;
import data.modifier.Barcode;
import data.wizard.BarcodeModel;
import data.wizard.EntryProfileCommandList;
import view.main.panel.AbstractPanel;
import view.main.panel.utility.LabeledComponent;
import view.main.panel.wizard.ListUpdateListener;
import view.main.panel.wizard.WizardPage;
import view.renderer.list.BarcodeListRenderer;
import view.validation.ComponentValidator;

import javax.swing.*;
import java.util.List;

public class EntryProfileCommandPanel extends AbstractPanel implements WizardPage<EntryProfileCommandList>, ListUpdateListener<Barcode>, EntryProfilePagePart {

    private final List<LabeledComponent<JComboBox<Barcode>>> components;

    public EntryProfileCommandPanel(List<EntryCommand> commands) {
        components = commands.stream().map(this::createLabeledComponent).toList();
    }

    // Fixme: insert a barcode model here
    private LabeledComponent<JComboBox<Barcode>> createLabeledComponent(EntryCommand command) {
        JComboBox<Barcode> barcodeCombo = new JComboBox<>();
        barcodeCombo.setRenderer(new BarcodeListRenderer());
        return new LabeledComponent<>(command.toString(), barcodeCombo);
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

    }

    @Override
    public void saveData(WizardEditor<EntryProfileCommandList> controller) {

    }

    @Override
    public void setupValidation(ComponentValidator validator) {
        // TODO: do not allow two commands that are the same
    }

    @Override
    public JComponent getIdentifyingComponent() {
        return null;
    }

    @Override
    public AbstractPanel getWizardEditPanel() {
        return this;
    }

    @Override
    public void listUpdated(DataModel<Barcode> model) {
        if (model instanceof BarcodeModel barcodeModel) {
            components.forEach(command -> command.getComponent().setModel(barcodeModel.copyList()));
        }
    }

    @Override
    public void updateView(EntryProfile model) {
        // TODO: implement this
    }
}
