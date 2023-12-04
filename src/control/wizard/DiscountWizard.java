package control.wizard;

import data.DataModel;
import data.modifier.Discount;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;

public class DiscountWizard extends AbstractWizard<Discount> {

    public DiscountWizard(DataModel<Discount> discounts) {
        super(discounts, new Discount().createWizard());
    }

    @Override
    public void selectElement(ListSelectionEvent event) {
        if (event.getSource() instanceof JList<?> list) {
            if (list.getSelectedValue() instanceof Discount selection) {
                setSelection(selection);
            }
        }
    }

    @Override
    protected Discount castSelectedElement(Object selection) {
        if (selection instanceof Discount selectedDiscount) {
            return selectedDiscount;
        } else {
            return null;
        }
    }

    @Override
    protected Discount getNewElement() {
        return new Discount();
    }
}
