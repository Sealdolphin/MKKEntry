package view;

import control.modifier.Modifier;

import javax.swing.*;
import java.awt.*;

public class ModifierValidationRenderer extends JLabel implements ListCellRenderer<Modifier> {

    public ModifierValidationRenderer(){
        setOpaque(true);    //Enable selection
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Modifier> jList, Modifier modifier, int index, boolean isSelected, boolean hasFocus) {

        setText(modifier.toString());
        UIDefaults defaults = javax.swing.UIManager.getDefaults();

        //Set selection
        if (isSelected) {
            setBackground(defaults.getColor("List.selectionBackground"));
            setForeground(defaults.getColor("List.selectionForeground"));
        } else {
            setBackground(Color.WHITE);
            setForeground(Color.BLACK);
        }

        //Validation coloring
        if(!modifier.validate()) setForeground(Color.RED);

        return this;
    }
}
