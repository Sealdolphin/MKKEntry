package view.main.panel.utility;

import javax.swing.*;
import java.awt.*;

public class JColoredPanel extends JPanel {

    private Color backgroundColor;

    public JColoredPanel(Color backgroundColor, int size) {
        this.backgroundColor = backgroundColor;
        setBackground(backgroundColor);
        setMinimumSize(new Dimension(size, size));
        setMaximumSize(new Dimension(size, size));
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    };

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        setBackground(backgroundColor);
    }
}
