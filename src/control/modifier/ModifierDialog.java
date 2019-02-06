package control.modifier;

import data.EntryProfile;

import javax.swing.*;
import java.awt.*;

public abstract class ModifierDialog extends JDialog {

    JPanel body;
    JButton btnSave;

    public static GridBagConstraints setConstraints(int x, int y, int w, int h){
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.gridwidth = w;
        constraints.gridheight = h;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        return constraints;
    }

    ModifierDialog(Window parent, EntryProfile profile, int index, String strTitle){
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

}
