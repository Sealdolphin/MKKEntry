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
import static data.Entry.DataColumn.*;

public class AppData extends DefaultTableModel implements Serializable, DataModel<Entry>{

    private List<Entry> entryList = new ArrayList<>();
    private List<Transaction> transactionList = new ArrayList<>();
    private Entry lastSelectedEntry;

    /**
     * New option file creation with default settings
     */
    public AppData() {
        super(0,Entry.DataColumn.values().length);
        setColumnIdentifiers(Arrays.stream(Entry.DataColumn.values()).map(Entry.DataColumn::getName).toArray());
        System.out.println("a new AppData has been constructed");
    }

    public void clearData(){
        entryList.clear();
        transactionList.clear();
        dataVector.clear();
        lastSelectedEntry = null;
        fireTableDataChanged();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        Entry.DataColumn column = Entry.DataColumn.values()[columnIndex];
        switch (column){
            default: return String.class;   //Use default rendering (String)
            case DISCOUNTS: return Discount.class;  //Use custom rendering (DiscountRenderer)
            case ENTERED: return Boolean.class;   //Use checkbox (Boolean)
        }
    }

    @Override
    public Object getValueAt(int row, int columnIndex) {
        Entry.DataColumn column = Entry.DataColumn.values()[columnIndex];
        switch (column){
            default: return super.getValueAt(row, columnIndex);                                     //Return a string
            case PRICE: return entryList.get(row).getAllFees() + " Ft";
            case DISCOUNTS: return entryList.get(row).getDiscounts();                               //Return discount list
            case ENTERED: return Boolean.parseBoolean(entryList.get(row).isEntered().toString());   //Return entered state
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
        Entry data = entryList.stream().filter(entry -> entry.get(ID.ordinal()).equals(id)).findAny().orElse(null);
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
        //Check if data already exists
        Entry conflict = entryList.stream().filter(entry -> entry.get(0).equals(data.get(ID.ordinal()))).findAny().orElse(null);
        if(conflict != null){
            //Data exists so we just enter the guest:
            //CASE 4 out of 4
            conflict.Enter();
            lastSelectedEntry = conflict;
        } else {
            if(!data.hasInvalidID() && lastSelectedEntry != null && lastSelectedEntry.hasInvalidID()) {
                if(lastSelectedEntry.get(TYPE.ordinal()).equals("Előrendelt") && !lastSelectedEntry.isEntered()) {
                    //CASE 3 out of 4
                    //IF TRUE => they must be TYPE! so let them in!
                    //TODO: OMG this is forbidden!!!!! :(
                    lastSelectedEntry.set(ID.ordinal(), data.get(ID.ordinal()));
                    lastSelectedEntry.Enter();
                } else throw new IOException("A vendég már belépett egy másik kóddal, vagy nem megfelelő a jegytípus. (Ez a rekord illegális.)");
            } else {
                //CASE 1 out of 4
                entryList.add(data);
                addRow(data);
                lastSelectedEntry = data;
            }
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
    public void replaceData(Entry oldData, Entry newData) throws IOException {
        removeData(oldData);
        addData(newData);
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(entryList.toArray());
        out.writeObject(transactionList.toArray());
        out.writeObject(lastSelectedEntry);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException{
        setColumnIdentifiers(Arrays.stream(Entry.DataColumn.values()).map(Entry.DataColumn::getName).toArray());
        //Reading entries
        Object[] objs = (Object[]) in.readObject();
        entryList = new ArrayList<>();
        for (Object entryObj : objs) {
            entryList.add((Entry) entryObj);
        }
        //Reading transactions
        objs = (Object[]) in.readObject();
        transactionList = new ArrayList<>();
        for (Object entryObj : objs) {
            transactionList.add((Transaction) entryObj);
        }
        //Reading last selection
        lastSelectedEntry = (Entry) in.readObject();
    }

}
