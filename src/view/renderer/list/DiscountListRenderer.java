package view.renderer.list;

import data.modifier.Discount;

import javax.swing.*;
import java.awt.*;

import static view.renderer.RenderedIcon.DISCOUNT;

public class DiscountListRenderer extends DefaultWizardTypeListRenderer<Discount> {
    public DiscountListRenderer() {
        super(DISCOUNT);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Discount> list, Discount discount, int index, boolean isSelected, boolean cellHasFocus) {
        lbName.setText(discount.getName());
        lbDescription.setText(String.format("Kedvezmény: %d", discount.getDiscount()));

        return super.getListCellRendererComponent(list, discount, index, isSelected, cellHasFocus);
    }
}
