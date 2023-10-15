package view.main.panel;

import data.util.ReadingFlag;
import view.main.interactive.ReadFlagListener;

import javax.swing.*;
import java.awt.*;

/**
 * An information panel at the bottom of the screen.
 * It reacts to the barcode reading operations and behaves correctly.
 * Shows the current state of reading, with color codes.
 */
public class InfoPanel extends AbstractPanel implements ReadFlagListener {

    private final JLabel lbInfo;

    public InfoPanel() {
        super(false, false);
        lbInfo = new JLabel();

        lbInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbInfo.setFont(new Font(DEFAULT_FONT, Font.BOLD,FONT_SIZE_MEDIUM));
        //FIXME: Remove this!!!
        readingFlagChanged(ReadingFlag.FL_DEFAULT);
    }

    @Override
    public void readingFlagChanged(ReadingFlag flag) {
        System.out.println("FLAG READ: " + flag.toString());
        lbInfo.setText(flag.getInfo());
        lbInfo.setForeground(flag.getTextColor());
        setBackground(flag.getColor());
    }

    @Override
    public void initializeLayout() {
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGap(0,0,Short.MAX_VALUE)
                        .addComponent(lbInfo)
                        .addGap(0,0,Short.MAX_VALUE)
        );
        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGap(0,0,Short.MAX_VALUE)
                        .addComponent(lbInfo)
                        .addGap(0,0,Short.MAX_VALUE)
        );
    }
}
