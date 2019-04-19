package view;

import javax.swing.*;

import static javax.swing.BoxLayout.PAGE_AXIS;

public class BarcodePanel extends JPanel {

    public BarcodePanel() {
        setLayout(new BoxLayout(this, PAGE_AXIS));
    }
}
