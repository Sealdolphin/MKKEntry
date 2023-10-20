package view.main.panel.wizard;

import data.wizard.WizardType;
import view.main.panel.AbstractPanel;

import java.awt.event.ActionEvent;

public abstract class AbstractWizard<T extends WizardType> implements Wizard {

    protected T model;
    protected WizardPage<T> wizardPage;

    protected AbstractWizard(T model) {
        this.model = model;
    }

    @Override
    public void cancelEditing(ActionEvent event) {
        System.out.println("CANCEL");
        wizardPage.refreshPage(model);
    }

    @Override
    public void doSaveEntity(ActionEvent event) {
        System.out.println("TRY SAVING");
        if (wizardPage.validateWizardPage()) {
            this.model = wizardPage.generateWizardType();
            System.out.println("SAVED");
        } else {
            System.out.println("UNSUCCESSFUL: VALIDATION FAILED");
        }
    }

    public abstract AbstractPanel getWizardPage();
}
