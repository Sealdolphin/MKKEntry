package view.main.panel.wizard.entryprofile;

import control.wizard.WizardController;
import data.DataModel;
import data.wizard.WizardType;
import view.main.panel.wizard.Wizard;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;

public class DataListView<T extends WizardType> extends JPanel {

    private final JList<T> list;

    private final Wizard wizard;

    private final JButton btnAdd;

    private final JButton btnRemove;

    public DataListView(DataModel<T> model, Wizard wizard, WizardController<T> controller) {
        list = new JList<>(model);
        btnAdd = new JButton("Hozzáadás");
        btnRemove = new JButton("Törlés");
        this.wizard = wizard;

        setLayout(new BorderLayout());
        add(createListPanel(), BorderLayout.CENTER);
        add(wizard.getPanel(), BorderLayout.EAST);

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(this::selectData);

        btnAdd.addActionListener(controller::createNew);
        btnRemove.addActionListener(controller::removeData);
        //wizard.changeModel(list.getSelectedValue());
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

    private void selectData(ListSelectionEvent event) {
        T selection = list.getSelectedValue();
        wizard.changeModel(selection);
    }

//    private void addData(ActionEvent event) {
//        // TODO: call wizard to create new data and select it too
//
//    }
//
//    private void removeData(ActionEvent event) {
//        // TODO: remove selection from data
//        T selection = list.getSelectedValue();
//
//        list.clearSelection();
//        wizard.changeModel(null);
//    }

    public void update(T updatedModel) {
        // TODO: not sure about that...
        // TODO: should get JList or list model as update!
        // TODO: should exist a type for wizard lists... EditableList
        //list.setListData();
    }


}
