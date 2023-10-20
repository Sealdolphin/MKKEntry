package view.main.panel.wizard;

import java.awt.event.ActionEvent;

public interface Wizard {

    void cancelEditing(ActionEvent event);

    void doSaveEntity(ActionEvent event);
}
