package data;


import control.modifier.Discount;

import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
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
            default: return String.class;   //Use default rendering (String)
            case 3: return Discount.class;  //Use custom rendering (DiscountRenderer)
            case 4: return Boolean.class;   //Use checkbox (Boolean)
        }
    }



    @Override
    public Object getValueAt(int row, int column) {
        switch (column){
            default: return super.getValueAt(row, column);                                  //Return a string
            case 3: return entryList.get(row).getDiscounts();                               //Return discount list
            case 4: return Boolean.parseBoolean(entryList.get(row).isEntered().toString()); //Return entered state
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
        return lastSelectedEntry != null ? entryList.indexOf(lastSelectedEntry) : -1;
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
        if(index > 0)
            lastSelectedEntry = entryList.get(index - 1);
        else if (entryList.size() > 0)
            lastSelectedEntry = entryList.get(0);
        else
            lastSelectedEntry = null;
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
