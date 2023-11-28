package view.main.panel.wizard.entryprofile;

import control.wizard.Wizard;
import control.wizard.WizardController;
import data.DataModel;
import data.wizard.WizardType;

import javax.swing.*;
import java.awt.*;

public class DataListView<T extends WizardType> extends JPanel {

    private final JList<T> list;

    private final JButton btnAdd;

    private final JButton btnRemove;

    public DataListView(DataModel<T> model, Wizard wizard, WizardController<T> controller) {
        list = new JList<>(model);
        btnAdd = new JButton("Hozzáadás");
        btnRemove = new JButton("Törlés");

        setLayout(new BorderLayout());
        add(createListPanel(), BorderLayout.CENTER);
        add(wizard.getView(), BorderLayout.EAST);

        list.setCellRenderer(model.createRenderer());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(wizard::selectElement);
        list.addListSelectionListener(event -> model.setSelection(list.getSelectedValue()));

        btnAdd.addActionListener(controller::createNew);
        btnRemove.addActionListener(controller::removeData);
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
}
