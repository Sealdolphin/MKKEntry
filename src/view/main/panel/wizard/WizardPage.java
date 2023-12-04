package view.main.panel.wizard;

import control.wizard.WizardEditor;
import data.wizard.WizardType;
import view.main.panel.AbstractPanel;
import view.validation.ComponentValidator;

import javax.swing.*;

public interface WizardPage<T extends WizardType> {

    void refreshPage(T model);
    void saveData(WizardEditor<T> controller);
    void setupValidation(ComponentValidator validator);
    JComponent getObjectValidationComponent();
    AbstractPanel getWizardEditPanel();
}
