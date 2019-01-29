package data;


import control.modifier.Discount;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static control.Application.uh;

public class AppData extends DefaultTableModel implements Serializable, DataModel<Entry>{

    private List<Entry> entryList = new ArrayList<>();
    private Entry lastSelectedEntry;

    /**
     * New option file creation with default settings
     */
    public AppData() {
        super(0,7);
        setColumnIdentifiers(Entry.columnNames);
        System.out.println("a new AppData has been constructed");
    }

    public void clearData(){
        entryList.clear();
        dataVector.clear();
        lastSelectedEntry = null;
        fireTableDataChanged();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex){
            default: return String.class;
            case 4: return Boolean.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        switch (column){
            default: return super.getValueAt(row, column);
            case 4: return Boolean.parseBoolean(entryList.get(row).isEntered().toString());
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Entry getDataByIndex(int index) {
        return entryList.get(index);
    }

    @Override
    public Entry getDataById(String id) {
        Entry data = entryList.stream().filter(entry -> entry.get(0).equals(id)).findAny().orElse(null);
        if(data != null) lastSelectedEntry = data;
        return data;
    }

    @Override
    public Entry getSelectedData() {
        return lastSelectedEntry;
    }

    @Override
    public int getSelectedIndex(){
        return entryList.indexOf(lastSelectedEntry);
    }

    @Override
    public void setSelection(Entry data) {
        lastSelectedEntry = data;
    }

    @Override
    public int getDataSize() {
        return entryList.size();
    }

    @Override
    public void addData(Entry data) throws IOException{
        Entry conflict = entryList.stream().filter(entry -> entry.get(0).equals(data.get(0))).findAny().orElse(null);
        if(conflict != null){
            if(conflict.isEntered()) throw new IOException(uh.getUIStr("ERR","DUPLICATE"));
            else conflict.Enter();
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
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(entryList.toArray());
        out.writeObject(lastSelectedEntry);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException{
        setColumnIdentifiers(Entry.columnNames);
        Object[] objs = (Object[]) in.readObject();
        entryList = new ArrayList<>();
        for (Object entryObj : objs) {
            entryList.add((Entry) entryObj);
        }
        lastSelectedEntry = (Entry) in.readObject();
    }

    @Override
    public void fireTableDataChanged() {
        super.fireTableDataChanged();
        System.out.println("[ENTRIES] LAST DATA: " + lastSelectedEntry);
    }
}
