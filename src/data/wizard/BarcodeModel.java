package data.wizard;

import data.DataModel;
import data.modifier.Barcode;
import view.renderer.BarcodeRenderer;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.ArrayList;
import java.util.List;

public class BarcodeModel implements DataModel<Barcode> {

    private final List<Barcode> barcodes;
    private Barcode selection = null;
    private Barcode editCache = null;
    private final List<ListDataListener> listeners;
    private final BarcodeRenderer renderer;

    public BarcodeModel() {
        this(new ArrayList<>());
    }

    public BarcodeModel(List<Barcode> barcodes) {
        this.barcodes = barcodes;
        this.renderer = new BarcodeRenderer();
        this.listeners = new ArrayList<>();
    }

    @Override
    public Barcode getElementById(String id) {
        return barcodes.stream()
                .filter(barcode -> barcode.getMetaData().equals(id))
                .findFirst()
                .orElse(null);
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
        selection = data;
        refreshEditCache(data, false);
    }

    @Override
    public void addData(Barcode data) {
        barcodes.add(data);
        int addedIdx = barcodes.size() - 1;
        refreshEditCache(data, true);
        listeners.forEach(l -> l.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, addedIdx, addedIdx)));
    }

    @Override
    public void removeData(Barcode data) {
        int removeIdx = barcodes.indexOf(data);
        barcodes.remove(data);
        refreshEditCache(data, true);
        listeners.forEach(l -> l.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, removeIdx, removeIdx)));
    }

    @Override
    public void updateSelected(Barcode data) {
        if (selection != null) {
            refreshEditCache(null, false);
            int selectedIdx = getSelectedIndex();
            barcodes.remove(selection);
            barcodes.add(selectedIdx, data);
            selection = data;
            listeners.forEach(l -> l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, selectedIdx, selectedIdx)));
        }
    }

    @Override
    public void deleteSelected() {
        if (selection != null) {
            removeData(selection);
        }
    }

    @Override
    public ListCellRenderer<Barcode> createRenderer() {
        return renderer;
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

    /**
     * Updates the Edit cache. This is a value that changes based on the signaling data
     * If the model have been changed, the cache must be updated (if it's a new data then it's updated, if the
     * data has been deleted, it's cleared). If the selection has been changed, the cached data must be deleted.
     * If the cached data has been saved, the cache must be cleared.
     * @param data the value that sent the cache signal
     * @param dataChanged if the model has been changed
     */
    private void refreshEditCache(Barcode data, boolean dataChanged) {
        if (editCache == null) {
            if (dataChanged) {
                editCache = data;
            }
        } else if (data == null) {
            editCache = null;
        } else if (editCache != data) {
            removeData(editCache);
        } else if (dataChanged) {
            editCache = null;
        }

        renderer.setBarcodeUnderEdit(editCache);
    }
}
