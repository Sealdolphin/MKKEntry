package view.renderer;

import control.modifier.Transaction;

import javax.swing.*;
import java.awt.*;

public class TransactionRenderer extends JLabel implements ListCellRenderer<Transaction> {

    public TransactionRenderer(){
        setOpaque(true);    //Enable selection
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Transaction> list, Transaction transaction, int index, boolean isSelected, boolean cellHasFocus) {
        setText(transaction.toString());
        UIDefaults defaults = javax.swing.UIManager.getDefaults();

        //Set selection
        if (isSelected) {
            setBackground(defaults.getColor("List.selectionBackground"));
            setForeground(defaults.getColor("List.selectionForeground"));
        } else {
            setBackground(Color.WHITE);
            setForeground(Color.BLACK);
        }

        setFont(new Font(Font.MONOSPACED, Font.ITALIC | Font.BOLD, 15));

        //Value coloring
        if(transaction.getValue() > 0) {
            if (isSelected) {
                setForeground(Color.GREEN);
            } else {
                setForeground(Color.GREEN.darker().darker());
            }
        } else {
            if (isSelected) {
                setForeground(Color.PINK);
            } else {
                setForeground(Color.RED.darker().darker());
            }
        }

        return this;
    }
}
