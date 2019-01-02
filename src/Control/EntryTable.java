package Control;

import Control.Utility.ExportFilter;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class EntryTable extends AbstractTableModel {

    private List<Entry> listOfEntries;
    private String[] columns = Entry.getColumnNames();

    EntryTable(){
        listOfEntries = new ArrayList<>();
    }

    @Override
    public int getRowCount() {
        return listOfEntries.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        if(columnIndex < columns.length)
            return columns[columnIndex];
        return null;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return new Entry().getValue(columnIndex).getClass();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(columnIndex < columns.length && rowIndex < listOfEntries.size())
            return listOfEntries.get(rowIndex).getValue(columnIndex);
        return null;
    }

    Stream<Entry> stream() {
        return listOfEntries.stream();
    }

    void addEntry(Entry guest) {
        listOfEntries.add(guest);
    }

    void removeEntry(Entry guest) {
        listOfEntries.remove(guest);
    }

    String[] exportEntries(ExportFilter filter){
        ArrayList<String> lines = new ArrayList<>();

        for (Entry entry :
                listOfEntries) {
            String filteredEntry = filter.applyFilter(entry);
            if(filteredEntry != null)
                lines.add(filteredEntry);
        }
        
        return lines.toArray(new String[0]);
    }
}
