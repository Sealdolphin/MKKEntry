package view.main.interactive;

import javax.swing.*;

public class InteractiveJMenuItem extends JMenuItem implements EnableWatcher {

    public InteractiveJMenuItem(String name) {
        super(name);
    }

    @Override
    public void updateEnabled(boolean enabled) {
        setEnabled(enabled);
    }
}
