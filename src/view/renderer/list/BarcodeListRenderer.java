package view.renderer.list;

import data.modifier.Barcode;

import javax.swing.*;
import java.awt.*;

import static view.renderer.RenderedIcon.BARCODE;

public class BarcodeListRenderer extends DefaultWizardTypeListRenderer<Barcode> {

    public BarcodeListRenderer() {
        super(BARCODE);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Barcode> list, Barcode barcode, int index, boolean isSelected, boolean cellHasFocus) {
        if (barcode == null) {
            lbName.setText("Válassz egyet...");
            lbDescription.setText(null);
        } else {
            lbName.setText(barcode.getName());
            lbDescription.setText(barcode.getDescription());
        }

        return super.getListCellRendererComponent(list, barcode, index, isSelected, cellHasFocus);
    }
}
