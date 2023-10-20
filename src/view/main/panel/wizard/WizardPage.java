package view.main.panel.wizard;

import data.wizard.WizardType;

public interface WizardPage<T extends WizardType> {

    T generateWizardType();

    void refreshPage(T model);

}
