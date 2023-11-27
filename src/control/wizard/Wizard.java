package control.wizard;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionEvent;

public interface Wizard {

    void selectElement(ListSelectionEvent event);

    void cancelEditing(ActionEvent event);

    void doSaveEntity(ActionEvent event);

    JPanel getView();
}
