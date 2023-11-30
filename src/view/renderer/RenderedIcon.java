package view.renderer;

import view.main.panel.utility.LoadedIcon;

import javax.swing.*;

public enum RenderedIcon {
    BARCODE("Icons\\barcode-solid.png"),
    UNDER_EDIT("Icons\\wrench-solid.png");

    private final String imagePath;

    RenderedIcon(String imagePath) {
        this.imagePath = imagePath;
    }

    ImageIcon getImage() {
        return new LoadedIcon(imagePath).getIcon();
    }

}
