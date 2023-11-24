package view.main.panel.wizard;

import data.wizard.WizardType;

public interface WizardPage<T extends WizardType> {

    void refreshPage(T model);

}
