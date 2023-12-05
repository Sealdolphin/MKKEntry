package data.entry;

import data.DataModel;
import data.modifier.Discount;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppData extends DefaultTableModel implements Serializable, DataModel<Entry> {

    private List<Entry> entryList = new ArrayList<>();
    private Entry lastSelectedEntry;

    /**
     * New option file creation with default settings
     */
    public AppData() {
        super(0, DataColumn.values().length);
        setColumnIdentifiers(Arrays.stream(DataColumn.values()).map(DataColumn::getName).toArray());
    }

    public String generateNewID() {
        String newID;
        Entry conflict;
        do {
            newID = Integer.toString(1000 + (int)(Math.random() * (9999 - 5000) + 1));
            conflict = getElementById(newID);
        } while (conflict == null);
        return newID;
    }

    public void clearData(){
        entryList.clear();
        dataVector.clear();
        lastSelectedEntry = null;
        fireTableDataChanged();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        DataColumn column = DataColumn.values()[columnIndex];
        return switch (column) {
            default -> String.class;   //Use default rendering (String)
            case DISCOUNTS -> Discount.class;  //Use custom rendering (DiscountRenderer)
            case ENTERED -> Boolean.class;   //Use checkbox (Boolean)
        };
    }

    @Override
    public Object getValueAt(int row, int columnIndex) {
        DataColumn column = DataColumn.values()[columnIndex];
        return switch (column) {
            default -> super.getValueAt(row, columnIndex);                                     //Return a string
            case PRICE -> entryList.get(row).getAllFees() + " Ft";
            case DISCOUNTS -> entryList.get(row).getDiscounts();                               //Return discount list
            case ENTERED -> Boolean.parseBoolean(entryList.get(row).isEntered().toString());   //Return entered state
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Entry getElementAt(int index) {
        return entryList.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {

    }

    @Override
    public void removeListDataListener(ListDataListener l) {

    }

    @Override
    public Entry getElementById(String id) {
        return entryList.stream().filter(entry -> entry.get(DataColumn.ID.ordinal()).equals(id)).findAny().orElse(null);
    }

    @Override
    public Entry getSelectedData() {
        return lastSelectedEntry;
    }

    @Override
    public int getSelectedIndex(){
        return lastSelectedEntry != null ? entryList.indexOf(lastSelectedEntry) : -1;
    }

    @Override
    public void setSelection(Entry data) {
        lastSelectedEntry = data;
    }

    @Override
    public int getSize() {
        return entryList.size();
    }

    @Override
    public void addData(Entry data){
        Entry conflict = getElementById(data.get(DataColumn.ID.ordinal()));
        if(conflict != null){
            lastSelectedEntry = conflict;
        } else {
            entryList.add(data);
            addRow(data);
            lastSelectedEntry = data;
        }

    }

    @Override
    public void removeData(Entry data) {
        if(data == null) return;
        int index = entryList.indexOf(data);
        entryList.remove(data);
        removeRow(index);
        if(index > 0)
            lastSelectedEntry = entryList.get(index - 1);
        else if (entryList.size() > 0)
            lastSelectedEntry = entryList.get(0);
        else
            lastSelectedEntry = null;
    }

    @Override
    public void updateSelected(Entry data) {
        // Not implemented!
    }

    @Override
    public void deleteSelected() {
        // Not implemented!
    }

    @Override
    public ListCellRenderer<Entry> createListRenderer() {
        return null;
    }

    @Override
    public void replaceData(Entry oldData, Entry newData) throws IOException {
        removeData(oldData);
        addData(newData);
    }

    @Serial
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(entryList.toArray());
        out.writeObject(lastSelectedEntry);
    }

    @Serial
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException{
        setColumnIdentifiers(Arrays.stream(DataColumn.values()).map(DataColumn::getName).toArray());
        //Reading entries
        Object[] objs = (Object[]) in.readObject();
        entryList = new ArrayList<>();
        for (Object entryObj : objs) {
            entryList.add((Entry) entryObj);
        }
        //Reading transactions
        objs = (Object[]) in.readObject();
        //Reading last selection
        lastSelectedEntry = (Entry) in.readObject();
    }

}
