package view.main.panel.wizard;

import control.wizard.AbstractWizard;
import data.DataModel;
import data.wizard.WizardType;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

public class DataListView<T extends WizardType> extends JPanel {

    private final JList<T> list;
    private final JButton btnAdd;
    private final JButton btnRemove;

    private final JButton btnImport;

    public DataListView(DataModel<T> model) {
        list = new JList<>(model);
        btnAdd = new JButton("Hozzáadás");
        btnAdd.setActionCommand(AbstractWizard.WizardCommands.ADD.name());
        btnAdd.addActionListener(this::selectNewData);

        btnRemove = new JButton("Törlés");
        btnRemove.setActionCommand(AbstractWizard.WizardCommands.DELETE.name());
        btnRemove.addActionListener(this::refreshDeleteBtn);

        btnImport = new JButton("Importálás");
        btnImport.setActionCommand(AbstractWizard.WizardCommands.IMPORT.name());

        refreshDeleteBtn(new ActionEvent(this, 0, AbstractWizard.WizardCommands.DELETE.name()));

        list.setCellRenderer(model.createListRenderer());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void refreshDeleteBtn(ActionEvent event) {
        if (event.getActionCommand().equals(AbstractWizard.WizardCommands.DELETE.name())) {
            btnRemove.setEnabled(list.getModel().getSize() > 0);
        }
    }

    public JPanel createListPanel() {
        return createListPanel(false);
    }

    public JPanel createListPanel(boolean importEnabled) {
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.add(new JScrollPane(list), BorderLayout.CENTER);
        listPanel.add(createEditPanel(importEnabled), BorderLayout.SOUTH);
        return listPanel;
    }

    private Box createEditPanel(boolean importEnabled) {
        Box editPanel = Box.createHorizontalBox();
        editPanel.add(Box.createGlue());
        editPanel.add(btnAdd);
        if (importEnabled) {
            editPanel.add(btnImport);
        }
        editPanel.add(btnRemove);
        editPanel.add(Box.createGlue());
        return editPanel;
    }

    public void setListSelectionListener(ListSelectionListener listener) {
        list.addListSelectionListener(listener);
    }

    public void setButtonActions(ActionListener listener) {
        btnAdd.addActionListener(listener);
        btnRemove.addActionListener(listener);
        btnImport.addActionListener(listener);
    }

    private void selectNewData(ActionEvent event) {
        if (event.getActionCommand().equals(AbstractWizard.WizardCommands.ADD.name())) {
            ListModel<T> model = list.getModel();
            int selectionIndex = model.getSize() - 1;
            list.setSelectedValue(model.getElementAt(selectionIndex), true);
            btnRemove.setEnabled(model.getSize() > 0);
        }
    }

    public void setListDoubleClickListener(MouseListener doubleClickListener) {
        list.addMouseListener(doubleClickListener);
    }
}
