package control.wizard;

import data.DataModel;
import data.modifier.Barcode;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;

public class BarcodeWizard extends DefaultDataListWizard<Barcode> {

    public BarcodeWizard(DataModel<Barcode> barcodes) {
        super(barcodes, new Barcode().createWizard());
    }

    @Override
    public void selectElement(ListSelectionEvent event) {
        if (event.getSource() instanceof JList<?> list) {
            if (list.getSelectedValue() instanceof Barcode selection) {
                setSelection(selection);
            }
            if (list.getSelectedValue() == null) {
                setSelection(null);
            }
        }
    }

    @Override
    protected Barcode getNewElement() {
        return new Barcode();
    }
}
