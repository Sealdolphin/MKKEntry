package control.modifier;

import control.utility.file.ExtensionFilter;
import view.ImagePanel;

import javax.swing.*;
import java.awt.*;

public abstract class ModifierDialog extends JDialog {

    JPanel body;
    JButton btnSave;
    int result = -1;

    public static GridBagConstraints setConstraints(int x, int y, int w, int h){
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.gridwidth = w;
        constraints.gridheight = h;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        return constraints;
    }

    ModifierDialog(Window parent, String strTitle){
        super(parent,strTitle,ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        //Create fields
        body = new JPanel();
        btnSave = new JButton("Mentés");
    }


    void finishDialog(Window parent){
        //Accept and Cancel buttons
        JButton btnCancel = new JButton("Mégse");
        btnCancel.addActionListener(e -> dispose());
        JPanel panelDialog = new JPanel();
        panelDialog.add(btnSave);
        panelDialog.add(btnCancel);
        add(body,BorderLayout.CENTER);
        add(panelDialog,BorderLayout.SOUTH);
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    int selectPicture(ImagePanel panelImg){
        JFileChooser fc = new JFileChooser();
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(new ExtensionFilter(new String[]{"png", "jpg", "jpeg", "bmp", "gif"}, "Minden Képfájl"));
        fc.addChoosableFileFilter(new ExtensionFilter(new String[]{"png"}, "Portable Network Graphics (.png)"));
        fc.addChoosableFileFilter(new ExtensionFilter(new String[]{"jpg", "jpeg"}, "Joint Photographic Experts Group (.jpg, .jpeg)"));
        fc.addChoosableFileFilter(new ExtensionFilter(new String[]{"bmp"}, "Bitmap (.bmp)"));
        fc.addChoosableFileFilter(new ExtensionFilter(new String[]{"gif"}, "Graphics Interchange Format (.gif)"));
        int dialogResult = fc.showOpenDialog(this);
        if (dialogResult == JFileChooser.APPROVE_OPTION) {
            panelImg.changePicture(fc.getSelectedFile().getAbsolutePath());
        }
        return dialogResult;
    }

    int open(){
        setVisible(true);
        while(true)
            if(!isVisible()) break;
        return result;
    }
}
