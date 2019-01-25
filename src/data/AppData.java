package data;


import control.Entry;

import javax.swing.table.DefaultTableModel;
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
        /*
        LOAD PROFILES FROM FILE...
        CREATE EVENT HANDLER
        CREATE CONTROLLER!!!
         */
        System.out.println("a new AppData has been constructed");
        System.out.println("creating new profile...");
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
}
