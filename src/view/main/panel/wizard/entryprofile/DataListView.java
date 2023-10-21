package view.main.panel.wizard.entryprofile;

import data.wizard.WizardType;
import view.main.panel.wizard.Wizard;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;

public class DataListView<T extends WizardType> extends JPanel {

    private final JList<T> list;

    private final Wizard wizard;

    public DataListView(T[] model, Wizard wizard) {
        list = new JList<>(model);
        this.wizard = wizard;
        setLayout(new BorderLayout());

        add(list, BorderLayout.WEST);
        add(wizard.getPanel(), BorderLayout.EAST);

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(this::selectData);
    }

    private void selectData(ListSelectionEvent event) {
        T selection = list.getSelectedValue();
        wizard.changeModel(selection);
    }

    public void update(T[] updatedModel) {
        // TODO: not sure about that...
        // TODO: should get JList or list model as update!
        // TODO: should exist a type for wizard lists... EditableList
        list.setListData(updatedModel);
    }


}
