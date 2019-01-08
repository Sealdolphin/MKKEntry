package Control;

import Control.EntryModifier.TicketType;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * The Table holding the entries
 * The database is integrated into the table model for convenience.
 * @author Márk Mihalovits
 */
public class EntryTable extends AbstractTableModel {

    /**
     * The list of the current entries in the Application
     */
    private List<Entry> listOfEntries;
    /**
     * The column names of the Table
     */
    private String[] columns = Entry.getColumnNames();
    /**
     * The default Ticket type for new entries
     */
    private TicketType defaultType;

    /**
     * Constructor
     * Creates a new EntryTable with an empty database
     * @param defaultType the default ticket type for new entries
     */
    EntryTable(TicketType defaultType){
        this.defaultType = defaultType;
        listOfEntries = new ArrayList<>();
    }

    /**
     * Creates a stream from the entry list.
     * @return the stream created from the entry list.
     */
    Stream<Entry> stream() {
        return listOfEntries.stream();
    }

    /**
     * Adds a new entry to the list
     * @param guest the new entry
     */
    void addEntry(Entry guest) {
        listOfEntries.add(guest);
    }

    /**
     * Removes a guest currently in the list if it contains
     * @param guest the entry to be removed
     */
    void removeEntry(Entry guest) {
        listOfEntries.remove(guest);
    }

    /**
     * Inherited from AbstractTableModel
     * @return the number of rows in the table
     */
    @Override
    public int getRowCount() {
        return listOfEntries.size();
    }

    /**
     * Inherited from AbstractTableModel
     * @return the number of columns in the table
     */
    @Override
    public int getColumnCount() {
        return columns.length;
    }

    /**
     * Inherited from AbstractTableModel
     * @param columnIndex the index of a column
     * @return the name of the indexed column
     */
    @Override
    public String getColumnName(int columnIndex) {
        if(columnIndex < columns.length)
            return columns[columnIndex];
        return null;
    }

    /**
     * Inherited from AbstractTableModel
     * @param columnIndex the index of a column
     * @return the class associated with the data type in the indexed column
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return new Entry(defaultType).getValue(columnIndex).getClass();
    }

    /**
     * Inherited from AbstractTableModel
     * @param rowIndex the index of a row
     * @param columnIndex the index of a column
     * @return the display value of an Entry in the indexed cell
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(columnIndex < columns.length && rowIndex < listOfEntries.size())
            return listOfEntries.get(rowIndex).getValue(columnIndex);
        return null;
    }
}
