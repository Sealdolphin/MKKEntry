package control.wizard;

import data.DataModel;
import data.wizard.WizardType;
import view.main.panel.wizard.DataListView;
import view.main.panel.wizard.WizardEditPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionEvent;

public abstract class AbstractWizard<T extends WizardType> implements Wizard {

    public enum WizardCommands {
        ADD,
        UPDATE,
        MOVE,
        DELETE,
        CANCEL
    }

    private final DataListView<T> view;
    private final WizardEditor<T> selectionEditor;
    protected final DataModel<T> dataList;

    protected AbstractWizard(DataModel<T> dataList, WizardEditor<T> selectionEditor) {
        this.selectionEditor = selectionEditor;
        this.view = new DataListView<>(dataList, new WizardEditPanel<>(this, selectionEditor.getView()));
        this.dataList = dataList;

        view.setListSelectionListener(this::selectElement);
        view.setButtonActions(this::handleUserAction);
    }

    @Override
    public void handleUserAction(ActionEvent event) {
        switch (WizardCommands.valueOf(event.getActionCommand())) {
            case ADD -> createNewElement();
            case MOVE -> {
                // TODO: implement this
            }
            case DELETE -> deleteElement();
            case UPDATE -> updateElement();
            case CANCEL -> selectionEditor.updateView();
            default -> {}
        }
    }

    @Override
    public void createNewElement() {
        T newData = getNewElement();
        dataList.addData(newData);
        setSelection(newData);
    }

    @Override
    public void selectElement(ListSelectionEvent event) {
        if (event.getSource() instanceof JList<?> list) {
            setSelection(castSelectedElement(list.getSelectedValue()));
        }
    }

    @Override
    public void deleteElement() {
        dataList.deleteSelected();
    }

    @Override
    public void updateElement() {
        T updatedData = selectionEditor.getSavedData();
        dataList.updateSelected(updatedData);
        view.invalidate();
    }

    protected void setSelection(T element) {
        dataList.setSelection(element);
        selectionEditor.updateSelection(element);
    }

    protected abstract T castSelectedElement(Object selection);

    protected abstract T getNewElement();

    public JPanel getView() {
        return view;
    }
}
