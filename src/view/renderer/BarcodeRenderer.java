package view.renderer;

import data.modifier.Barcode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import static view.renderer.RenderedIcon.BARCODE;
import static view.renderer.RenderedIcon.UNDER_EDIT;

public class BarcodeRenderer extends JPanel implements ListCellRenderer<Barcode> {

    private final JLabel lbName;
    private final JLabel lbDescription;
    private Barcode barcodeUnderEdit = null;

    public BarcodeRenderer() {
        setOpaque(true);
        lbName = new JLabel();
        lbDescription = new JLabel();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBorder(new EmptyBorder(2, 2, 2, 2));

        lbName.setIcon(BARCODE.getImage());
        lbName.setFont(new Font("Arial", Font.BOLD, 22));
        lbDescription.setFont(new Font("Arial", Font.PLAIN, 12));

        add(lbName);
        add(lbDescription);
    }

    public void setBarcodeUnderEdit(Barcode barcode) {
        barcodeUnderEdit = barcode;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Barcode> list, Barcode barcode, int index, boolean isSelected, boolean cellHasFocus) {
        lbName.setText(barcode.getName());
        lbDescription.setText(barcode.getDescription());

        if (isSelected) {
            if (barcode.equals(barcodeUnderEdit)) {
                lbName.setIcon(UNDER_EDIT.getImage());
                lbName.setText("<Üres>");
                lbDescription.setText("Szerkesztés alatt...");
                setBackground(Color.ORANGE);
                setForeground(list.getSelectionForeground());
            } else {
                lbName.setIcon(BARCODE.getImage());
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            }
        } else {
            lbName.setIcon(BARCODE.getImage());
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        return this;
    }
}
