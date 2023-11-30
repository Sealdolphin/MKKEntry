package control.wizard;

import data.DataModel;
import data.modifier.Barcode;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;

public class BarcodeWizard extends AbstractWizard<Barcode> {

    public BarcodeWizard(DataModel<Barcode> barcodes) {
        super(barcodes, new Barcode().createWizard());
    }

    @Override
    public void selectElement(ListSelectionEvent event) {
        if (event.getSource() instanceof JList<?> list) {
            if (list.getSelectedValue() instanceof Barcode selection) {
                setSelection(selection);
            }
        }
    }

    @Override
    protected Barcode castSelectedElement(Object selection) {
        if (selection instanceof Barcode selectedBarcode) {
            return selectedBarcode;
        } else {
            return null;
        }
    }

    @Override
    protected Barcode getNewElement() {
        return new Barcode();
    }
}
