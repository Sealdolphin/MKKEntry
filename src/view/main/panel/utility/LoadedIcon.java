package view.main.panel.utility;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class LoadedIcon {

    private final ImageIcon icon;

    public static final String SYSTEM_DEFAULT_PATH = "Icons" + File.separator + "system" + File.separator;

    public static final int DEFAULT_IMAGE_SIZE = 32;

    public LoadedIcon(String filename) {
        this(filename, DEFAULT_IMAGE_SIZE);
    }

    public LoadedIcon(String filename, int size) {
        this(filename, size, size);
    }

    public LoadedIcon(String filename, int width, int height) {
        ImageIcon loadedIcon = new ImageIcon(filename);
        Image image = loadedIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        icon = new ImageIcon(image);
    }

    public ImageIcon getIcon() {
        return icon;
    }

}
