package control.wizard;

import data.DataModel;
import data.entryprofile.EntryProfile;
import view.main.panel.wizard.DialogWizardView;
import view.main.panel.wizard.WizardEditPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;

public class EntryProfileWizard extends AbstractWizard<EntryProfile> {

    public EntryProfileWizard(DataModel<EntryProfile> dataList) {
        super(dataList, new EntryProfile().createWizard());
    }

    @Override
    protected EntryProfile getNewElement() {
        return new EntryProfile();
    }

    @Override
    public void selectElement(ListSelectionEvent event) {
        if (event.getSource() instanceof JList<?> list) {
            if (list.getSelectedValue() instanceof EntryProfile selection) {
                setSelection(selection);
            }
            if (list.getSelectedValue() == null) {
                setSelection(null);
            }
        }
    }

    @Override
    public JPanel getView() {
        WizardEditPanel<EntryProfile> editPanel = new WizardEditPanel<>(this, selectionEditor.getWizardPage(), validator);
        return new DialogWizardView(view, editPanel, "Belépőprofil szerkesztése");
    }
}
