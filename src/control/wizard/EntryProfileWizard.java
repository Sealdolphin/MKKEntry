package control.wizard;

import data.DataModel;
import data.entryprofile.EntryProfile;
import view.main.panel.wizard.WizardEditPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EntryProfileWizard extends AbstractWizard<EntryProfile> {

    public EntryProfileWizard(DataModel<EntryProfile> dataList) {
        super(dataList, new EntryProfile().createWizard());
        view.setListDoubleClickListener(new EditDialogCreator());
    }

    private void openEditDialog() {
        JDialog editDialog = new JDialog();
        WizardEditPanel<EntryProfile> panel = new WizardEditPanel<>(this, selectionEditor.getWizardPage(), validator);
        editDialog.setTitle("Belépőprofil szerkesztése");
        editDialog.add(panel);
        editDialog.pack();
        editDialog.setLocationRelativeTo(null);
        editDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        editDialog.setVisible(true);
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
        return view.createListPanel();
    }

    private class EditDialogCreator extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            if (mouseEvent.getSource() instanceof JList<?>) {
                if (mouseEvent.getClickCount() == 2) {
                    openEditDialog();
                }
            }
        }
    }
}
