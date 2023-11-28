package view.util;

import javax.swing.*;
import java.awt.*;

public class MKKIcon {

    private final ImageIcon icon;

    public static final int DEFAULT_IMAGE_SIZE = 32;

    public MKKIcon(String filename) {
        this(filename, DEFAULT_IMAGE_SIZE);
    }

    public MKKIcon(String filename, int size) {
        this(filename, size, size);
    }

    public MKKIcon(String filename, int width, int height) {
        ImageIcon loadedIcon = new ImageIcon(filename);
        Image image = loadedIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        icon = new ImageIcon(image);
    }

    public ImageIcon getIcon() {
        return icon;
    }

}
