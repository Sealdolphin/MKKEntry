package data.wizard;

import data.modifier.Barcode;
import view.renderer.list.BarcodeListRenderer;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class BarcodeModel extends DefaultWizardModel<Barcode> {

    private final BarcodeListRenderer listRenderer;

    public BarcodeModel() {
        this(new ArrayList<>());
    }

    public BarcodeModel(List<Barcode> barcodes) {
        super(barcodes);
        this.listRenderer = new BarcodeListRenderer();
    }

    public ListCellRenderer<Barcode> createListRenderer() {
        return listRenderer;
    }

    @Override
    protected void updateRenderers(Barcode barcode) {
        listRenderer.updateRenderer(barcode);
    }

    @Override
    protected Barcode castModelData(Object data) {
        Barcode modelData = null;
        if (data instanceof Barcode barcode) {
            modelData = barcode;
        }
        return modelData;
    }

    @Override
    public DefaultWizardModel<Barcode> copyList() {
        return new BarcodeModel(new ArrayList<>(dataList));
    }
}
