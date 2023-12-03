package view.renderer;

import view.main.panel.utility.LoadedIcon;

import javax.swing.*;

public enum RenderedIcon {
    BARCODE("Icons\\barcode-solid.png"),

    TICKET_TYPE("Icons\\ticket-solid.png"),
    UNDER_EDIT("Icons\\wrench-solid.png");

    private final String imagePath;

    RenderedIcon(String imagePath) {
        this.imagePath = imagePath;
    }

    public ImageIcon getImage() {
        return new LoadedIcon(imagePath).getIcon();
    }

}
