package control.wizard;

import control.modifier.DiscountEditor;
import data.DataModel;
import data.modifier.Discount;
import data.wizard.BarcodeModel;

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
            if (list.getSelectedValue() == null) {
                setSelection(null);
            }
        }
    }

    @Override
    protected Discount getNewElement() {
        return new Discount();
    }

    public void updateBarcodeOptions(BarcodeModel options) {
        if (selectionEditor instanceof DiscountEditor editor) {
            editor.setBarcodeOptions(options);
        }
    }
}
