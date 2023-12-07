package control.wizard;

import data.DataModel;
import data.wizard.WizardType;
import view.main.panel.wizard.AbstractWizardView;
import view.main.panel.wizard.DataListView;
import view.main.panel.wizard.WizardEditPanel;
import view.validation.ComponentValidator;

import javax.swing.*;
import java.awt.event.ActionEvent;

public abstract class AbstractWizard<T extends WizardType> implements Wizard {

    public enum WizardCommands {
        ADD,
        UPDATE,
        MOVE,
        DELETE,
        CANCEL
    }

    protected final DataListView<T> view;
    protected final WizardEditor<T> selectionEditor;
    protected final DataModel<T> dataList;
    private final ComponentValidator validator = new ComponentValidator();

    protected AbstractWizard(DataModel<T> dataList, WizardEditor<T> selectionEditor) {
        selectionEditor.getWizardPage().setupValidation(validator);
        selectionEditor.updateSelection(null);

        this.selectionEditor = selectionEditor;
        this.view = new DataListView<>(dataList);
        this.dataList = dataList;

        setupUniqueValidation();
        view.setListSelectionListener(this::selectElement);
        view.setButtonActions(this::handleUserAction);
    }

    private void setupUniqueValidation() {
        validator.addComponent(
                selectionEditor.getWizardPage().getIdentifyingComponent(),
                this::isEditedDataUnique,
                "Ez az elem már létezik!"
        );
    }

    private boolean isEditedDataUnique() {
        T cachedEdit = selectionEditor.doSaveData();
        T data = selectionEditor.data;
        return dataList.isUnique(cachedEdit, data);
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
    public void deleteElement() {
        dataList.deleteSelected();
        setSelection(null);
    }

    @Override
    public void updateElement() {
        if (selectionEditor.data != null  && validator.validate()) {
            T updatedData = selectionEditor.loadBackEditCache();
            dataList.updateSelected(updatedData);
            view.invalidate();
        }
    }

    protected void setSelection(T element) {
        validator.resetAll();
        dataList.setSelection(element);
        selectionEditor.updateSelection(element);
    }

    protected abstract T getNewElement();

    public JPanel getView() {
        WizardEditPanel<T> editPanel = new WizardEditPanel<>(this, selectionEditor.getWizardPage(), validator);
        return new AbstractWizardView(view, editPanel);
    }
}
