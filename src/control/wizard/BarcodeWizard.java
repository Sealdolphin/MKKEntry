package control.wizard;

import data.modifier.Barcode;
import view.main.panel.wizard.WizardPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionEvent;

public class BarcodeWizard implements Wizard {

    private final WizardPanel<Barcode> view;
    private final WizardEditor<Barcode> selectionEditor;

    public BarcodeWizard() {
        this.selectionEditor = new Barcode().createWizard();
        this.view = new WizardPanel<>(this, selectionEditor);
    }

    @Override
    public void selectElement(ListSelectionEvent event) {
        if (event.getSource() instanceof JList<?> list) {
            if (list.getSelectedValue() instanceof Barcode selection) {
                selectionEditor.updateSelection(selection);
            }
        }
    }

    @Override
    public void cancelEditing(ActionEvent event) {
        selectionEditor.updateView();
    }

    @Override
    public void doSaveEntity(ActionEvent event) {
        selectionEditor.getView().saveData(selectionEditor);
    }

    @Override
    public JPanel getView() {
        return view;
    }
}
