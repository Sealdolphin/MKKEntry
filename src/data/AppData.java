package data;


import control.Entry;

import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AppData extends DefaultTableModel implements Serializable, DataModel<Entry>{

    private List<Entry> entryList = new ArrayList<>();
    private Entry lastSelectedEntry;

    /**
     * New option file creation with default settings
     */
    public AppData() throws Exception {
        super(0,7);
        setColumnIdentifiers(Entry.columnNames);
        /*
        LOAD PROFILES FROM FILE...
        CREATE EVENT HANDLER
        CREATE CONTROLLER!!!
         */
        System.out.println("a new AppData has been constructed");
        System.out.println("creating new profile...");
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
        return null;
    }

    @Override
    public Entry getSelectedData() {
        return lastSelectedEntry;
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
    public void addData(Entry data) {
        entryList.add(data);
        addRow(data);
    }

    @Override
    public void removeData(Entry data) {
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
}
