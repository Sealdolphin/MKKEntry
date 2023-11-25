package view.main.panel.wizard;

import control.wizard.WizardEditor;
import data.wizard.WizardType;

public interface WizardPage<T extends WizardType> {

    void refreshPage(T model);

    void saveData(WizardEditor<T> controller);
}
