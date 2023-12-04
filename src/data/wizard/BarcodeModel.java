package data.wizard;

import data.modifier.Barcode;
import view.renderer.list.BarcodeListRenderer;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class BarcodeModel extends DefaultWizardModel<Barcode> implements ComboBoxModel<Barcode> {

    private final BarcodeListRenderer listRenderer;

    public BarcodeModel() {
        this(new ArrayList<>());
    }

    public BarcodeModel(List<Barcode> barcodes) {
        super(barcodes);
        this.listRenderer = new BarcodeListRenderer();
    }

    @Override
    public Barcode getElementById(String id) {
        return dataList.stream()
                .filter(barcode -> barcode.getMetaData().equals(id))
                .findFirst()
                .orElse(null);
    }

    public ListCellRenderer<Barcode> createListRenderer() {
        return listRenderer;
    }

    @Override
    protected void updateRenderers(Barcode barcode) {
        listRenderer.updateRenderer(barcode);
    }

    @Override
    public void setSelectedItem(Object selection) {
        if (selection instanceof Barcode selectedBarcode) {
            setSelection(selectedBarcode);
        }
        if (selection == null) {
            setSelection(null);
        }
    }

    @Override
    public Object getSelectedItem() {
        return getSelectedData();
    }
}
