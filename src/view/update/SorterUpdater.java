package view.update;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class SorterUpdater<T extends TableModel> implements DocumentListener {

    private final TableRowSorter<T> tableRowSorter;
    private final Supplier<String> filterSource;
    private final MenuElement filterElement;

    public SorterUpdater(TableRowSorter<T> tableRowSorter, Supplier<String> source, MenuElement filterMenu) {
        this.tableRowSorter = tableRowSorter;
        this.filterSource = source;
        this.filterElement = filterMenu;
    }

    private void updateRegexSorter() {
        List<MenuElement> subElements = Arrays.asList(filterElement.getSubElements());
        int[] indices = subElements.stream()
                .filter(element -> element instanceof JCheckBoxMenuItem filter && filter.getState())
                .mapToInt(subElements::indexOf)
                .toArray();
        try {
            RowFilter<TableModel, Object> rowFilter = RowFilter.regexFilter("(?i)" + filterSource.get(), indices);
            tableRowSorter.setRowFilter(rowFilter);
        } catch (java.util.regex.PatternSyntaxException ex) {
            System.out.println("WARNING: " + ex.getLocalizedMessage());
        }
    }

    public JCheckBoxMenuItem createMenuItem(String name) {
        JCheckBoxMenuItem newItem = new JCheckBoxMenuItem(name);
        newItem.addActionListener(l -> updateRegexSorter());
        return newItem;
    }

    @Override
    public void insertUpdate(DocumentEvent event) {
        updateRegexSorter();
    }

    @Override
    public void removeUpdate(DocumentEvent event) {
        updateRegexSorter();
    }

    @Override
    public void changedUpdate(DocumentEvent event) {
        updateRegexSorter();
    }
}