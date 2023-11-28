package data.wizard;

import data.DataModel;
import data.modifier.Barcode;
import view.main.panel.wizard.barcode.BarcodeRenderer;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.ArrayList;
import java.util.List;

public class BarcodeModel implements DataModel<Barcode> {

    private final List<Barcode> barcodes;
    private Barcode selection = null;
    private final List<ListDataListener> listeners;

    public BarcodeModel() {
        this(new ArrayList<>());
    }

    public BarcodeModel(List<Barcode> barcodes) {
        this.barcodes = barcodes;
        listeners = new ArrayList<>();
    }

    @Override
    public Barcode getElementById(String id) {
        return barcodes.stream().filter(barcode -> barcode.getMetaData().equals(id)).findFirst().orElse(null);
    }

    @Override
    public Barcode getSelectedData() {
        return selection;
    }

    @Override
    public int getSelectedIndex() {
        if (selection != null) {
            return barcodes.indexOf(selection);
        }
        return -1;
    }

    @Override
    public void setSelection(Barcode data) {
        this.selection = data;
    }

    @Override
    public void addData(Barcode data) {
        barcodes.add(data);
        int addedIdx = barcodes.indexOf(data);
        listeners.forEach(l -> l.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, addedIdx, addedIdx)));
    }

    @Override
    public void removeData(Barcode data) {
        int removeIdx = barcodes.indexOf(data);
        barcodes.remove(data);
        listeners.forEach(l -> l.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, removeIdx, removeIdx)));
    }

    @Override
    public void updateSelection(Barcode data) {
        if (selection != null) {
            int selectedIdx = getSelectedIndex();
            barcodes.remove(selection);
            barcodes.add(selectedIdx, data);
            selection = data;
            listeners.forEach(l -> l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, selectedIdx, selectedIdx)));
        }
    }

    @Override
    public void deleteSelected() {
        removeData(selection);
    }

    @Override
    public ListCellRenderer<Barcode> createRenderer() {
        return new BarcodeRenderer();
    }

    @Override
    public void replaceData(Barcode oldData, Barcode newData) {
        // Not implemented
    }

    @Override
    public int getSize() {
        return barcodes.size();
    }

    @Override
    public Barcode getElementAt(int index) {
        return barcodes.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }
}
