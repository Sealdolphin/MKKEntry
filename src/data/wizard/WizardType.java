package data.wizard;

import control.wizard.WizardEditor;

public interface WizardType {

    String getId();
    WizardEditor<?> createWizard();
}
