package control.wizard;

import control.modifier.DiscountEditor;
import data.DataModel;
import data.modifier.Barcode;
import data.modifier.Discount;
import data.wizard.BarcodeModel;
import view.main.panel.wizard.ListUpdateListener;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;

public class DiscountWizard extends AbstractWizard<Discount> implements ListUpdateListener<Barcode> {

    public DiscountWizard(DataModel<Discount> discounts) {
        this(discounts, new BarcodeModel());
    }

    public DiscountWizard(DataModel<Discount> discounts, DataModel<Barcode> barcodeModel) {
        super(discounts, new Discount().createWizard());
        listUpdated(barcodeModel);
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

    @Override
    public void listUpdated(DataModel<Barcode> model) {
        if (model instanceof BarcodeModel barcodeModel) {
            updateBarcodeOptions(barcodeModel);
        }
    }
}
