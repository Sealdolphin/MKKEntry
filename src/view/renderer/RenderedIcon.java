package view.renderer;

import view.main.panel.utility.LoadedIcon;

import javax.swing.*;

import static view.main.panel.utility.LoadedIcon.SYSTEM_DEFAULT_PATH;

public enum RenderedIcon {
    BARCODE(SYSTEM_DEFAULT_PATH + "barcode-solid.png"),
    TICKET_TYPE(SYSTEM_DEFAULT_PATH + "ticket-solid.png"),
    DISCOUNT(SYSTEM_DEFAULT_PATH + "tag-solid.png"),
    ENTRY_PROFILE(SYSTEM_DEFAULT_PATH + "id-card-solid.png"),
    UNDER_EDIT(SYSTEM_DEFAULT_PATH + "wrench-solid.png"),
    SAVE(SYSTEM_DEFAULT_PATH + "circle-check-regular.png"),
    CANCEL(SYSTEM_DEFAULT_PATH + "ban-solid.png");

    private final String imagePath;

    RenderedIcon(String imagePath) {
        this.imagePath = imagePath;
    }

    public ImageIcon getImage() {
        return new LoadedIcon(imagePath).getIcon();
    }

}
