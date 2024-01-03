package control.wizard;

import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionEvent;

public interface Wizard {

    void selectElement(ListSelectionEvent event);

    void handleUserAction(ActionEvent event);

    void createNewElement();

    void deleteElement();

    boolean updateElement();
}
