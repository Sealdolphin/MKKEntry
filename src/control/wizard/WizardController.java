package control.wizard;

import data.DataModel;
import data.wizard.WizardType;
import view.main.panel.wizard.entryprofile.DataListView;

import java.awt.event.ActionEvent;

public class WizardController<T extends WizardType> {

    private final DataModel<T> wizardData;
    private final Wizard wizard;

    public WizardController(DataModel<T> wizardData, Wizard wizard) {
        this.wizardData = wizardData;
        this.wizard = wizard;
    }

    public DataListView<T> createView() {
        return new DataListView<>(wizardData, wizard, this);
    }

    public void createNew(ActionEvent event) {

    }

    public void removeData(ActionEvent event) {
        wizardData.deleteSelected();
    }

}
