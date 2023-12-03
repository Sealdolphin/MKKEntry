package view.main.panel.wizard;

import control.wizard.AbstractWizard;
import data.DataModel;
import data.wizard.WizardType;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DataListView<T extends WizardType> extends JPanel {

    private final JList<T> list;

    private final JButton btnAdd;

    private final JButton btnRemove;

    public DataListView(DataModel<T> model, WizardEditPanel<T> wizardEditPanel) {
        list = new JList<>(model);
        btnAdd = new JButton("Hozzáadás");
        btnAdd.setActionCommand(AbstractWizard.WizardCommands.ADD.name());
        btnAdd.addActionListener(this::selectNewData);

        btnRemove = new JButton("Törlés");
        btnRemove.setActionCommand(AbstractWizard.WizardCommands.DELETE.name());

        setLayout(new BorderLayout());
        add(createListPanel(), BorderLayout.CENTER);
        add(wizardEditPanel, BorderLayout.EAST);

        list.setCellRenderer(model.createListRenderer());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public JPanel createListPanel() {
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.add(new JScrollPane(list), BorderLayout.CENTER);
        listPanel.add(createEditPanel(), BorderLayout.SOUTH);
        return listPanel;
    }

    private Box createEditPanel() {
        Box editPanel = Box.createHorizontalBox();
        editPanel.add(Box.createGlue());
        editPanel.add(btnAdd);
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
    }

    private void selectNewData(ActionEvent event) {
        if (event.getActionCommand().equals(AbstractWizard.WizardCommands.ADD.name())) {
            ListModel<T> model = list.getModel();
            int selectionIndex = model.getSize() - 1;
            list.setSelectedValue(model.getElementAt(selectionIndex), true);
        }
    }
}
