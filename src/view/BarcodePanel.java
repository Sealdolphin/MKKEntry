package view;

import javax.swing.*;
import java.awt.*;

import static javax.swing.BoxLayout.PAGE_AXIS;

@Deprecated
public class BarcodePanel extends JPanel {

    public BarcodePanel(String imagePath, String tooltip) {
        setLayout(new BoxLayout(this, PAGE_AXIS));

        JLabel lbTooltip = new JLabel(tooltip);

        lbTooltip.setFont(new Font(lbTooltip.getFont().getName(),Font.PLAIN,20));
        lbTooltip.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(new ImagePanel(imagePath));
        add(lbTooltip);

    }


}
