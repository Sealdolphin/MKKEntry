package view.main.panel.wizard;

import data.wizard.WizardType;

public interface WizardPage<T extends WizardType> {

    boolean validateWizardPage();

    T generateWizardType();

    void refreshPage(T model);

}
