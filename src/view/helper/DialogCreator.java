package view.helper;

import control.utility.file.ExtensionFilter;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public abstract class DialogCreator {

    public static JFileChooser getPictureChooser(){
        JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(new ExtensionFilter(new String[]{"png", "jpg", "jpeg", "bmp", "gif"}, "Minden Képfájl"));
        fc.addChoosableFileFilter(new ExtensionFilter(new String[]{"png"}, "Portable Network Graphics (.png)"));
        fc.addChoosableFileFilter(new ExtensionFilter(new String[]{"jpg", "jpeg"}, "Joint Photographic Experts Group (.jpg, .jpeg)"));
        fc.addChoosableFileFilter(new ExtensionFilter(new String[]{"bmp"}, "Bitmap (.bmp)"));
        fc.addChoosableFileFilter(new ExtensionFilter(new String[]{"gif"}, "Graphics Interchange Format (.gif)"));
        return fc;
    }

    public static JFileChooser getImportFileChooser() {
        JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(new ExtensionFilter(new String[]{"json"}, "JSON fájl"));
        return fc;
    }

    public static int openDialogWithAction(Component parent, JFileChooser dialog, Consumer<JFileChooser> action) {
        int dialogResult = dialog.showOpenDialog(parent);
        if (dialogResult == JFileChooser.APPROVE_OPTION) {
            action.accept(dialog);
        }
        return dialogResult;
    }

}
