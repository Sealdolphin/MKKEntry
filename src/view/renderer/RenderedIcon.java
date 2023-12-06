package view.renderer;

import view.main.panel.utility.LoadedIcon;

import javax.swing.*;
import java.io.File;

public enum RenderedIcon {
    BARCODE("Icons" + File.separator + "barcode-solid.png"),
    TICKET_TYPE("Icons" + File.separator + "ticket-solid.png"),
    DISCOUNT("Icons" + File.separator + "tag-solid.png"),
    ENTRYPROFILE("Icons" + File.separator + "id-card-solid.png"),
    UNDER_EDIT("Icons" + File.separator + "wrench-solid.png");

    private final String imagePath;

    RenderedIcon(String imagePath) {
        this.imagePath = imagePath;
    }

    public ImageIcon getImage() {
        return new LoadedIcon(imagePath).getIcon();
    }

}
