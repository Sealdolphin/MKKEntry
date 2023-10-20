package view.main.panel.wizard;

import data.wizard.WizardType;

import javax.swing.*;
import java.awt.event.ActionEvent;

public interface Wizard {

    void cancelEditing(ActionEvent event);

    void doSaveEntity(ActionEvent event);

    JPanel getPanel();

    void changeModel(WizardType model);
}
