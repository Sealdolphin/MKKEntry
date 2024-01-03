package view.main.panel.utility;

import javax.swing.*;
import java.awt.*;

public class ColoredIcon implements Icon {

    private Color color;

    private final int width;

    private final int height;

    private static final Color DEFAULT_COLOR = Color.WHITE;

    private static final int DEFAULT_SIZE = 50;

    public ColoredIcon() {
        this(DEFAULT_COLOR);
    }

    public ColoredIcon(Color color) {
        this(color, DEFAULT_SIZE);
    }

    public ColoredIcon(Color color, int size) {
        this(color, size, size);
    }

    public ColoredIcon(Color color, int width, int height) {
        this.color = color;
        this.width = width;
        this.height = height;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Color rollback = g.getColor();
        g.setColor(color);
        g.fillRoundRect(x, y, width, height, width,height);
        g.setColor(rollback);
    }

    @Override
    public int getIconWidth() {
        return width;
    }

    @Override
    public int getIconHeight() {
        return height;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
