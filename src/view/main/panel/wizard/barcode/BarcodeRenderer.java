package view.main.panel.wizard.barcode;

import data.modifier.Barcode;
import view.util.MKKIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class BarcodeRenderer extends JPanel implements ListCellRenderer<Barcode> {

    private final JLabel lbName;
    private final JLabel lbDescription;

    public BarcodeRenderer() {
        setOpaque(true);
        lbName = new JLabel();
        lbDescription = new JLabel();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBorder(new EmptyBorder(2, 2, 2, 2));

        lbName.setIcon(new MKKIcon("Icons\\barcode-solid.png").getIcon());
        lbName.setFont(new Font("Arial", Font.BOLD, 22));
        lbDescription.setFont(new Font("Arial", Font.PLAIN, 12));

        add(lbName);
        add(lbDescription);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Barcode> list, Barcode barcode, int index, boolean isSelected, boolean cellHasFocus) {
        lbName.setText(barcode.getName());
        lbDescription.setText(barcode.getDescription());

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        return this;
    }
}
