package view.main.panel.wizard;

import javax.swing.*;
import java.awt.*;

public class AbstractWizardView extends JPanel {

    public AbstractWizardView(DataListView<?> view, WizardEditPanel<?> wizardEditPanel) {
        setLayout(new BorderLayout());
        add(view.createListPanel(), BorderLayout.CENTER);
        add(wizardEditPanel, BorderLayout.EAST);
    }

}
